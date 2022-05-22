package com.rchat.server.controllers

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class MainController {
    @GetMapping("/")
    fun greeting(model: Model): String {
        model.addAttribute("title", "World")
        return "index"
    }

    @GetMapping("/register")
    fun register(model: Model): String {
//        model.addAttribute("user", User())
        return "form"
    }
}