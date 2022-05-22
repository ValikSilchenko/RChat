package com.rchat.server.services

import com.rchat.server.models.Users
import com.rchat.server.repos.UserRepository

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component


@Component
class PgUserDetailsService(@Autowired private var userRepo: UserRepository) : UserDetailsService {
    private var bCryptPasswordEncoder: BCryptPasswordEncoder = BCryptPasswordEncoder()  // TODO

    @Override
    override fun loadUserByUsername(username: String): UserDetails {
        val user: Users = userRepo.findByUsername(username) ?: throw UsernameNotFoundException("User not found")

        val authorities: List<SimpleGrantedAuthority> = listOf(SimpleGrantedAuthority(user.toString()))

        return User(user.username, user.password, authorities)
    }

    fun saveUser(user: Users): Boolean {
        val dbUser = userRepo.findByUsername(user.username)
        println(dbUser)
        if (dbUser != null)
            return false

//        user.password = bCryptPasswordEncoder.encode(user.password)
        userRepo.save(user)
        return true
    }

    fun checkPassword(user: Users): Boolean {
        val userFormDB = userRepo.findByUsername(user.username) ?: throw UsernameNotFoundException("User not found")
        return (userFormDB.password == user.password)
    }
}