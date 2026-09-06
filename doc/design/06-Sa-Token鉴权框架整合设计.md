# Sa-Token 鉴权框架整合设计

> 状态：已落地（2026-09-06）
> 框架版本：Sa-Token 1.46.0（dev 分支，支持 Spring Boot 4）
> 官方文档：本仓库 `doc/sa-token-doc-new/`（克隆自 [dromara/Sa-Token](https://github.com/dromara/Sa-Token) dev 分支 sa-token-doc-new）

---

## 一、整合目标与结论

将原有「手写 JWT（jjwt）+ Redis 单点 Token 校验」方案整体替换为 Sa-Token 权限认证框架，
由框架统一提供：登录认证、会话管理、注解鉴权、路由拦截、踢人下线、顶人下线、Token 续期等能力。

**对外 API 契约保持不变**：前端仍以 `Authorization: Bearer <token>` 携带凭证，
登录 / 刷新 / 注销接口的出入参结构（LoginVO）不变，前端零改动。

## 二、依赖引入（遵循官方 micro/import-intro 规范）

> 官方要求：网关（Reactor）与内部服务（Servlet）**分开引入 starter，禁止父 pom dependencies 统一引入**；
> 父 pom 仅通过 BOM 做版本管理。

| 模块 | 依赖 | 说明 |
|------|------|------|
| 根 pom | `sa-token-bom 1.46.0`（dependencyManagement import） | 仅统一版本 |
| smart-property-common | `sa-token-spring-boot4-starter` + `sa-token-redis-template` | 业务服务（Servlet） |
| smart-property-gateway | `sa-token-reactor-spring-boot4-starter` + `sa-token-redis-template` | 网关（Reactor） |

会话数据经 `sa-token-redis-template` 存 Redis，网关与各服务共享（Redis 连接复用 `spring.data.redis` 配置）。
原 jjwt 依赖已全部移除。

## 三、配置（application-common.yml / gateway application.yml）

```yaml
sa-token:
  token-name: Authorization      # 框架从该请求头读取 token
  token-prefix: Bearer           # 自动剥离 `Bearer ` 前缀（兼容前端现有传参）
  timeout: 7200                  # 有效期 2h，与原 jwt.expiration 一致
  active-timeout: -1             # 不冻结
  auto-renew: true               # 活跃自动续期（滑动过期）
  is-concurrent: false           # 新登录挤掉旧登录（对齐原 auth:token:{userId} 单点语义）
  token-style: uuid
  is-read-cookie: false          # 前后端分离，仅读 header
  is-read-header: true
  is-log: true
```

Redis 中的实际键：`Authorization:login:token:{tokenValue}`、`Authorization:login:session:{loginId}`。

## 四、鉴权架构（三层防线）

```
前端 (Authorization: Bearer xxx)
        │
        ▼
[1] 网关 SaReactorFilter（SaTokenConfigure，WebFilter）
    ├── 白名单放行：login / captcha / sms-code / refresh / 文档
    ├── OPTIONS 预检放行
    ├── StpUtil.checkLogin()                ← Redis 校验登录态
    ├── SaRouter.match("/api/v1/system/**") → StpUtil.checkRole("admin")
    ├── 身份写入 SaStorage（exchange attributes）
    └── 失败 → HTTP 401/403 + {code:"A0301"/"A0302", message, timestamp}
        │
        ▼
[2] 网关 AuthFilter（GlobalFilter）
    └── 读取 SaStorage 身份 → 转发下游头 X-User-Id / X-Username / X-Company-Id
        （白名单请求无身份，原样放行；loginId 经 Redis 反序列化可能为 String，按 Object 处理）
        │
        ▼
[3] 业务服务（system / property / operation）
    ├── SaTokenConfigure（common）：SaInterceptor 二道防线
    │     ├── /api/** 白名单外 checkLogin（防绕过网关直连）
    │     ├── /api/v1/system/** checkRole("admin")
    │     └── 注解鉴权开启：@SaCheckLogin / @SaCheckRole / @SaCheckPermission
    ├── StpInterfaceImpl（common）：从 SaSession 读取 roles / permissions
    ├── SecurityHeaderFilter（common，不变）：X-User-Id 等 → SecurityContextHolder（业务代码零改动）
    └── GlobalExceptionHandler（common，新增）：NotLoginException→401/A0301，
        NotRoleException / NotPermissionException→403/A0302
```

> 注意：网关 GlobalFilter 中**不能直接调用 StpUtil**（reactor 上下文仅在 SaReactorFilter
> 同步块内可用，见官方 `SaTokenContextFilterForReactor` 实现），因此采用
> 「SaReactorFilter 鉴权 + SaStorage 传身份 + GlobalFilter 转发」的组合。

## 五、登录态与权限数据

### 5.1 登录（AuthServiceImpl.login）

1. 验证码 / 用户状态 / BCrypt 密码校验（流程不变）；
2. `StpUtil.login(userId)` 创建 Token 与会话（`is-concurrent=false`：新登录顶掉旧登录）；
3. 身份与权限写入 SaSession（Redis 共享）：
   - `username` / `companyId`：网关转发身份头的数据源；
   - `roles` / `permissions`：`StpInterfaceImpl`（common 与 gateway 各一份实现）读取的数据源；
4. `LoginVO.accessToken = StpUtil.getTokenValue()`，`expiresIn = StpUtil.getTokenTimeout()`；
   **Sa-Token 为单 Token 模型，refreshToken 返回同一 Token 值**以保持前端兼容。

### 5.2 续期（/auth/refresh）

`StpUtil.getLoginIdByToken(token)` 校验 Token 仍有效 → 用户状态复核 →
`StpUtil.renewTimeout(token, SaManager.getConfig().getTimeout())` 滑动续期 → 返回当前 Token。
Token 过期 / 被踢 / 被顶后 refresh 将返回 401 语义的业务异常，前端走重新登录。

### 5.3 注销与踢人

- `/auth/logout` → `StpUtil.logoutByTokenValue(token)`（框架级注销，Redis 登录态即删）；
- 新增管理端点 `POST /api/v1/system/users/{id}/kickout`（`@SaCheckRole("admin")`）→
  `StpUtil.kickout(userId)`，被踢端下次请求返回 401「已被踢下线」（`NotLoginException.KICK_OUT`）；
- 重新登录顶号时旧 Token 返回 401「已被顶下线」（`BE_REPLACED`）。

### 5.4 权限数据模型

角色 `roleKey`（如 `admin`）与权限码（菜单表 `perms`，管理员为 `*:*:*` 通配）在登录时快照进会话；
权限变更需重新登录生效（与原 JWT 方案一致）。Sa-Token 权限匹配支持 `*` 通配。

## 六、移除的历史实现

| 移除项 | 位置 |
|--------|------|
| `JwtUtils`（jjwt 封装） | common/security/util（删除，jjwt 依赖移除） |
| 网关手写 JWT 解析 + `auth:token:{userId}` Redis 单点校验 | gateway AuthFilter（重写） |
| `TOKEN_CACHE_KEY` Token 缓存读写 | system AuthServiceImpl（由框架 Redis 键替代） |
| Spring Security 过滤链（`@EnableWebSecurity` + permitAll 链） | 三个服务的 SecurityConfig（system 保留 PasswordEncoder；property/operation 整文件删除） |
| `spring-boot-starter-security` | common pom（改为仅 `spring-security-crypto` 提供 BCrypt） |
| `JWT_SECRET` 环境变量 / ENC 配置 | application-common.yml、gateway yml、.env.example |

## 七、验证记录（本机实测，2026-09-06）

中间件：`deploy/docker-compose.middleware.yml`（MySQL/Redis/Nacos/RabbitMQ/MinIO），`init-db` 后实测：

| 场景 | 结果 |
|------|------|
| 登录 `POST /api/v1/auth/login` | ✅ 00000，返回 uuid Token + expiresIn 7200 + roles/permissions |
| 无 Token 访问受保护接口 | ✅ HTTP 401 `{"code":"A0301","message":"未登录或登录已失效"}` |
| 带 Token 访问 user-info | ✅ 00000（网关校验 + 身份头转发 + 服务链路全通） |
| admin 路由 `/api/v1/system/users` | ✅ 00000（网关 checkRole + 服务二道防线均通过） |
| `/auth/refresh` 续期 | ✅ 00000，同一 Token 续期 7200s |
| 管理端 kickout | ✅ 被踢 Token 返回 HTTP 401「已被踢下线」 |
| 重新登录顶号 | ✅ 旧 Token 返回 401「已被顶下线」，新 Token 正常 |
| `mvn clean install`（-Dmaven.test.skip） | ✅ 全模块 BUILD SUCCESS |
| 鉴权链路模块（common/gateway/system）测试编译 | ✅ 通过 |

## 八、已知边界与后续建议

1. **property / operation 模块的单测**存在与本整合无关的历史编译错误（服务层已改返回 VO 而测试未跟上，共 20+ 文件），需单独安排修复；
2. 前端 `sp_refresh_token` 与 `sp_access_token` 现为同一值，仍可正常工作；后续前端可去掉 refresh 分支，依赖 `auto-renew` 滑动续期；
3. 权限/角色变更后需重新登录生效；如需实时生效，可在管理端变更角色后调用 `StpUtil.kickout(userId)` 强制重登；
4. Sa-Token 会话依赖 Redis，Redis 不可用时登录校验整体不可用（原方案为降级放行，新方案为 fail-closed，更安全）。
