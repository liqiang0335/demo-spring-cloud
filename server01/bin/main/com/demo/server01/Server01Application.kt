package com.demo.server01

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@SpringBootApplication
@EnableDiscoveryClient
class Server01Application

fun main(args: Array<String>) {
    runApplication<Server01Application>(*args)
}
