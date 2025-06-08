package com.demo.gateway.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 网关管理控制器
 */
@RestController
@RequestMapping("/gateway")
class GatewayController {

    @GetMapping("/info")
    fun getGatewayInfo(): Map<String, Any> {
        return mapOf(
            "name" to "Spring Cloud Gateway",
            "version" to "1.0.0",
            "status" to "running",
            "timestamp" to System.currentTimeMillis(),
            "routes" to listOf(
                mapOf("path" to "/server01/**", "target" to "http://localhost:9801"),
                mapOf("path" to "/server02/**", "target" to "http://localhost:9802"),
                mapOf("path" to "/api/**", "target" to "http://localhost:9801")
            )
        )
    }

    @GetMapping("/health")
    fun health(): Map<String, String> {
        return mapOf(
            "status" to "UP",
            "gateway" to "healthy"
        )
    }
}
