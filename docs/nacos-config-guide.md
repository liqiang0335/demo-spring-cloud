# Nacos 动态配置管理指南

本文档介绍如何在 Nacos 中配置和管理微服务的动态配置。

## 1. 访问 Nacos 控制台

- **地址**: `http://localhost:18848/nacos`
- **默认用户名**: `nacos`
- **默认密码**: `nacos`

## 2. 需要创建的配置文件

### 2.1 公共配置 (common-config.yml)

**Data ID**: `common-config.yml` **Group**: `DEFAULT_GROUP` **配置格式**: `YAML`

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

### 2.2 Gateway 路由配置 (gateway-routes.yml)

**Data ID**: `gateway-routes.yml` **Group**: `DEFAULT_GROUP` **配置格式**: `YAML`

```yaml
spring:
  cloud:
    gateway:
      routes:
        # 路由到 server01 服务
        - id: server01_route
          uri: lb://server01-service
          predicates:
            - Path=/server01/**
          filters:
            - StripPrefix=1
            - AddResponseHeader=X-Response-From, Gateway
            - AddResponseHeader=X-Route-Version, v1.0

        # 路由到 server02 服务
        - id: server02_route
          uri: lb://server02-service
          predicates:
            - Path=/server02/**
          filters:
            - StripPrefix=1
            - AddResponseHeader=X-Response-From, Gateway
            - AddResponseHeader=X-Route-Version, v1.0

        # API路由 - 可以根据需要添加更多路由规则
        - id: api_route
          uri: lb://server01-service
          predicates:
            - Path=/api/**
          filters:
            - AddRequestHeader=X-Gateway-Request, true
            - AddRequestHeader=X-Request-Time, "#{T(System).currentTimeMillis()}"

      discovery:
        locator:
          enabled: true
          lower-case-service-id: true

# Gateway 特定配置
demo:
  gateway:
    version: "1.0.0"
    enable-cors: true
    enable-rate-limit: false
    default-timeout: 30000
```

### 2.3 Server01 配置 (server01-config.yml)

**Data ID**: `server01-config.yml` **Group**: `DEFAULT_GROUP` **配置格式**: `YAML`

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

# 数据库配置示例（如果需要）
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

### 2.4 Server02 配置 (server02-config.yml)

**Data ID**: `server02-config.yml` **Group**: `DEFAULT_GROUP` **配置格式**: `YAML`

```yaml
# Server02 服务特定配置
demo:
  server02:
    message: "Hello from Server02 (Nacos Config)"
    version: "2.0.0"
    enableFeatureX: true
    batchSize: 100
    retryAttempts: 5
    allowedUsers:
      - "admin"
      - "user1"
      - "user2"

# 业务配置示例
business:
  processing:
    enabled: true
    max-queue-size: 1000
    worker-threads: 4
  notification:
    email-enabled: true
    sms-enabled: false
```

## 3. 配置创建步骤

### 3.1 登录 Nacos 控制台

1. 打开浏览器访问 `http://localhost:18848/nacos`
2. 使用用户名 `nacos` 和密码 `nacos` 登录

### 3.2 创建配置文件

1. 点击左侧菜单 "配置管理" -> "配置列表"
2. 点击右上角 "+" 按钮创建新配置
3. 按照上述模板依次创建以下配置：
   - `common-config.yml`
   - `gateway-routes.yml`
   - `server01-config.yml`
   - `server02-config.yml`

### 3.3 配置参数说明

- **Data ID**: 配置文件的唯一标识
- **Group**: 配置分组，默认使用 `DEFAULT_GROUP`
- **配置格式**: 选择 `YAML`
- **配置内容**: 复制上述对应的配置内容

## 4. 动态配置验证

### 4.1 启动服务

确保所有服务都已启动：

- Gateway: `http://localhost:9999`
- Server01: `http://localhost:9801`
- Server02: `http://localhost:9802`

### 4.2 验证配置生效

#### 测试 Server01 配置

```bash
# 查看 Server01 当前配置
curl http://localhost:9999/server01/config

# 或直接访问
curl http://localhost:9801/api/config
```

#### 测试 Server02 配置

```bash
# 查看 Server02 当前配置
curl http://localhost:9999/server02/config

# 或直接访问
curl http://localhost:9802/api/config
```

### 4.3 动态更新测试

1. 在 Nacos 控制台修改配置（如修改 `server01-config.yml` 中的 `message` 字段）
2. 点击 "发布" 按钮
3. 等待几秒钟后，重新调用 API 验证配置是否自动更新

```bash
# 修改配置后再次调用，应该看到新的配置值
curl http://localhost:9801/api/config
```

## 5. 配置刷新机制

### 5.1 自动刷新

- 配置了 `@RefreshScope` 注解的 Bean 会自动刷新
- Nacos Client 会监听配置变化并自动推送更新

### 5.2 手动刷新（可选）

```bash
# 手动触发配置刷新
curl -X POST http://localhost:9801/actuator/refresh
curl -X POST http://localhost:9802/actuator/refresh
```

## 6. 最佳实践

### 6.1 配置分层

- **公共配置**: 所有服务共享的配置放在 `common-config.yml`
- **服务配置**: 每个服务特有的配置放在各自的配置文件中
- **环境配置**: 可以为不同环境创建不同的配置文件

### 6.2 配置命名规范

- Data ID 格式: `服务名-环境.yml`，如 `server01-dev.yml`, `server01-prod.yml`
- Group 可以按业务线或团队划分

### 6.3 配置版本管理

- Nacos 支持配置历史版本管理
- 重要配置修改前建议备份
- 可以使用 Nacos 的配置回滚功能

### 6.4 安全考虑

- 敏感配置（如密码、密钥）可以考虑加密存储
- 生产环境建议修改 Nacos 默认用户名密码
- 考虑使用 Nacos 的权限管理功能

## 7. 故障排查

### 7.1 常见问题

1. **配置不生效**: 检查 Data ID、Group 是否匹配
2. **连接失败**: 检查 Nacos 服务是否正常运行
3. **刷新失败**: 检查 `@RefreshScope` 注解是否正确使用

### 7.2 日志查看

```bash
# 查看服务日志中的 Nacos 相关信息
# 在 IDEA 控制台或服务日志文件中查看
```

### 7.3 健康检查

```bash
# 检查服务健康状态
curl http://localhost:9801/actuator/health
curl http://localhost:9802/actuator/health
```

这样就完成了 Nacos 动态配置管理的完整设置。您可以按照这个指南在 Nacos 控制台中创建相应的配置文件，然后测试动态配置的效果。
