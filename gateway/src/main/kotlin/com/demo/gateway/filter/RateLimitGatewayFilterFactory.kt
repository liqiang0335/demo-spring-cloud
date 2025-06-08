package com.demo.gateway.filter

import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

/**
 * 自定义限流过滤器
 */
@Component
class RateLimitGatewayFilterFactory : AbstractGatewayFilterFactory<RateLimitGatewayFilterFactory.Config>() {

    data class Config(
        var requestsPerSecond: Int = 10
    )

    private val requestCounts = mutableMapOf<String, MutableList<Long>>()

    override fun apply(config: Config): GatewayFilter {
        return GatewayFilter { exchange, chain ->
            val request = exchange.request
            val response = exchange.response
            val clientIp = request.remoteAddress?.address?.hostAddress ?: "unknown"
            val currentTime = System.currentTimeMillis()
            
            val requests = requestCounts.getOrPut(clientIp) { mutableListOf() }
            
            // 清理超过1秒的请求记录
            requests.removeIf { it < currentTime - 1000 }
            
            if (requests.size >= config.requestsPerSecond) {
                response.statusCode = HttpStatus.TOO_MANY_REQUESTS
                return@GatewayFilter response.setComplete()
            }
            
            requests.add(currentTime)
            chain.filter(exchange)
        }
    }

    override fun getConfigClass(): Class<Config> = Config::class.java
}
