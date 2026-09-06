# 部署操作手册

本文档覆盖 **和家云服务管理云平台** 从环境准备到服务上线的完整步骤。

适用版本：Java 25 / Spring Boot 4.0.0 / Spring Cloud 2025.1.0 / Spring Cloud Alibaba 2025.1.0.0 / MySQL 9.7 / Redis 7.x / Nacos 3.2.4。

---

## 1. 部署架构

```
              ┌─────────────────────────────┐
              │    Nacos 3.2.4 (8848/8080)  │  ← 注册中心 / 配置中心
              └──────────────┬──────────────┘
                             │
   ┌─────────────┐   ┌───────┴────────┐  ┌──────────────┐  ┌──────────────┐
   │  Gateway    │──▶│ System (8081) │  │ Property     │  │ Operation    │
   │  (8000)     │   │  smart_        │  │ (8082)       │  │ (8083)       │
   │             │   │  property_     │  │ smart_       │  │ smart_       │
   │             │   │  system        │  │ property_    │  │ property_    │
   │             │   │                │  │ property     │  │ operation    │
   └─────────────┘   └────────────────┘  └──────────────┘  └──────────────┘
            │
            ▼
   ┌─────────────────────────────┐    ┌─────────────────────────────┐
   │  MySQL 9.7 (3306)           │    │  Redis 7.x (6379)           │
   └─────────────────────────────┘    └─────────────────────────────┘
```

| 组件 | 端口 | 说明 |
|------|------|------|
| Nacos | 8848 / 9848 / 8080 | 注册与配置中心，**必须先于业务服务启动**（8080 为 v3 控制台） |
| MySQL | 3306 | 三个业务库（system / property / operation） |
| Redis | 6379 | Token / 字典 / 限流计数 |
| Gateway | 8000 | 唯一对外入口，路由 + 认证 + 限流 |
| System | 8081 | 认证 / 用户 / 部门 / 字典 / 日志 |
| Property | 8082 | 房产 / 业主 / 租赁 / 收费 / 车位 |
| Operation | 8083 | 工单 / 保洁 / 消防 / 保安 / 公告 / 消息 |

---

## 2. 环境准备

### 2.1 必备软件

| 软件 | 版本 | 检查命令 |
|------|------|----------|
| JDK | 25 LTS | `java -version` |
| Maven | 3.9+ | `mvn -v` |
| MySQL | 9.7 | `mysql --version` |
| Redis | 7.x | `redis-cli --version` |
| Nacos | 3.2.4 | 启动后访问 `:8080`（v3 控制台独立端口） |
| Git | 任意 | `git --version` |
| curl | 任意 | `curl --version`（验证用） |

### 2.2 环境变量

**Windows（Git Bash）— 临时**：

```bash
export JAVA_HOME="/c/Program Files/Java/jdk-25.0.3"
export PATH="$JAVA_HOME/bin:$PATH"
export MAVEN_HOME="/d/zzzFile/zzz/idea_Maven/apache-maven-3.9.16"
export PATH="$MAVEN_HOME/bin:$PATH"
```

**永久设置**（控制面板 → 系统 → 高级系统设置 → 环境变量）：

| 变量名 | 值 |
|--------|----|
| `JAVA_HOME` | `C:\Program Files\Java\jdk-25.0.3` |
| `MAVEN_HOME` | `D:\zzzFile\zzz\idea_Maven\apache-maven-3.9.16` |
| `PATH` 追加 | `%JAVA_HOME%\bin;%MAVEN_HOME%\bin` |

### 2.3 中间件连接配置

四个微服务的 application.yml 默认连接以下地址，本地环境保持一致即可：

| 中间件 | 主机 | 端口 | 账号 / 密码 |
|--------|------|------|------------|
| MySQL | `localhost` | 3306 | `root` / `123456` |
| Redis | `localhost` | 6379 | 无密码 |
| Nacos | `localhost` | 8848（控制台 `:8080`） | `nacos` / `nacos` |

> ⚠️ 部署到测试 / 生产环境时，**不要使用默认账号密码**，务必修改并通过 Nacos 配置中心下发，不要硬编码到 application.yml。

### 2.4 启动中间件

按以下顺序启动：

```bash
# 1. MySQL
net start mysql          # Windows
# 或 mysqld --console     # Linux/Mac

# 2. Redis
redis-server             # 前台启动
# 或 redis-server --daemonize yes   # 后台

# 3. Nacos（standalone 模式）
# Windows:  startup.cmd -m standalone
# Linux/Mac: sh startup.sh -m standalone
```

启动后访问 http://localhost:8080 确认控制台登录页能打开（v3 控制台独立端口，OpenAPI 仍在 8848）。

