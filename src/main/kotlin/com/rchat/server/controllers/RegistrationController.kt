package com.rchat.server.controllers

import com.rchat.server.models.Users
import com.rchat.server.services.PgUserDetailsService

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("registration")
class RegistrationController(@Autowired private var userService: PgUserDetailsService) {
    @PostMapping
    fun addUser(@Valid user: Users, bindingResult: BindingResult, model: Model): String {
        if (bindingResult.hasErrors())
            return "error"
        if (!userService.saveUser(user)) {
            return "error"
        }
        return "success"
    }
}