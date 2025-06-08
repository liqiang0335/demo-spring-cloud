package com.demo.server01.controller

import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class Server01Controller {

    @GetMapping("/hello")
    fun hello(@RequestParam(defaultValue = "World") name: String): Map<String, Any> {
        return mapOf(
            "message" to "Hello $name from Server01!",
            "server" to "server01",
            "port" to 9801,
            "timestamp" to System.currentTimeMillis()
        )
    }

    @GetMapping("/info")
    fun info(): Map<String, Any> {
        return mapOf(
            "service" to "server01",
            "port" to 9801,
            "version" to "1.0.0",
            "status" to "running"
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
