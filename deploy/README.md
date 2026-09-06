# Deploy · 部署与运维

本目录是 **和家云服务管理云平台** 的部署、运维、初始化相关内容的统一入口。

## 文档

| 文件 | 用途 |
|------|------|
| [DEPLOY.md](DEPLOY.md) | **部署操作手册** — 环境准备、数据库初始化、服务启动、接口验证、问题排查 |

## 脚本

| 文件 | 用途 | 平台 |
|------|------|------|
| `init-db.bat` / `init-db.sh` | 一键初始化三个业务库（system / property / operation） | Windows / Linux |
| `test-api.bat` / `test-api.sh` | 一键跑通核心接口，验证服务可用 | Windows / Linux |

## SQL

`sql/` 目录存放三个库的结构与初始数据脚本：

| 文件 | 库 | 表数 |
|------|----|----|
| `sql/smart_property_system.sql` | `smart_property_system` | 16 |
| `sql/smart_property_property.sql` | `smart_property_property` | 26 |
| `sql/smart_property_operation.sql` | `smart_property_operation` | 22 |

## 快速开始

```bash
# 1. 读部署手册
#    用编辑器打开 DEPLOY.md，至少读完 §1 §2 §3

# 2. 启动中间件（MySQL / Redis / Nacos）

# 3. 初始化数据库
bash deploy/init-db.sh         # Linux / Mac
deploy\init-db.bat             # Windows

# 4. 启动四个微服务（按 DEPLOY.md §5）
mvn -pl smart-property-gateway  -am spring-boot:run
mvn -pl smart-property-system   -am spring-boot:run
mvn -pl smart-property-property -am spring-boot:run
mvn -pl smart-property-operation -am spring-boot:run

# 5. 验证
bash deploy/test-api.sh        # Linux / Mac
deploy\test-api.bat            # Windows
```

## 默认账号

| 字段 | 值 |
|------|----|
| 用户名 | `admin` |
| 密码 | `admin123` |
| 租户 | `company_id = 1` |

首次登录后请立即改密。生产环境请走 Nacos 配置中心覆盖默认密钥。

## 端口速查

| 角色 | 端口 |
|------|------|
| Nacos | 8848 / 9848 |
| Gateway | 8000 |
| System | 8081 |
| Property | 8082 |
| Operation | 8083 |

> 端口与本地运行约束详见根目录 [CLAUDE.md](../CLAUDE.md)「本地运行约定」一节。

## 相关文档

- [../README.md](../README.md) — 项目总览
- [../CLAUDE.md](../CLAUDE.md) — 开发规范 / 端口约定 / 数据库规范
- [../doc/TECH_STACK.md](../doc/TECH_STACK.md) — 技术栈
- [../doc/design/](../doc/design/) — 各服务详细设计文档
