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
import kotlin.random.Random


@Component
class PgUserDetailsService(private var userRepo: UserRepository,
                           private var mailService: DefaultEmailService
) : UserDetailsService {
    private var bCryptPasswordEncoder: BCryptPasswordEncoder = BCryptPasswordEncoder()  // TODO
    private var verificationUsers: MutableMap<String, Pair<String, Users>> = mutableMapOf()
    private var verificationRecovers: MutableMap<String, Pair<String, Users>> = mutableMapOf()

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

        val code = Random.nextInt(100000, 999999).toString()
        val mailContext = EmailContext(
            "no-reply.rchat@gmail.com",
            user.email!!,
            "Подтверждение регистрации",
            "<a>Ваш код подтверждения регистрации:</a> <h2>${code}</h2> <a>Введите его в приложении для подтверждения</a>")
        mailService.sendMail(mailContext)
        verificationUsers[user.email!!] = Pair(code, user)
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

    fun recoverPassword(userMail: String): Boolean {
        val dbUser = userRepo.findByEmail(userMail) ?: return false
        val code = Random.nextInt(100000, 999999).toString()
        val mailContext = EmailContext(
            "no-reply.rchat@gmail.com",
            dbUser.email!!,
            "Восстановление пароля",
            "<a>Ваш код для восстановления пароля:</a> <h2>${code}</h2>")
        mailService.sendMail(mailContext)
        verificationRecovers[dbUser.email!!] = Pair(code, dbUser)
        return true
    }

    fun verifyRecover(userMail: String, verificationCode: String): Boolean {
        val dbUser = userRepo.findByEmail(userMail) ?: return false
        if (verificationRecovers[dbUser.email!!]?.first == verificationCode) {
            verificationRecovers.remove(dbUser.email!!)
            return true
        }
        return false
    }

    fun changePassword(userMail: String, newPassword: String): Boolean {
        val dbUser = userRepo.findByEmail(userMail) ?: return false
        dbUser.password = bCryptPasswordEncoder.encode(newPassword)
        return true
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
        return !userRepo.findById(userId).isEmpty
    }
}