# CLAUDE.md - 智能物业系统开发规范

## 项目概述

和家云服务管理云平台 - 基于Spring Cloud微服务架构的物业管理系统。

**设计文档位置**: `doc/design/*.md`
**技术栈文档**: `doc/TECH_STACK.md`

---

## 技术栈锁定

| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 25 | LTS版本 |
| Spring Boot | 4.0.0 | 基础框架 |
| Spring Cloud | 2025.1.2 | 微服务框架 |
| Spring Cloud Alibaba | 2025.1.0.0 | 微服务增强 |
| Sa-Token | 1.46.0 | 鉴权框架（登录认证/注解鉴权/会话/踢人下线），见 doc/design/06 |
| MyBatis-Plus | 3.5.6 | ORM框架 |
| MySQL | 9.7 | 主数据库 (Innovation版本) |
| Redis | 7.x | 缓存 |
| RabbitMQ | 3.x | 消息队列 |

---

## 微服务拆分（4个服务）

```
smart-property-gateway      # API网关
smart-property-system       # 系统基础（认证授权+系统管理+文件+报表）
smart-property-property     # 房产财务（房产+财务，强关联）
smart-property-operation    # 运营管理（运营+行政）
```

### 服务职责

| 服务 | 职责 | 数据库 |
|------|------|--------|
| gateway | 路由、限流、认证 | 无 |
| system | 认证授权、系统管理、文件管理、报表服务 | smart_property_system |
| property | 房产管理、业主管理、租赁管理、收费管理、车位管理 | smart_property_property |
| operation | 服务工单、保洁绿化、消防保安、停车管理、公告规章、消息中心 | smart_property_operation |

---

## 核心约束

### 代码规范（阿里巴巴Java开发手册）

- **命名**: 类名UpperCamelCase，方法/变量lowerCamelCase，常量UPPER_SNAKE_CASE
- **分层**: Controller → Service → Mapper，禁止跨层调用
- **领域模型**: DO（数据库）、DTO（入参）、VO（出参）、Query（查询）
- **异常**: 统一异常处理，5位错误码（A用户端/B系统端/C第三方）
- **日志**: SLF4J + 占位符 `log.info("userId: {}", userId)`

### 数据库规范

- **必备字段**: `id`, `company_id`, `create_by`, `create_time`, `update_by`, `update_time`, `is_deleted`
- **表前缀**: `sys_`, `property_`, `finance_`, `operation_`, `admin_`, `report_`, `file_`, `auth_`
- **布尔字段**: `is_xxx tinyint unsigned`（1=是，0=否）
- **金额**: `decimal(10,2)`
- **索引**: 主键`pk_`，唯一`uk_`，普通`idx_`
- **多租户**: 每张业务表必须有`company_id`字段

### MyBatis-Plus规范

- **分页**: 必须使用`PaginationInnerInterceptor`插件
- **简单CRUD**: 使用MyBatis-Plus内置方法
- **复杂SQL**: 必须写XML映射文件，放在`resources/mapper/`目录
- **禁止**: 在Java代码中拼接SQL

### 接口规范

- **风格**: RESTful
- **路径**: `/api/v1/{模块}/{资源}`
- **响应**: `{code, message, data}`
- **分页**: `{total, pageNum, pageSize, pages, records}`

---

## 安全要求

- **密码**: BCrypt加密存储
- **敏感字段**: AES加密存储 + 脱敏展示（手机号/身份证）
- **认证**: Sa-Token（网关 SaReactorFilter 统一校验 + 服务 SaInterceptor 二道防线；会话存 Redis 共享；详见 doc/design/06-Sa-Token鉴权框架整合设计.md）
- **注解鉴权**: Controller 可用 @SaCheckLogin / @SaCheckRole / @SaCheckPermission；权限数据在登录时快照进 SaSession
- **接口限流**: Sentinel（全局1000 QPS，单用户100 QPS）
- **审计日志**: AOP自动记录操作日志

---

## 参考资源

- 设计文档: `doc/design/` 目录下各模块设计文档
- 技术栈: `doc/TECH_STACK.md`
- 代码规范: 《阿里巴巴Java开发手册（泰山版）》

---

## 开发环境配置（macOS · Apple Silicon）

### Java & Maven（SDKMAN 统一管理）

| 工具 | 安装方式 | 实际路径 | 版本 |
|------|----------|----------|------|
| JDK | SDKMAN (`sdk install java 25.0.4-tem`) | `~/.sdkman/candidates/java/25.0.4-tem` | 25.0.4 LTS (Temurin) |
| Maven | SDKMAN (`sdk install maven 3.9.16`) | `~/.sdkman/candidates/maven/current` | 3.9.16 |

- `JAVA_HOME` / `PATH` 由 SDKMAN 自动注入（shell 配置文件已加载 `sdkman-init.sh`），**无需手动设置环境变量**
- 确认/切换版本：`sdk current java`、`sdk use java 25.0.4-tem`