---

## 3. 数据库初始化

> ⚠️ 严格遵守 [CLAUDE.md](../../CLAUDE.md) 中「数据库操作规范」：**只**操作 `smart_property_*` 库，不动其他库；删除/重建前先备份。

### 3.1 三个业务库

| 数据库 | 服务 | 初始化脚本 |
|--------|------|-----------|
| `smart_property_system` | system | `deploy/sql/smart_property_system.sql` |
| `smart_property_property` | property | `deploy/sql/smart_property_property.sql` |
| `smart_property_operation` | operation | `deploy/sql/smart_property_operation.sql` |

### 3.2 一键初始化

```bash
# 进入 SQL 目录
cd deploy/sql

# Linux / Mac / Git Bash
bash ../init-db.sh

# Windows CMD / PowerShell
..\init-db.bat
```

脚本会按 **system → property → operation** 顺序执行。

### 3.3 手动初始化（适合排错）

```bash
mysql -h localhost -P 3306 -u root -p123456 < deploy/sql/smart_property_system.sql
mysql -h localhost -P 3306 -u root -p123456 < deploy/sql/smart_property_property.sql
mysql -h localhost -P 3306 -u root -p123456 < deploy/sql/smart_property_operation.sql
```

执行后确认：

```sql
SHOW DATABASES LIKE 'smart_property_%';
SELECT COUNT(*) FROM information_schema.tables
  WHERE table_schema IN ('smart_property_system','smart_property_property','smart_property_operation');
-- 预期：3 个库，共 64 张表
```

### 3.4 默认账号

初始化完成后自带一个超管账号：

| 字段 | 值 |
|------|----|
| 用户名 | `admin` |
| 密码 | `admin123` |
| 公司 | 默认租户（company_id = 1） |

> 首次登录后请立即修改密码，并关闭 / 限制 admin 远程登录。

---

## 4. 端口与启动顺序

### 4.1 端口分配

| 服务 | 端口 | 启动优先级 |
|------|------|-----------|
| Nacos | 8848 / 9848 | 1（先启动） |
| Gateway | 8000 | 2 |
| System | 8081 | 3（被其他服务依赖） |
| Property | 8082 | 4（依赖 system） |
| Operation | 8083 | 5（依赖 system + property） |

> 业务服务**强依赖** Nacos：未启动 Nacos 直接跑业务，会因找不到注册中心而失败。

### 4.2 启动前清场（防端口占用）

`mvn spring-boot:run` 结束后偶发残留 java 进程，下一次启动会报 `Port XXXX was already in use`。规范做法见 [CLAUDE.md](../../CLAUDE.md)「本地运行约定」。

```bash
# 启动前先确认端口空闲
netstat -ano | findstr ":8000 :8081 :8082 :8083" || true

# 若有残留：反查 PID 再杀（不要直接 taskkill /F /IM java.exe）
PID=$(netstat -ano | findstr ":8081" | awk '{print $NF}' | head -1)
[ -n "$PID" ] && taskkill //F //PID "$PID"
```

---

## 5. 启动微服务

### 5.1 标准启动流程

**方式一：后台启动（推荐）**

```bash
cd D:/work/SmartProperty

# Gateway
mvn -pl smart-property-gateway -am spring-boot:run > /tmp/sp-gateway.log 2>&1 &
# 等待健康
for i in $(seq 1 30); do curl -sf http://localhost:8000/actuator/health && break || sleep 2; done

# System
mvn -pl smart-property-system -am spring-boot:run > /tmp/sp-system.log 2>&1 &
for i in $(seq 1 30); do curl -sf http://localhost:8081/actuator/health && break || sleep 2; done

# Property
mvn -pl smart-property-property -am spring-boot:run > /tmp/sp-property.log 2>&1 &
for i in $(seq 1 30); do curl -sf http://localhost:8082/actuator/health && break || sleep 2; done

# Operation
mvn -pl smart-property-operation -am spring-boot:run > /tmp/sp-operation.log 2>&1 &
for i in $(seq 1 30); do curl -sf http://localhost:8083/actuator/health && break || sleep 2; done
```

**方式二：IDEA Run / Debug**

在 IDEA 中分别打开四个模块的 `*Application.java` 主类直接运行即可，端口不变。

### 5.2 启动顺序与依赖

```
Nacos (8848)  ──▶  MySQL (3306)  ──▶  Redis (6379)  ──▶  Gateway (8000)
                                                                │
                                                                ▼
                                              ┌─── System (8081) ───┐
                                              │                     │
                                              ▼                     ▼
                                        Property (8082)    Operation (8083)
```

### 5.3 验证服务上线

