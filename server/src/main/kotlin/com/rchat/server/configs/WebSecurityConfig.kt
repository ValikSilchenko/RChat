package com.rchat.server.configs

import com.rchat.server.services.PgUserDetailsService

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder


@Configuration
@EnableWebSecurity
@EnableConfigurationProperties
class WebSecurityConfig(private var userDetailsService: PgUserDetailsService) : WebSecurityConfigurerAdapter() {
    @Override
    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/**", "/user", "/").permitAll()
                .anyRequest().authenticated()
//            .and()
//                .httpBasic()
//            .and()
//                .formLogin()
//                .loginPage("/login")
//                .defaultSuccessUrl("/")  // TODO
//                .permitAll()
//            .and()
//            .logout()
//            .permitAll()
//            .logoutSuccessUrl("/")  // TODO
    }

    @Override
    @Throws(Exception::class)
    override fun configure(builder: AuthenticationManagerBuilder) {
        builder.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder())
        builder.inMemoryAuthentication().passwordEncoder(bCryptPasswordEncoder())
    }

    @Bean
    fun bCryptPasswordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}