package com.demo.server02.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.stereotype.Component

@Component
@RefreshScope
@ConfigurationProperties(prefix = "demo.server02")
data class Server02Config(
    var message: String = "Hello from Server02",
    var version: String = "1.0.0",
    var enableFeatureX: Boolean = false,
    var batchSize: Int = 50,
    var retryAttempts: Int = 3,
    var allowedUsers: MutableList<String> = mutableListOf()
)
