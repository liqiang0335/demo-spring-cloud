package com.demo.server01.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.stereotype.Component

@Component
@RefreshScope
@ConfigurationProperties(prefix = "demo.server01")
data class Server01Config(
    var message: String = "Hello from Server01",
    var version: String = "1.0.0",
    var enableDebug: Boolean = false,
    var maxConnections: Int = 100,
    var timeout: Long = 5000L,
    var features: MutableList<String> = mutableListOf()
)
