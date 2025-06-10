---
applyTo: "**"
---

该项目是一个基于 `Spring Cloud` 的微服务架构示例。

- 开发语言：`Kotlin`
- 主要框架：`Spring Boot 3.2` + `Spring Cloud`
- 服务注册与配置中心：`Nacos v2`
- API 网关：`Spring Cloud Gateway`
- 如果有必要时请使用 `context7` MCP 查询技术最新的文档

## 项目结构

- `gateway`: 作为 API 网关，负责路由请求到相应的服务。
  - 端口: `http://localhost:9999`
- `server01`: 服务提供者，提供具体的业务逻辑。
  - 端口: `http://localhost:9801`
- `server02`: 服务提供者，提供具体的业务逻辑。
  - 端口: `http://localhost:9802`
- `web`: 前端应用，负责与用户交互。

## nacos-server

- nacos-server: 服务注册与配置中心，提供服务发现和配置管理功能。
- `docker/docker-compose.yml `文件中定义了所有 nacos 的配置, 容器的名称是 `demo-nacos`。
- 管理地址是: `http://localhost:18848/nacos`

## 注意事项

- 项目使用 IDEA 开发工具运行中, 所以不要使用 `./gradlew bootRun` 脚本直接运行项目测试, 需要执行时和我确认,让我帮你运行,让我把运行结果发给你分析。
