# 智能物业系统 - 后端微服务技术栈设计

## 一、整体架构概览

```
┌─────────────────────────────────────────────────────────────────┐
│                        客户端层                                  │
│  Web App (Vue)  │  小程序  │  移动App  │  业主/租户门户           │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                      API Gateway (Spring Cloud Gateway)          │
│            路由 / 限流 / 认证(Sa-Token) / 日志 / 灰度发布                   │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                      服务注册与发现 (Nacos)                       │
└─────────────────────────────────────────────────────────────────┘
                              │
        ┌─────────────────────┼─────────────────────┐
        ▼                     ▼                     ▼
┌──────────────┐    ┌──────────────┐    ┌──────────────┐
│  Gateway     │    │  System      │    │  Property    │
│  API网关     │    │  系统基础     │    │  房产财务    │
└──────────────┘    └──────────────┘    └──────────────┘
                              │
                              ▼
                    ┌──────────────┐
                    │  Operation   │
                    │  运营管理     │
                    └──────────────┘
```

---

## 二、技术栈选型

### 1. 核心框架

| 层级 | 技术选型 | 版本 | 说明 |
|------|---------|------|------|
| **开发语言** | Java | 25 | LTS版本，性能提升显著，支持虚拟线程 |
| **基础框架** | Spring Boot | 4.0.0 | 行业标准，生态成熟 |
| **微服务框架** | Spring Cloud | 2025.1.0 | 完整的微服务解决方案 |
| **微服务增强** | Spring Cloud Alibaba | 2025.1.0.0 | 阿里巴巴微服务最佳实践 |

### 2. 微服务核心组件

| 组件 | 技术选型 | 说明 |
|------|---------|------|
| **服务注册/配置中心** | Nacos 3.1.1 | 同时提供服务发现和配置管理 |
| **API网关** | Spring Cloud Gateway | 响应式网关，性能优于Zuul |
| **服务调用** | OpenFeign + LoadBalancer | 声明式HTTP客户端 |
| **熔断降级** | Sentinel | 阿里开源，功能强大 |
| **分布式事务** | Seata | 支持AT/TCC/Saga模式 |
| **链路追踪** | SkyWalking / Micrometer Tracing | 可观测性 |

### 3. 数据层

| 组件 | 技术选型 | 说明 |
|------|---------|------|
| **关系型数据库** | MySQL 9.7 | 主数据库，支持JSON、窗口函数 (Innovation版本) |
| **缓存** | Redis 7.x (集群模式) | 热点数据缓存、分布式锁、会话管理 |
| **消息队列** | RabbitMQ 3.x | 可靠投递、延迟队列、死信队列 |
| **对象存储** | MinIO / 阿里云OSS | 文件、图片、图纸存储 |

### 4. 开发工具

| 组件 | 技术选型 | 说明 |
|------|---------|------|
| **ORM框架** | MyBatis-Plus 3.5.6 | 简化CRUD，支持代码生成 |
| **API文档** | Knife4j 4.4.0 (Swagger增强) | 在线API调试 |
| **工具库** | Hutool 5.8.27 + MapStruct 1.5.5 | 常用工具、对象映射 |
| **校验框架** | Hibernate Validator | 参数校验 |
| **日志框架** | SLF4J + Logback | 统一日志格式 |

---

## 三、微服务拆分方案

采用 **4个核心业务服务** 的精简方案，降低开发和运维压力。

### 服务清单

```
smart-property-gateway          # API网关服务
smart-property-system           # 系统基础服务
├── 认证授权 (OAuth2/JWT)
├── 系统管理 (用户/角色/权限/菜单/字典)
├── 文件管理 (上传/下载/网盘)
└── 报表服务 (统计分析/导出)

smart-property-property         # 房产财务服务
├── 房产管理 (住宅/商业/楼宇/房间)
├── 业主管理 (业主档案/家庭成员/验房/入住)
├── 租赁管理 (租户/合同/租金/转兑)
├── 收费管理 (费项设置/物业费生成/收费/退款)
├── 车位管理 (车位维护/销售/出租/缴费)
└── 预收款管理 (预收/抵扣/余额)

smart-property-operation        # 运营管理服务
├── 服务工单 (报修/投诉/回访)
├── 保洁绿化 (清洁安排/绿化检查)
├── 消防安全 (消防设施/巡查/演练)
├── 保安管理 (执勤/巡逻/来访/物品出入)
├── 停车管理 (车辆识别/出入记录/临时停车)
├── 公告通知 (发布/推送/已读)
├── 规章制度 (上传/查阅)
├── 意见箱 (提交/处理/反馈)
├── 投票调查 (发起/投票/统计)
└── 消息中心 (站内消息/短信/邮件/微信推送)
```

