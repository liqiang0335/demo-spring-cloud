package com.demo.server01

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class Server01Application

fun main(args: Array<String>) {
    runApplication<Server01Application>(*args)
}