```bash
# 1. 直连检查（不走网关）
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health
curl http://localhost:8083/actuator/health

# 2. 经网关检查
curl http://localhost:8000/actuator/health

# 3. 登录拿 Token
curl -X POST http://localhost:8000/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

预期返回 `{"code":"00000","message":"ok","data":{"accessToken":"..."}}`。

### 5.4 日志位置

| 服务 | 默认日志路径 |
|------|-------------|
| Gateway | `<项目>/logs/smart-property-gateway.log`（也支持 stdout） |
| System | `<项目>/logs/smart-property-system.log` |
| Property | `<项目>/logs/smart-property-property.log` |
| Operation | `<项目>/logs/smart-property-operation.log` |

后台启动时输出在 `/tmp/sp-*.log`。

---

## 6. 接口连通性验证

### 6.1 一键测试脚本

```bash
# 进入 deploy 目录
cd deploy

# Linux / Mac / Git Bash
bash test-api.sh

# Windows
test-api.bat
```

脚本会按 登录 → 用户信息 → 菜单树 → 用户列表 → 部门树 → 创建小区 → 小区列表 → 创建工单 → 工单列表 → 创建公告 顺序跑一遍，输出每个接口前 200 字符。

### 6.2 手工调用示例

```bash
TOKEN=$(curl -s -X POST http://localhost:8000/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}' \
  | grep -o '"accessToken":"[^"]*"' | cut -d'"' -f4)

# 拿部门树
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8000/api/v1/system/depts/tree

# 拿小区列表
curl -H "Authorization: Bearer $TOKEN" \
  "http://localhost:8000/api/v1/property/communitys?pageNum=1&pageSize=10"
