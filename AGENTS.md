# AGENTS.md

智能物业系统（和家云服务管理云平台）——基于 Spring Cloud 微服务架构的物业管理系统。本文件为 AI 代理提供本代码库的开发指引；详细设计文档见 `doc/design/`，技术栈说明见 `doc/TECH_STACK.md`，部署手册见 `deploy/DEPLOY.md`。

## 微服务架构（4 个服务）

| 服务 | 模块 | 端口 | 数据库 | 职责 |
|------|------|------|--------|------|
| smart-property-gateway | 网关 | 8000 | 无 | 路由、限流（Sentinel）、认证、Knife4j 聚合（`/doc.html`） |
| smart-property-system | 系统基础 | 8001 | smart_property_system | 认证授权、系统管理、文件管理、报表 |
| smart-property-property | 房产财务 | 8002 | smart_property_property | 房产、业主、租赁、收费、车位 |
| smart-property-operation | 运营管理 | 8003 | smart_property_operation | 工单、保洁绿化、消防保安、公告、消息 |

- `smart-property-common`：公共模块（core / redis / mybatis / security / log / mq / oss），被四个服务依赖。
- 技术栈：Java 25 · Spring Boot 4.0.0 · Spring Cloud 2025.1.2 · Spring Cloud Alibaba 2025.1.0.0 · Sa-Token 1.46.0 · MyBatis-Plus 3.5.6 · MySQL 9.7 · Redis 7.x · RabbitMQ 3.x · Nacos · MinIO。
- 鉴权：Sa-Token 统一认证——网关 `SaReactorFilter` 校验登录态与角色，服务端 `SaInterceptor` 二道防线 + `@SaCheckLogin/@SaCheckRole/@SaCheckPermission` 注解鉴权；会话存 Redis 共享；整合详情见 `doc/design/06-Sa-Token鉴权框架整合设计.md`，框架文档见 `doc/sa-token-doc-new/`。
- 跨服务调用（各服务不同库）：统一走 OpenFeign（`@EnableFeignClients` 已在各启动类开启，扫描各自 `remote` 包），经 Nacos 服务发现直连、不走网关。服务间无用户上下文（`X-User-Id` 等头由网关注入，Feign 直连没有），所需上下文（如 companyId）显式作参数传。先例：operation→system `FileRemoteClient`、system→property `PaymentRemoteClient`、operation→property `PropertyRemoteClient`（配 `FallbackFactory` 空结果兜底，远端故障不阻塞主流程）。property 的 `/api/v1/internal/property/names` 提供小区/楼宇/房号 id→名称批量解析，供各服务回填列表 VO 名称字段。

## 开发环境（macOS · Apple Silicon）

| 工具 | 安装方式 | 实际路径 | 版本 |
|------|----------|----------|------|
| JDK | SDKMAN (`sdk install java 25.0.4-tem`) | `~/.sdkman/candidates/java/25.0.4-tem` | 25.0.4 LTS (Temurin) |
| Maven | SDKMAN (`sdk install maven 3.9.16`) | `~/.sdkman/candidates/maven/current` | 3.9.16 |
| 中间件 | Docker Compose | `deploy/docker-compose.middleware.yml` | — |

- `JAVA_HOME` / `PATH` 由 SDKMAN 自动注入，无需手动设置环境变量；确认版本用 `sdk current java`。
- 中间件（MySQL 3306 / Redis 6379 / Nacos 8848+8080 / RabbitMQ 5672+15672 / MinIO 9000+9001）默认连 `127.0.0.1`，凭据见 `.env.example`：

```bash
cd deploy
docker compose -f docker-compose.middleware.yml up -d    # 启动中间件
bash init-db.sh all                                      # 初始化数据库（可选 system|property|operation）
```

## 常用命令

