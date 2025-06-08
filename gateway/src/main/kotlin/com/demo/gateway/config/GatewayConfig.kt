package com.demo.gateway.config

import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.http.HttpStatus
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Configuration
class GatewayConfig {

    /**
     * 编程式路由配置（可选，作为 YAML 配置的补充）
     */
    @Bean
    fun customRouteLocator(builder: RouteLocatorBuilder): RouteLocator {
        return builder.routes()
            // 健康检查路由
            .route("health_check") { r ->
                r.path("/health")
                    .filters { f ->
                        f.setStatus(HttpStatus.OK)
                            .addResponseHeader("Content-Type", "application/json")
                    }
                    .uri("http://localhost:9999")
            }
            // 负载均衡路由示例（如果使用服务发现）
            .route("loadbalanced_route") { r ->
                r.path("/lb/**")
                    .filters { f ->
                        f.stripPrefix(1)
                            .addRequestHeader("X-Gateway-Timestamp", System.currentTimeMillis().toString())
                    }
                    .uri("http://localhost:9801") // 可以根据需要修改为 lb://service-name
            }
            .build()
    }

    /**
     * 全局过滤器 - 请求日志记录
     */
    @Bean
    fun loggingGlobalFilter(): GlobalFilter {
        return GlobalFilter { exchange, chain ->
            val request = exchange.request
            val startTime = System.currentTimeMillis()
            
            println("Gateway Request: ${request.method} ${request.uri} from ${request.remoteAddress}")
            
            chain.filter(exchange).doFinally {
                val duration = System.currentTimeMillis() - startTime
                println("Gateway Response: ${exchange.response.statusCode} - Duration: ${duration}ms")
            }
        }
    }

    /**
     * 全局过滤器 - 请求头处理
     */
    @Bean
    fun requestHeaderFilter(): GlobalFilter {
        return GlobalFilter { exchange, chain ->
            val request = exchange.request.mutate()
                .header("X-Gateway-Version", "1.0")
                .header("X-Request-ID", java.util.UUID.randomUUID().toString())
                .build()
            
            chain.filter(exchange.mutate().request(request).build())
        }
    }
}
