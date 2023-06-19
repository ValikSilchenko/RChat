package com.rchat.server.services

import com.rchat.server.models.Users
import com.rchat.server.repos.UserRepository

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component
import javax.persistence.Tuple


@Component
class PgUserDetailsService(private var userRepo: UserRepository,
                           private var mailService: DefaultEmailService) : UserDetailsService {
    private var bCryptPasswordEncoder: BCryptPasswordEncoder = BCryptPasswordEncoder()  // TODO
    private var verificationUsers: MutableMap<String, Pair<String, Users>> = mutableMapOf()

    @Override
    override fun loadUserByUsername(username: String): UserDetails {
        val user: Users = userRepo.findByUsername(username) ?: throw UsernameNotFoundException("User not found")

        val authorities: List<SimpleGrantedAuthority> = listOf(SimpleGrantedAuthority(user.toString()))

        return User(user.username, user.password, authorities)
    }

    fun registerUser(user: Users): Boolean {
        val dbUser = userRepo.findByEmail(user.email)
        if (dbUser != null || verificationUsers.containsKey(user.email))
            return false

        val verificationCode = mailService.sendMail(user.email!!)
        verificationUsers[user.email!!] = Pair(verificationCode, user)
        return true
    }

    fun saveUser(userMail: String, verificationCode: String): Users? {
        if (verificationCode == verificationUsers[userMail]?.first) {
            val user = verificationUsers[userMail]?.second
            verificationUsers.remove(userMail)
            user?.password = bCryptPasswordEncoder.encode(user?.password)
            return userRepo.save(user!!)
        }
        return null
    }

    fun login(email: String, password: String): Users? {
        val dbUser = userRepo.findByEmail(email) ?: return null
        if (dbUser.email == email && bCryptPasswordEncoder.matches(password, dbUser.password))
            return dbUser
        return null
    }

    fun autoLogin(user: Users) {
        val auth: Authentication = UsernamePasswordAuthenticationToken(user, null, null)
        SecurityContextHolder.getContext().authentication = auth
    }

    fun getMatchUsers(substr: String): List<Users?> {
        return userRepo.findMatchUsers(substr.lowercase())
    }

    fun getById(id: Int): Users {
        return userRepo.getById(id)
    }

    fun getByName(username: String): Users {
        return userRepo.findByUsername(username) ?: throw UsernameNotFoundException(username)
    }

    fun userExists(userId: Int): Boolean {
        if (userRepo.findById(userId).isEmpty)
            return false
        return true
    }
}