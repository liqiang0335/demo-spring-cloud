package com.demo.server02.controller

import com.demo.server02.config.Server02Config
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class Server02Controller {

    @Autowired
    private lateinit var server02Config: Server02Config

    @GetMapping("/hello")
    fun hello(@RequestParam(defaultValue = "World") name: String): Map<String, Any> {
        return mapOf(
            "message" to "${server02Config.message} $name!",
            "server" to "server02",
            "port" to 9802,
            "version" to server02Config.version,
            "featureX_enabled" to server02Config.enableFeatureX,
            "timestamp" to System.currentTimeMillis()
        )
    }

    @GetMapping("/info")
    fun info(): Map<String, Any> {
        return mapOf(
            "service" to "server02",
            "port" to 9802,
            "version" to server02Config.version,
            "status" to "running",
            "config" to mapOf(
                "message" to server02Config.message,
                "enableFeatureX" to server02Config.enableFeatureX,
                "batchSize" to server02Config.batchSize,
                "retryAttempts" to server02Config.retryAttempts,
                "allowedUsers" to server02Config.allowedUsers
            )
        )
    }

    @GetMapping("/config")
    fun getConfig(): Map<String, Any> {
        return mapOf(
            "message" to server02Config.message,
            "version" to server02Config.version,
            "enableFeatureX" to server02Config.enableFeatureX,
            "batchSize" to server02Config.batchSize,
            "retryAttempts" to server02Config.retryAttempts,
            "allowedUsers" to server02Config.allowedUsers,
            "lastUpdated" to System.currentTimeMillis()
        )
    }

    @GetMapping("/users")
    fun getUsers(): Map<String, Any> {
        return mapOf(
            "users" to listOf(
                mapOf("id" to 1, "name" to "张三", "email" to "zhangsan@example.com"),
                mapOf("id" to 2, "name" to "李四", "email" to "lisi@example.com")
            ),
            "server" to "server02",
            "total" to 2
        )
    }

    @PostMapping("/users")
    fun createUser(@RequestBody user: Map<String, Any>): Map<String, Any> {
        return mapOf(
            "created" to user,
            "id" to System.currentTimeMillis(),
            "server" to "server02",
            "created_at" to System.currentTimeMillis()
        )
    }
}
