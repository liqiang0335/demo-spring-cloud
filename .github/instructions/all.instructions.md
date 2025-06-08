---
applyTo: "**"
---

该项目是一个基于 Spring Cloud 的微服务架构示例。

- 开发语言：Kotlin
- 主要框架：Spring Boot, Spring Cloud

## 项目结构

- gateway: 作为 API 网关，负责路由请求到相应的服务。
  - 端口: http://localhost:9999
- server01: 服务提供者，提供具体的业务逻辑。
  - 端口: http://localhost:9801
- server02: 服务提供者，提供具体的业务逻辑。
  - 端口: http://localhost:9802
- web: 前端应用，负责与用户交互。
