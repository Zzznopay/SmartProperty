# 智能物业系统 - 和家云服务管理云平台

基于 Spring Cloud 微服务架构的物业管理系统。

## 技术栈

| 技术 | 版本 |
|------|------|
| Java | 25 |
| Spring Boot | 4.0.0 |
| Spring Cloud | 2025.1.0 |
| Spring Cloud Alibaba | 2025.1.0.0 |
| MyBatis-Plus | 3.5.6 |
| MySQL | 9.7 |
| Redis | 7.x |

完整技术栈与版本说明见 [doc/TECH_STACK.md](doc/TECH_STACK.md)。

## 微服务拆分

| 服务 | 模块 | 端口 | 数据库 |
|------|------|------|--------|
| smart-property-gateway | API网关 | **8000** | - |
| smart-property-system | 认证授权 / 系统管理 / 文件 / 报表 | **8001** | smart_property_system |
| smart-property-property | 房产 / 业主 / 租赁 / 收费 / 车位 | **8002** | smart_property_property |
| smart-property-operation | 工单 / 保洁 / 消防 / 停车 / 公告 / 消息 | **8003** | smart_property_operation |

> 端口与本地运行约定见 [CLAUDE.md](CLAUDE.md) 端口防护章节；完整部署步骤见 [deploy/DEPLOY.md](deploy/DEPLOY.md)。

## 文档导航

| 文档 | 内容 |
|------|------|
| [CLAUDE.md](CLAUDE.md) | 项目开发规范与本地运行约定 |
| [doc/TECH_STACK.md](doc/TECH_STACK.md) | 技术栈与版本选型 |
| [doc/design/](doc/design/) | 各服务详细设计文档 |
| [deploy/README.md](deploy/README.md) | 部署与运维索引 |
| [deploy/DEPLOY.md](deploy/DEPLOY.md) | 部署操作手册（环境/初始化/启动/验证） |

## 快速开始（Docker Compose 全栈）

**前提**：已安装 Docker 24+ 与 docker compose v2。

```bash
# 1. 复制环境变量模板
cp .env.temple.example .env.temple
# 编辑 .env.temple，至少修改 MYSQL_ROOT_PASSWORD / NACOS_PASSWORD / MINIO_SECRET_KEY / SMART_PROPERTY_SECRET_KEY

# 2. 一键启动（4 业务 + 5 中间件：MySQL/Redis/Nacos/RabbitMQ/MinIO）
docker compose up -d

# 3. 等待所有容器 healthy（约 90s）
docker compose ps

# 4. 验证
curl http://localhost:8000/actuator/health   # gateway
curl http://localhost:8001/actuator/health   # system
curl http://localhost:8002/actuator/health   # property
curl http://localhost:8003/actuator/health   # operation

# 5. 登录拿 Token
TOKEN=$(curl -s -X POST http://localhost:8000/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}' \
  | grep -o '"accessToken":"[^"]*"' | cut -d'"' -f4)
echo "Token: $TOKEN"

# 6. 访问 Knife4j 统一文档（4 tab 聚合）
open http://localhost:8000/doc.html     # Mac
# 或浏览器直接打开 http://localhost:8000/doc.html
```

**默认账号**：`admin / admin123`

**统一接口文档入口**：`http://localhost:8000/doc.html`（4 个 tab：System/Property/Operation/Gateway）

## 快速开始（本地开发模式）

适合 IDE 单步调试：本地启 MySQL/Redis/Nacos，业务服务走 `mvn spring-boot:run`。

```bash
# 1. 初始化数据库
bash deploy/init-db.sh    # Linux/Mac；Windows 用 deploy\init-db.bat

# 2. 启动中间件（MySQL 3306、Redis 6379、Nacos 8848）

# 3. 启动四个微服务
mvn -pl smart-property-gateway   -am spring-boot:run
mvn -pl smart-property-system    -am spring-boot:run
mvn -pl smart-property-property  -am spring-boot:run
mvn -pl smart-property-operation -am spring-boot:run
```

- 网关地址：http://localhost:8000
- Knife4j（业务服务独立）：http://localhost:8001/doc.html

详细步骤与排错见 [deploy/DEPLOY.md](deploy/DEPLOY.md)。

## 目录结构