### 服务合并说明

| 原方案（10个） | 新方案（4个） | 合并理由 |
|---------------|--------------|----------|
| auth + system + file + report | system | 都是基础支撑类，强关联 |
| property + finance | property | 强关联，查询频繁跨表 |
| operation + admin | operation | 都是运营管理类 |
| gateway | 独立保留 | 基础设施，职责单一 |

---

## 四、项目结构

```
smart-property/
├── smart-property-common/              # 公共模块
│   ├── smart-property-common-core/     # 核心工具/常量/异常
│   ├── smart-property-common-redis/    # Redis工具
│   ├── smart-property-common-mybatis/  # MyBatis配置/租户拦截器
│   ├── smart-property-common-security/ # 安全工具/脱敏/加密
│   └── smart-property-common-log/      # 日志/审计工具
│
├── smart-property-gateway/             # 网关服务
├── smart-property-system/              # 系统基础服务
├── smart-property-property/            # 房产财务服务
├── smart-property-operation/           # 运营管理服务
│
├── docs/                               # 文档
├── deploy/                             # 部署配置
│   ├── docker/
│   └── sql/
└── pom.xml                             # 父POM
```

---

## 五、数据库设计

### 分库策略

按服务边界拆分数据库（共4个库）：

```
smart_property_system       # 系统库 (用户/角色/权限/字典/租户/文件/报表)
smart_property_property     # 房产库 (住宅/楼宇/房间/业主/租赁/费项/收费/车位)
smart_property_operation    # 运营库 (服务单/保洁/消防/安防/停车/公告/规章/消息)
```

### 多租户方案

采用 **共享数据库、tenant_id字段隔离** 方案：

**数据规模预估：**
- 10 家物业公司
- 100 个小区
- 20 万使用人
- 房产记录约 40-60 万
- 收费记录约 500-1000 万（含历史）

**隔离策略：**
- 每张业务表增加 `company_id` 字段标识所属物业公司
- 通过 MyBatis 拦截器自动注入租户条件，开发无感知
- 网关层统一解析租户标识（Header: X-Tenant-Id）传递到下游
- 索引策略：`company_id` + 业务字段联合索引

---

## 六、安全设计

### 1. 认证授权

```
├── Sa-Token 统一认证（网关统一鉴权 + 注解鉴权，会话存 Redis）
├── RBAC 权限模型 (角色-菜单-操作权限)
├── 多租户数据隔离 (company_id)
├── 业主/租户独立登录入口 (小程序/门户)
├── 支持小程序/APP/Web多端登录
└── Token刷新机制 (Redis存储RefreshToken)
```

### 2. 数据安全

```
├── 敏感字段加密存储
│   ├── 身份证号 → AES加密存储，展示时脱敏
│   ├── 手机号 → AES加密存储，展示时脱敏 (138****8888)
│   └── 密码 → BCrypt单向哈希
│
└── 数据脱敏规则
    ├── 身份证：显示前3后4位
    ├── 手机号：显示前3后4位
    └── 姓名：显示姓，名用*替代
```

### 3. 接口安全

```
├── 防重放攻击 (timestamp + nonce + sign)
├── 接口限流 (Sentinel)
│   ├── 全局：1000 QPS
│   ├── 单用户：100 QPS
│   └── 登录接口：10 QPS + 图形验证码
└── 输入校验 (Hibernate Validator + XSS防护)
```

---

## 七、技术栈版本锁定

```xml
<!-- 父POM版本管理 -->
<properties>
    <java.version>25</java.version>
    <spring-boot.version>4.0.0</spring-boot.version>
    <spring-cloud.version>2025.1.0</spring-cloud.version>
    <spring-cloud-alibaba.version>2025.1.0.0</spring-cloud-alibaba.version>
    <mybatis-plus.version>3.5.6</mybatis-plus.version>
    <mysql.version>9.7.0</mysql.version>
    <redis.version>3.3.0</redis.version>
    <rabbitmq.version>3.12.0</rabbitmq.version>
    <seata.version>2.5.0</seata.version>
    <sentinel.version>1.8.9</sentinel.version>
    <nacos.version>3.1.1</nacos.version>
    <rocketmq.version>5.3.1</rocketmq.version>
    <knife4j.version>4.4.0</knife4j.version>
    <hutool.version>5.8.27</hutool.version>
    <mapstruct.version>1.5.5.Final</mapstruct.version>
</properties>
```

---

## 总结

本技术栈设计特点：

1. **精简务实** - 4个核心业务服务，降低开发和运维压力
2. **安全完善** - 数据加密、接口防护、审计日志
3. **性能优秀** - 多级缓存、读写分离、异步处理
4. **规模匹配** - 针对10家物业/100小区/20万用户优化
