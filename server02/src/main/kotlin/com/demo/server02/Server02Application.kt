package com.demo.server02

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@SpringBootApplication
@EnableDiscoveryClient
class Server02Application

fun main(args: Array<String>) {
    runApplication<Server02Application>(*args)
}