```
smart-property/
├── smart-property-common/    # 公共模块（core/redis/mybatis/security/log/mq/oss）
├── smart-property-gateway/   # 网关服务（含 Knife4j 聚合）
├── smart-property-system/    # 系统基础服务
├── smart-property-property/  # 房产财务服务
├── smart-property-operation/ # 运营管理服务
├── deploy/                   # 部署相关（脚本/SQL/手册/聚合 SQL）
├── doc/                      # 设计文档与技术栈
├── docker-compose.yml        # 4 业务 + 5 中间件一键启动
├── .env.example              # 环境变量模板
└── .dockerignore
```

## 已实现功能模块

### smart-property-system

| 模块 | 功能 |
|------|------|
| 认证授权 | 登录 / 注销 / 刷新 Token / 用户信息 / 菜单树 |
| 用户管理 | CRUD / 重置密码 / 角色分配 |
| 部门管理 | 树形查询 / CRUD |
| 字典管理 | 字典类型 / 字典数据 |
| 日志 | 登录日志 / 操作日志 |

### smart-property-property

| 模块 | 功能 |
|------|------|
| 房产 | 小区 / 楼宇 / 单元 / 房间 |
| 业主与租赁 | 业主 / 租户 / 租赁合同 / 验房 / 装修 |
| 收费 | 费项 / 台账 / 收费 / 退款 / 作废 / 抄表 / 预收款 |
| 车位 | 车位管理 / 销售 / 出租 |

### smart-property-operation

| 模块 | 功能 |
|------|------|
| 工单 | 服务工单 创建/分配/处理/回访/关闭 |
| 物业运营 | 清洁 / 消防 / 保安 / 来访 / 车辆进出 / 绿化 |
| 行政 | 公告 / 规章 / 投票 / 意见箱 / 社区活动 |
| 消息 | 消息中心 发送/已读/未读统计 |

## REST API 路径规范

| 路径前缀 | 模块 |
|----------|------|
| `/api/v1/auth/...` | 认证授权 |
| `/api/v1/system/...` | 系统管理 |
| `/api/v1/property/...` | 房产管理 |
| `/api/v1/finance/...` | 财务管理 |
| `/api/v1/operation/...` | 运营管理 |
| `/api/v1/admin/...` | 行政管理 |

完整接口列表与请求示例见 [doc/design/](doc/design/) 下各服务设计文档。

## 单元测试

| 服务 | 测试类 |
|------|--------|
| system | Auth / SysUser / SysDept / SysDict |
| property | Community / Building / Unit / Room / Owner / FeeItem / Ledger / Payment / ParkingSpace / LeaseContract / CheckRecord / Decoration / MeterReading / Prepayment / Tenant |
| operation | ServiceOrder / Notice / CleanArrange / FireFacility / SecurityArrange / VisitRecord / VehicleRecord / Greenery / CommunityActivity / Regulation / Survey / OpinionBox / Message |

## 后续扩展

- 报表统计（ECharts）
- 短信服务（阿里云短信）
- 微信小程序 / 移动 App

## 公共工具与自动填充

业务代码不应手写审计字段、时间格式、JSON 序列化、分页转换——统一由 `smart-property-common` 提供：

| 类 | 作用 | 关键方法 |
|----|------|----------|
| `core.util.DateUtils` | 时间格式化、LocalDateTime ↔ Date | `format()` `parseDateTime()` `now()` |
| `core.util.JsonUtils` | 统一 JSON 序列化/反序列化，复用 `smartPropertyObjectMapper` | `toJson()` `parse()` `parseList()` |
| `core.util.PageUtils` | MyBatis-Plus `IPage` → `PageResult` 转换 | `toPage(IPage)` `defaultPage()` |
| `core.service.TenantGuard` | 多租户越权校验（按 companyId） | `requireSameCompany(Long)` `requireSameCompany(BaseEntity)` |
| `core.context.SecurityContextHolder` | 当前操作人上下文（ThreadLocal） | `getUserId()` `getCompanyId()` |
| `security.filter.SecurityHeaderFilter` | 网关 X-User-Id/X-Company-Id 头自动注入 ThreadLocal | 自动注册（web 服务） |
| `mybatis.handler.AutoFillHandler` | 自动填充 createTime/updateTime/createBy/updateBy/isDeleted/companyId | MP 自动调用 |

**业务 Service 模板**：

```java
// 旧写法（已废弃，手写审计字段）
user.setCreateTime(LocalDateTime.now());
user.setUpdateTime(LocalDateTime.now());
user.setIsDeleted(0);
user.setCreateBy(operator);
user.setCompanyId(companyId);
save(user);

// 新写法（AutoFillHandler 全自动）
user.setCreateBy(operator);  // 仅 createBy/updateBy 来自业务
save(user);                  // 其余由框架填充
```
