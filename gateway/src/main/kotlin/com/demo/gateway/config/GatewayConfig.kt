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
            // 路由到 server01 服务
            .route("server01_route") { r ->
                r.path("/server01/**")
                    .filters { f ->
                        f.stripPrefix(1) // 去掉前缀: 去掉 URL 路径中的第一个部分
                            .addResponseHeader("X-Response-From", "Gateway")
                    }
                    .uri("lb://server01-service")  // 使用服务名称
            }
            // 路由到 server02 服务
            .route("server02_route") { r ->
                r.path("/server02/**")
                    .filters { f ->
                        f.stripPrefix(1)
                            .addResponseHeader("X-Response-From", "Gateway")
                    }
                    .uri("lb://server02-service")  // 使用服务名称
            }
            // API路由 - 路由到 server01
            .route("api_route") { r ->
                r.path("/api/**")
                    .filters { f ->
                        f.addRequestHeader("X-Gateway-Request", "true")
                    }
                    .uri("lb://server01-service")  // 使用服务名称
            }
            // 负载均衡路由示例
            .route("loadbalanced_route") { r ->
                r.path("/lb/**")
                    .filters { f ->
                        f.stripPrefix(1)
                            .addRequestHeader("X-Gateway-Timestamp", System.currentTimeMillis().toString())
                    }
                    .uri("lb://server01-service")  // 使用服务名称
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
