# Nacos 在 Spring Boot 中的配置与使用学习笔记

## 目录
1. [Nacos 简介](#1-nacos-简介)
2. [环境准备](#2-环境准备)
3. [依赖配置](#3-依赖配置)
4. [Spring Boot 应用配置](#4-spring-boot-应用配置)
5. [配置文件结构](#5-配置文件结构)
6. [配置类编写](#6-配置类编写)
7. [Nacos 控制台操作](#7-nacos-控制台操作)
8. [动态配置刷新](#8-动态配置刷新)
9. [配置优先级和加载机制](#9-配置优先级和加载机制)
10. [最佳实践](#10-最佳实践)
11. [常见问题与解决方案](#11-常见问题与解决方案)

---

## 1. Nacos 简介

### 1.1 什么是 Nacos
Nacos (Dynamic Naming and Configuration Service) 是阿里巴巴开源的一个更易于构建云原生应用的动态服务发现、配置管理和服务管理平台。

### 1.2 主要功能
- **服务发现和服务健康监测**
- **动态配置管理**
- **动态 DNS 服务**
- **服务及其元数据管理**

### 1.3 核心概念
- **Namespace**: 用于进行租户粒度的配置隔离
- **Group**: 配置分组，通常用于区分不同的项目或应用
- **Data ID**: 配置的唯一标识符
- **Configuration**: 具体的配置内容

---

## 2. 环境准备

### 2.1 Nacos Server 部署
本项目使用 Docker Compose 部署 Nacos Server：

```yaml
# docker/docker-compose.yml
services:
  demo-nacos:
    image: nacos/nacos-server:v2.5.0
    container_name: demo-nacos
    ports:
      - "18848:8848"  # 服务注册发现端口
      - "19848:9848"  # gRPC端口（2.0+版本需要）
    environment:
      - MODE=standalone
      - NACOS_AUTH_TOKEN=SecretKey012345678901234567890123456789012345678901234567890123456789
      - NACOS_AUTH_IDENTITY_KEY=demo-nacos
      - NACOS_AUTH_IDENTITY_VALUE=demo-nacos
    volumes:
      - ./data/nacos-data:/home/nacos/data
      - ./data/nacos-logs:/home/nacos/logs
```

### 2.2 启动 Nacos Server
```bash
# 进入 docker 目录
cd docker
# 启动 Nacos Server
docker-compose up -d
```

### 2.3 访问控制台
- **地址**: http://localhost:18848/nacos
- **用户名**: nacos
- **密码**: nacos

---

## 3. 依赖配置

### 3.1 Gradle 依赖配置
```kotlin
// build.gradle.kts
plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.2.11"
    id("io.spring.dependency-management") version "1.1.7"
}

extra["springCloudVersion"] = "2023.0.3"
extra["springCloudAlibabaVersion"] = "2023.0.3.3"

dependencies {
    implementation(platform("com.alibaba.cloud:spring-cloud-alibaba-dependencies:${property("springCloudAlibabaVersion")}"))
    implementation(platform("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}"))
    
    // Nacos 配置管理
    implementation("com.alibaba.cloud:spring-cloud-starter-alibaba-nacos-config")
    // Nacos 服务发现
    implementation("com.alibaba.cloud:spring-cloud-starter-alibaba-nacos-discovery")
    
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
}
```

### 3.2 版本兼容性
- **Spring Boot**: 3.2.11
- **Spring Cloud**: 2023.0.3
- **Spring Cloud Alibaba**: 2023.0.3.3
- **Nacos Server**: v2.5.0

---

## 4. Spring Boot 应用配置

### 4.1 application.yml 基础配置
```yaml
# server01/src/main/resources/application.yml
server:
  port: 9801

spring:
  application:
    name: server01-service  # 服务名称，重要！
  profiles:
    active: dev
  config:
    import: 
      - "optional:nacos:server01-config.yml"  # 服务特定配置
      - "optional:nacos:common-config.yml"    # 公共配置
  cloud:
    nacos:
      config:
        server-addr: 127.0.0.1:18848    # Nacos 服务器地址
        namespace: public               # 命名空间，默认 public
        group: DEFAULT_GROUP           # 配置分组
        file-extension: yml            # 配置文件格式
        refresh-enabled: true          # 启用配置自动刷新
        shared-configs:                # 共享配置
          - data-id: common-config.yml
            group: DEFAULT_GROUP
            refresh: true
        extension-configs:             # 扩展配置
          - data-id: server01-config.yml
            group: DEFAULT_GROUP
            refresh: true
      discovery:
        server-addr: 127.0.0.1:18848   # 服务发现地址
        namespace: public
        group: DEFAULT_GROUP

# 暴露配置刷新端点
management:
  endpoints:
    web:
      exposure:
        include: refresh,health,info
```

### 4.2 配置详解

#### 4.2.1 核心配置项
- `spring.application.name`: 服务名称，用于服务发现和配置文件关联
- `spring.config.import`: 导入外部配置源（Nacos 配置）
- `spring.cloud.nacos.config.server-addr`: Nacos 服务器地址
- `spring.cloud.nacos.config.namespace`: 命名空间
- `spring.cloud.nacos.config.group`: 配置分组

#### 4.2.2 配置加载机制
- `shared-configs`: 多个服务共享的配置
- `extension-configs`: 服务特定的扩展配置
- `optional:nacos:` 前缀表示配置是可选的，不存在时不会报错

---

## 5. 配置文件结构

### 5.1 配置层次结构
```
Nacos 配置中心
├── common-config.yml        # 公共配置（所有服务共享）
├── gateway-routes.yml       # 网关路由配置
├── server01-config.yml      # Server01 服务配置
└── server02-config.yml      # Server02 服务配置
```

### 5.2 公共配置 (common-config.yml)
```yaml
# 公共配置文件 - 所有服务共享
logging:
  level:
    com.alibaba.nacos: INFO
    com.alibaba.cloud.nacos: INFO
    org.springframework.cloud: INFO
    root: INFO
    com.demo: DEBUG

management:
  endpoint:
    health:
      show-details: always
  endpoints:
    web:
      exposure:
        include: refresh,health,info,metrics

# 公共业务配置
demo:
  common:
    app-name: "Spring Cloud Demo"
    environment: "development"
    timezone: "Asia/Shanghai"
    feature-flags:
      enable-metrics: true
      enable-tracing: false
```

### 5.3 服务特定配置 (server01-config.yml)
```yaml
# Server01 服务特定配置
demo:
  server01:
    message: "Hello from Server01 (Nacos Config)"
    version: "2.0.0"
    enableDebug: true
    maxConnections: 200
    timeout: 8000
    features:
      - "feature-a"
      - "feature-b"
      - "enhanced-logging"

# 数据库配置示例
spring:
  datasource:
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
      connection-timeout: 30000

# 缓存配置示例
cache:
  redis:
    enabled: false
    host: localhost
    port: 6379
    timeout: 2000
```

---

## 6. 配置类编写

### 6.1 配置类注解说明
```kotlin
@Component                                    // 注册为 Spring Bean
@RefreshScope                                // 支持配置动态刷新
@ConfigurationProperties(prefix = "demo.server01")  // 绑定配置前缀
data class Server01Config(
    var message: String = "Hello from Server01",   // 默认值
    var version: String = "1.0.0",
    var enableDebug: Boolean = false,
    var maxConnections: Int = 100,
    var timeout: Long = 5000L,
    var features: MutableList<String> = mutableListOf()
)
```

### 6.2 关键注解详解

#### 6.2.1 @RefreshScope
- **作用**: 标记 Bean 支持配置动态刷新
- **原理**: 当配置变更时，Spring 会销毁旧的 Bean 实例，创建新的实例
- **注意**: 必须添加此注解才能实现动态配置更新

#### 6.2.2 @ConfigurationProperties
- **作用**: 将配置文件中的属性绑定到 Java 对象
- **prefix**: 指定配置属性的前缀
- **松散绑定**: 支持 kebab-case、camelCase 等多种命名风格

### 6.3 在 Controller 中使用配置
```kotlin
@RestController
@RequestMapping("/api")
class Server01Controller {

    @Autowired
    private lateinit var server01Config: Server01Config

    @GetMapping("/config")
    fun getConfig(): Map<String, Any> {
        return mapOf(
            "message" to server01Config.message,
            "version" to server01Config.version,
            "enableDebug" to server01Config.enableDebug,
            "maxConnections" to server01Config.maxConnections,
            "timeout" to server01Config.timeout,
            "features" to server01Config.features,
            "lastUpdated" to System.currentTimeMillis()
        )
    }
}
```

---

## 7. Nacos 控制台操作

### 7.1 访问控制台
1. 打开浏览器访问: http://localhost:18848/nacos
2. 使用用户名 `nacos` 和密码 `nacos` 登录

### 7.2 创建配置
1. 点击左侧菜单 "配置管理" -> "配置列表"
2. 点击右上角 "+" 按钮创建新配置
3. 填写配置信息：
   - **Data ID**: 配置文件名（如 `server01-config.yml`）
   - **Group**: 配置分组（默认 `DEFAULT_GROUP`）
   - **配置格式**: 选择 `YAML`
   - **配置内容**: 输入 YAML 格式的配置内容

### 7.3 发布配置
1. 输入配置内容后，点击 "发布" 按钮
2. 系统会自动将配置推送到相关的服务实例

### 7.4 修改配置
1. 在配置列表中找到要修改的配置
2. 点击 "编辑" 按钮
3. 修改配置内容后点击 "发布"
4. 服务会自动获取最新配置（无需重启）

---

## 8. 动态配置刷新

### 8.1 自动刷新机制
- Nacos Client 会建立长连接监听配置变化
- 当配置发生变更时，Nacos Server 会主动推送变更通知
- 标记了 `@RefreshScope` 的 Bean 会自动重新创建

### 8.2 手动刷新配置
```bash
# 触发配置刷新端点
curl -X POST http://localhost:9801/actuator/refresh
```

### 8.3 验证配置更新
```bash
# 查看当前配置
curl http://localhost:9801/api/config

# 在 Nacos 控制台修改配置后再次查看
curl http://localhost:9801/api/config
```

### 8.4 刷新事件监听
```kotlin
@Component
class ConfigRefreshListener {
    
    @EventListener
    fun handleRefreshEvent(event: RefreshEvent) {
        println("配置刷新事件: ${event.keys}")
    }
}
```

---

## 9. 配置优先级和加载机制

### 9.1 配置加载顺序
Spring Boot 配置加载优先级（高到低）：
1. 命令行参数
2. 系统环境变量
3. application.yml 中的配置
4. Nacos 中的配置

### 9.2 Nacos 配置优先级
在 Nacos 配置中，优先级如下：
1. `extension-configs` 中的配置
2. `shared-configs` 中的配置
3. 默认配置文件（`${spring.application.name}.yml`）

### 9.3 配置合并策略
- **相同配置项**: 高优先级配置覆盖低优先级配置
- **不同配置项**: 进行合并
- **集合类型**: 进行合并（如 List、Map）

---

## 10. 最佳实践

### 10.1 配置分层策略
```
环境维度:
├── dev (开发环境)
├── test (测试环境)
├── staging (预发布环境)
└── prod (生产环境)

业务维度:
├── common-config.yml (公共配置)
├── database-config.yml (数据库配置)
├── redis-config.yml (缓存配置)
└── service-specific-config.yml (服务特定配置)
```

### 10.2 命名规范
```yaml
# Data ID 命名规范
服务名-环境.yml               # 如: user-service-dev.yml
服务名-模块-环境.yml          # 如: user-service-database-dev.yml
common-环境.yml              # 如: common-dev.yml
```

### 10.3 配置管理建议
1. **敏感信息处理**: 密码、密钥等敏感信息建议加密存储
2. **配置版本管理**: 重要配置修改前做好备份
3. **环境隔离**: 不同环境使用不同的 Namespace
4. **权限控制**: 生产环境配置应限制修改权限

### 10.4 监控和告警
```yaml
# 配置监控示例
management:
  endpoints:
    web:
      exposure:
        include: configprops,env,health,info
  endpoint:
    configprops:
      show-values: ALWAYS
    env:
      show-values: ALWAYS
```

---

## 11. 常见问题与解决方案

### 11.1 配置不生效
**问题**: 修改了 Nacos 配置，但服务中的配置没有更新

**解决方案**:
1. 检查 `@RefreshScope` 注解是否添加
2. 确认 Data ID、Group、Namespace 是否匹配
3. 检查服务是否正常连接到 Nacos Server
4. 查看应用日志中的 Nacos 相关信息

### 11.2 连接失败
**问题**: 服务启动时无法连接到 Nacos Server

**解决方案**:
```bash
# 检查 Nacos Server 是否正常运行
curl http://localhost:18848/nacos/actuator/health

# 检查端口是否正确开放
netstat -tlnp | grep 18848

# 检查 Docker 容器状态
docker ps | grep nacos
```

### 11.3 配置加载失败
**问题**: 启动时报 "Could not locate PropertySource" 错误

**解决方案**:
1. 确保配置文件在 Nacos 中已创建
2. 使用 `optional:nacos:` 前缀使配置变为可选
3. 检查配置文件格式是否正确

### 11.4 配置刷新缓慢
**问题**: 配置修改后需要很长时间才生效

**解决方案**:
```yaml
spring:
  cloud:
    nacos:
      config:
        refresh-enabled: true
        # 配置刷新间隔，默认为 1000ms
        timeout: 3000
```

### 11.5 日志查看
```bash
# 查看 Nacos 相关日志
tail -f nacos.log | grep -i "config"

# 查看应用日志
tail -f application.log | grep -i "nacos"
```

---

## 12. 实战示例

### 12.1 完整的配置示例
参考项目中的配置文件：
- `server01/src/main/resources/application.yml`
- `server02/src/main/resources/application.yml`
- `gateway/src/main/resources/application.yml`

### 12.2 测试脚本
项目提供了完整的测试脚本：
```bash
# Linux/Mac
./scripts/test-nacos-config.sh

# Windows
./scripts/test-nacos-config.bat
```

### 12.3 API 测试
使用 HTTP 文件测试配置管理：
```bash
# 查看 scripts/nacos-config-api.http
# 包含了完整的 Nacos API 操作示例
```

---

## 总结

Nacos 作为配置中心和服务发现中心，为微服务架构提供了强大的支持。通过本学习笔记，您应该能够：

1. 理解 Nacos 的核心概念和工作原理
2. 在 Spring Boot 项目中正确配置和使用 Nacos
3. 实现动态配置管理和服务发现
4. 处理常见问题和性能优化

建议在实际项目中逐步应用这些概念，并根据具体业务需求调整配置策略。

---

**参考资料**:
- [Spring Cloud Alibaba 官方文档](https://github.com/alibaba/spring-cloud-alibaba/wiki)
- [Nacos 官方文档](https://nacos.io/zh-cn/docs/what-is-nacos.html)
- [Spring Boot 配置文档](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config)
