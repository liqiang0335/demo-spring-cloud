package com.demo.server01.controller

import com.demo.server01.config.Server01Config
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class Server01Controller {

    @Autowired
    private lateinit var server01Config: Server01Config

    @GetMapping("/hello")
    fun hello(@RequestParam(defaultValue = "World") name: String): Map<String, Any> {
        return mapOf(
            "message" to "${server01Config.message} $name!",
            "server" to "server01",
            "port" to 9801,
            "version" to server01Config.version,
            "enableDebug" to server01Config.enableDebug,
            "timestamp" to System.currentTimeMillis()
        )
    }

    @GetMapping("/info")
    fun info(): Map<String, Any> {
        return mapOf(
            "service" to "server01",
            "port" to 9801,
            "version" to server01Config.version,
            "status" to "running",
            "config" to mapOf(
                "message" to server01Config.message,
                "enableDebug" to server01Config.enableDebug,
                "maxConnections" to server01Config.maxConnections,
                "timeout" to server01Config.timeout,
                "features" to server01Config.features
            )
        )
    }

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

    @PostMapping("/data")
    fun postData(@RequestBody data: Map<String, Any>): Map<String, Any> {
        return mapOf(
            "received" to data,
            "server" to "server01",
            "processed_at" to System.currentTimeMillis()
        )
    }
}
