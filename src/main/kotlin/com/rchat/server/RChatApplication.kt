package com.rchat.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class RChatApplication

fun main(args: Array<String>) {
    runApplication<RChatApplication>(*args)
}