```bash
# 编译（CI / 快速验证用，不要顺手跑 spring-boot:run）
mvn -pl <module> -am -DskipTests compile

# 单测（单模块）
mvn -pl smart-property-system test

# 本地启动单个服务（后台跑，验证完必须停掉，见下方"端口防护"）
mvn -pl smart-property-system -am spring-boot:run > /tmp/sp-system.log 2>&1 &

# Docker Compose 全栈一键启动（4 业务 + 5 中间件）
cp .env.example .env && docker compose up -d
```

- 健康检查：`curl http://localhost:8001/actuator/health`（各服务同理，端口见上表）。
- 默认账号 `admin / admin123`；统一接口文档 `http://localhost:8000/doc.html`。
- 单测数据源由各服务 `src/test/resources/application-test.yml` 提供（远程 MySQL，非 H2），跑测试前确认可达。

## 端口防护（本地运行约定）

四个服务共用同一组端口，`mvn spring-boot:run` / IDE Run 之后**必须配套收尾**，否则下次启动报 `Port XXXX was already in use`：

1. 启动前确认端口空闲：`lsof -nP -iTCP:8000-8003 -sTCP:LISTEN || true`
2. `spring-boot:run` 在后台跑并轮询 `/actuator/health` 确认启动完成。
3. 调试完毕立即停止：`lsof -ti :8001 | xargs kill -9`，兜底 `pkill -f "spring-boot:run"`。
4. 只验证单服务时**只起那一个**，验证完杀掉。

## 代码规范（阿里巴巴 Java 开发手册）

- 分层：Controller → Service → Mapper，禁止跨层调用；领域模型分 DO / DTO / VO / Query。
- 命名：类 UpperCamelCase，方法/变量 lowerCamelCase，常量 UPPER_SNAKE_CASE。
- 异常：统一异常处理，5 位错误码（A 用户端 / B 系统端 / C 第三方）。
- 日志：SLF4J 占位符 `log.info("userId: {}", userId)`。

### 数据库

- 必备字段：`id`, `company_id`, `create_by`, `create_time`, `update_by`, `update_time`, `is_deleted`；每张业务表必须有 `company_id`（多租户）。
- 表前缀：`sys_` / `property_` / `finance_` / `operation_` / `admin_` / `report_` / `file_` / `auth_`；索引命名 `pk_` / `uk_` / `idx_`；金额 `decimal(10,2)`；布尔用 `is_xxx tinyint unsigned`。
- MyBatis-Plus：分页必须用 `PaginationInnerInterceptor`；简单 CRUD 用内置方法；复杂 SQL 写 XML 放 `resources/mapper/`；禁止 Java 拼接 SQL。
- 数据库操作仅限 `smart_property_*` 本地库；DROP / DELETE / TRUNCATE 前必须确认影响范围；禁止操作系统库。

### 接口

- RESTful，路径 `/api/v1/{模块}/{资源}`；响应统一 `{code, message, data}`；分页统一 `{total, pageNum, pageSize, pages, records}`。

### 公共工具（勿手写审计字段）

业务代码统一使用 `smart-property-common` 提供的工具，不要手写审计字段、时间格式、分页转换：

- `mybatis.handler.AutoFillHandler`：自动填充 createTime / updateTime / createBy / updateBy / isDeleted / companyId，业务仅手动 set createBy / updateBy。
- `core.util.PageUtils` / `JsonUtils` / `DateUtils`：分页转换、JSON、时间。
- `core.service.TenantGuard`：多租户越权校验；`core.context.SecurityContextHolder` + `security.filter.SecurityHeaderFilter`：当前操作人上下文。

## 安全要求

密码 BCrypt；敏感字段（手机号/身份证）AES 加密存储 + 脱敏展示；认证与鉴权统一由 Sa-Token 提供（网关 SaReactorFilter + 服务注解鉴权，Token 经 `Authorization: Bearer` 头传递，会话存 Redis）；Sentinel 限流（全局 1000 QPS，单用户 100 QPS）；AOP 审计日志。
