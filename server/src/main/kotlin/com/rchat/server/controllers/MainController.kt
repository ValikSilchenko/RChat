package com.rchat.server.controllers

import com.rchat.server.models.Users
import com.rchat.server.services.PgUserDetailsService

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import javax.validation.Valid

@Controller
@RequestMapping("/")
class MainController (@Autowired private var userService: PgUserDetailsService) {
    @GetMapping
    fun greeting(model: Model): String {
        model.addAttribute("title", "World")
        return "index"
    }

    @GetMapping("about")
    fun about(model: Model) = "about"

    @PostMapping("registration")
    fun addUser(@Valid user: Users, bindingResult: BindingResult, model: Model): String {
        if (bindingResult.hasErrors()) {
            val err: List<String> = when (bindingResult.fieldErrors[0].field) {
                "username" -> listOf("err", "Incorrect username length")
//                "email" -> listOf("err", "Incorrect email")
//                "phone" -> listOf("err", "Incorrect phone length. It must contains 10 digits without other symbols")
                else -> listOf("err", "Incorrect password length")
            }
            model.addAttribute(err[0], err[1])
            return "form"
        }
        if (!userService.saveUser(user)) {
            return "redirect:/registration"
        }
        userService.autoLogin(user)
        return "redirect:/"
    }

    @GetMapping("registration")
    fun register(model: Model) = "form"
}