```

### 6.3 访问 API 文档

启动 system 服务后访问：http://localhost:8081/doc.html （Knife4j）

---

## 7. 优雅停止服务

### 7.1 单服务停止

```bash
# 通过端口反查 PID 再杀
PID=$(netstat -ano | findstr ":8081" | awk '{print $NF}' | head -1)
[ -n "$PID" ] && taskkill //F //PID "$PID"
```

### 7.2 全量清理

```bash
# 兜底：杀掉所有 spring-boot:run 残留的 java 进程
taskkill //F //IM java.exe 2>/dev/null || true
```

> ⚠️ 不要在 IDEA Debug 模式下用 `taskkill /F /IM java.exe` 强杀，会导致断点状态错乱。先在 IDEA 停止按钮终止。

---

## 8. 常见问题

### 8.1 端口被占用

```
Web server failed to start. Port 8081 was already in use.
```

解决：见 [§4.2 启动前清场](#42-启动前清场防端口占用)。

### 8.2 找不到 Nacos / 启动后立即退出

- 确认 Nacos 已 `startup.sh -m standalone` 启动并能访问 http://localhost:8080（v3 控制台；OpenAPI 在 8848）
- 确认 `application.yml` 中 `spring.cloud.nacos.discovery.server-addr=localhost:8848`
- 看 `logs/*.log` 中是否有 `Connection refused` 字样

### 8.3 数据库连接失败

- 确认 `mysql -uroot -p123456` 能登入
- 确认 `deploy/sql/` 三个脚本已执行，`SHOW DATABASES` 可见三个库
- 检查服务 `application.yml` 中 `spring.datasource.url` 的库名是否与脚本一致

### 8.4 Redis 连接失败

- `redis-cli ping` 期望返回 `PONG`
- 有密码的 Redis 需在 `spring.data.redis.password` 配上

### 8.5 登录返回 401 / Token 失效

- 检查 system 服务是否正常启动（日志中是否有 `Started *Application in X seconds`）
- 检查 Redis 中是否有过期 Token 残留，可在登录前 `redis-cli FLUSHDB`（**仅本地**）

### 8.6 Knife4j / Swagger 文档打不开

- 确认请求的是 system 服务（8081），不是 gateway（8000）
- 确认访问的是 `/doc.html`，不是 `/swagger-ui.html`

### 8.7 Maven 编译报错：Java 版本不匹配

```bash
mvn -v
# 期望：Java version: 25.x
```

如果显示 17 / 21，需要重新设 `JAVA_HOME` 并关闭当前 shell 重开。

---

## 9. 生产部署 Checklist

仅供本地/测试参考，生产环境（K8s/Docker/物理机）需要额外处理：

- [ ] 修改所有中间件默认密码（MySQL / Redis / Nacos / admin）
- [ ] JWT 密钥、加密密钥使用 Nacos 配置中心下发，不进 git
- [ ] 关闭 Knife4j（生产不应暴露）
- [ ] Sentinel 限流规则按业务调整（默认全局 1000 QPS / 单用户 100 QPS）
- [ ] 启用 HTTPS / Nginx 反代
- [ ] 启用审计日志、慢 SQL 日志、GC 日志
- [ ] 数据库开启 binlog、主从
- [ ] 监控告警：Nacos、MySQL、Redis、JVM

---

## 10. Docker Compose 一键全栈

仓库根 `docker-compose.yml` 提供 **4 业务 + 5 中间件** 一键启动方案。

### 10.1 服务清单

| 容器名 | 镜像 | 端口 | 数据卷 |
|--------|------|------|--------|
| sp-mysql | `mysql:9.7` | 3306 | `mysql-data` |
| sp-redis | `redis:7-alpine` | 6379 | `redis-data` |
| sp-nacos | `nacos/nacos-server:v3.1.1-standalone` | 8848/9848 | `nacos-data` |
| sp-rabbitmq | `rabbitmq:3-management` | 5672/15672 | `rabbitmq-data` |
| sp-minio | `minio/minio:latest` | 9000/9001 | `minio-data` |
| sp-gateway | 本地构建 | 8000 | - |
| sp-system | 本地构建 | 8001 | - |
| sp-property | 本地构建 | 8002 | - |
| sp-operation | 本地构建 | 8003 | - |

### 10.2 启动

```bash
# 1. 准备环境变量
cp .env.example .env
# 编辑 .env，至少替换 MYSQL_ROOT_PASSWORD / NACOS_PASSWORD / MINIO_SECRET_KEY

# 2. 启动
docker compose up -d

# 3. 查看进度
docker compose ps
docker compose logs -f gateway   # 看业务服务启动

# 4. 等所有容器 healthy
docker compose ps
# 期望：sp-mysql / sp-nacos / sp-gateway 等全部 `Up (healthy)`
```

### 10.3 验证

```bash
# 健康检查
curl http://localhost:8000/actuator/health   # gateway
curl http://localhost:8001/actuator/health   # system
curl http://localhost:8002/actuator/health   # property
curl http://localhost:8003/actuator/health   # operation

# 登录拿 token
TOKEN=$(curl -s -X POST http://localhost:8000/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}' \
  | grep -o '"accessToken":"[^"]*"' | cut -d'"' -f4)

# 核心业务
curl -H "Authorization: Bearer $TOKEN" http://localhost:8000/api/v1/system/depts/tree
curl -H "Authorization: Bearer $TOKEN" "http://localhost:8000/api/v1/property/communitys?pageNum=1&pageSize=10"
curl -H "Authorization: Bearer $TOKEN" "http://localhost:8000/api/v1/operation/service-orders?pageNum=1&pageSize=10"

# Knife4j 统一文档（4 tab）
open http://localhost:8000/doc.html
```

### 10.4 重新构建镜像

```bash
# 改完代码后
mvn -Dmaven.test.skip=true package

# 重新构建某个服务
docker compose build gateway
docker compose up -d gateway
```

### 10.5 数据重置

```bash
# 停服 + 删卷
docker compose down -v

# 重启（自动跑 deploy/sql/00-init-other-dbs.sql + 3 个建表 SQL）
docker compose up -d
```

### 10.6 镜像构建原理

- 多阶段构建：builder (`maven:3.9-eclipse-temurin-25`) → runtime (`eclipse-temurin:25-jre`)
- 镜像最终大小 ~250MB（仅 JRE + fat jar）
- non-root 用户运行（`spring:spring`）
- `HEALTHCHECK` 走 `/actuator/health` 端点
- `JAVA_OPTS` 通过环境变量注入，含 `-XX:+UseContainerSupport` 自动适配容器内存

---

## 11. 附录：目录约定

```
smart-property/
├── docker-compose.yml          # Docker 全栈编排（4 业务 + 5 中间件）
├── .env.example                # 环境变量模板
├── .dockerignore
├── Dockerfile.template         # 镜像构建参考模板
│
├── smart-property-{common,gateway,system,property,operation}/
│   ├── Dockerfile              # 每个服务一个
│   ├── pom.xml
│   └── src/{main,test}/...
│
├── deploy/                     # 部署相关
│   ├── README.md
│   ├── DEPLOY.md               # 部署操作手册（本文件）
│   ├── init-db.bat / init-db.sh    # 一键初始化数据库
│   ├── test-api.bat / test-api.sh  # 一键接口测试
│   └── sql/                    # 三个库结构 + 初始数据 + 自动建库
│       ├── 00-init-other-dbs.sql   # 自动建 property/operation 库
│       ├── smart_property_system.sql
│       ├── smart_property_property.sql
│       └── smart_property_operation.sql
│
└── doc/                        # 设计文档
```
