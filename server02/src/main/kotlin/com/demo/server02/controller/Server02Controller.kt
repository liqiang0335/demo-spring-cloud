package com.demo.server02.controller

import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class Server02Controller {

    @GetMapping("/hello")
    fun hello(@RequestParam(defaultValue = "World") name: String): Map<String, Any> {
        return mapOf(
            "message" to "Hello $name from Server02!",
            "server" to "server02",
            "port" to 9802,
            "timestamp" to System.currentTimeMillis()
        )
    }

    @GetMapping("/info")
    fun info(): Map<String, Any> {
        return mapOf(
            "service" to "server02",
            "port" to 9802,
            "version" to "1.0.0",
            "status" to "running"
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