**常用命令：**
```bash
java -version          # 检查Java版本
mvn clean install      # 编译打包
mvn spring-boot:run    # 启动Spring Boot应用
mvn test               # 运行测试
```

### 中间件（Docker Compose 统一编排）

本地开发不直接安装 MySQL/Redis 等中间件，全部由 `deploy/docker-compose.middleware.yml` 启动；
服务端配置默认连 `127.0.0.1`（可用环境变量或 `.env` 覆盖，见 `.env.example`）。

```bash
cd /Users/zzz/Documents/work/SmartProperty/deploy
docker compose -f docker-compose.middleware.yml up -d    # 启动全部中间件
docker compose -f docker-compose.middleware.yml ps       # 查看状态
docker compose -f docker-compose.middleware.yml down     # 停止
```

| 中间件 | 端口 | 默认凭据 |
|--------|------|----------|
| MySQL | 3306 | root / 123456 |
| Redis | 6379 | 密码 123456 |
| Nacos | 8848（控制台 8080，gRPC 9848） | nacos / 123456 |
| RabbitMQ | 5672（管理台 15672） | rabbitmq / 123456 |
| MinIO | 9000（控制台 9001） | minioadmin / 12345678 |

### MySQL 数据库

| 项目 | 值 |
|------|-----|
| 主机 | `127.0.0.1` |
| 端口 | `3306` |
| 账号 | `root` |
| 密码 | `123456` |

**连接命令（本机未装 mysql 客户端，走容器内执行）：**
```bash
docker exec -it $(docker compose -f deploy/docker-compose.middleware.yml ps -q mysql) mysql -u root -p123456
```

**初始化数据库（首次或重建后执行）：**
```bash
cd /Users/zzz/Documents/work/SmartProperty/deploy
bash init-db.sh all    # 可选 system | property | operation
```

**项目数据库：**
- `smart_property_system` - 系统服务库
- `smart_property_property` - 房产财务库
- `smart_property_operation` - 运营管理库

---

## 数据库操作规范

### ⚠️ 重要限制

1. **仅限项目数据库** - 只操作 `smart_property_*` 相关数据库，禁止操作其他数据库
2. **禁止生产环境操作** - 仅在本地开发环境执行数据库操作
3. **删除前必须确认** - 执行 DROP/DELETE/TRUNCATE 前必须先确认影响范围
4. **备份优先** - 执行破坏性操作前建议先备份

### 允许的操作

- ✅ 创建/修改项目数据库表结构
- ✅ 插入测试数据
- ✅ 查询数据用于调试
- ✅ 执行项目SQL迁移脚本

### 禁止的操作

- ❌ 操作非项目数据库（如 mysql、information_schema 等系统库）
- ❌ 修改数据库用户权限
- ❌ 删除整个数据库
- ❌ 在不确定影响范围时执行批量删除

---

## 本地运行约定（端口占用防护）

后端四个微服务（gateway / system / property / operation）共用同一组端口。
**任何一次 `mvn spring-boot:run` / IDE Run / 调试启动之后，必须配套收尾**——否则下次起服务时会报 `Web server failed to start. Port XXXX was already in use`，需要手动 `lsof` + `kill` 才能恢复。

### 必须遵守

1. **启动前先确认端口空闲**

   ```bash
   # macOS
   lsof -nP -iTCP:8000-8003 -sTCP:LISTEN || true
   # 若有残留 PID，直接 kill -9 <pid>
   ```

2. **`mvn spring-boot:run` 必须在后台跑**，并主动跟踪任务，**确认编译/启动完成后立即停掉服务**

   ```bash
   # 启动（后台）
   cd /Users/zzz/Documents/work/SmartProperty
   mvn -pl smart-property-system -am spring-boot:run > /tmp/sp-system.log 2>&1 &
   # 等待健康（轮询，最多 60s）
   for i in $(seq 1 30); do
     curl -sf http://localhost:8001/actuator/health && break || sleep 2
   done
   ```

3. **调试完毕立即停止服务**

   ```bash
   # 通过端口反查 PID 再杀，避免误杀
   lsof -ti :8001 | xargs kill -9
   # 兜底：把 spring-boot:run 留下的 mvn 进程也清掉
   pkill -f "spring-boot:run" 2>/dev/null || true
   ```

4. **CI/一键验证场景** 用 `mvn -pl <module> -am -DskipTests compile` / `mvn -pl <module> test` 就够了，**不要** 顺手跑 `spring-boot:run`。

### 端口分配速查

| 服务                                     | 端口   |
| -------------------------------------- | ---- |
| gateway (`smart-property-gateway`)     | 8000 |
| system (`smart-property-system`)       | 8001 |
| property (`smart-property-property`)   | 8002 |
| operation (`smart-property-operation`) | 8003 |

> 同台机器同时跑四个服务时，记得每个 `mvn spring-boot:run` 命令后都跟上对应的停止动作；如果只是单服务验证，**只起那一个**，验证完杀掉。

---


