package com.epam.brn.config

import com.epam.brn.constant.BrnPath
import com.epam.brn.constant.BrnPath.CLOUD
import com.epam.brn.constant.BrnPath.FOLDERS
import com.epam.brn.constant.BrnPath.UPLOAD
import com.epam.brn.constant.BrnRoles.ADMIN
import com.epam.brn.constant.BrnRoles.USER
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
@EnableWebSecurity
class WebSecurityBasicConfiguration(
    @Qualifier("brainUpUserDetailService") brainUpUserDetailService: UserDetailsService
) : WebSecurityConfigurerAdapter() {

    private val userDetailsService: UserDetailsService = brainUpUserDetailService

    @Throws(Exception::class)
    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder())
    }

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http.csrf()
            .disable()
            .authorizeRequests()
            .antMatchers(BrnPath.LOGIN).permitAll()
            .antMatchers(BrnPath.REGISTRATION).permitAll()
            .antMatchers("/admin/**").hasRole(ADMIN)
            .antMatchers("/users/current").hasAnyRole(ADMIN, USER)
            .antMatchers("/users/**").hasRole(ADMIN)
            .antMatchers("$CLOUD$UPLOAD").hasRole(ADMIN)
            .antMatchers("$CLOUD$FOLDERS").hasRole(ADMIN)
            .antMatchers("/**").hasAnyRole(ADMIN, USER)
            .and().formLogin()
            .and().httpBasic()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun brnAuthenticationManager(): AuthenticationManager = super.authenticationManagerBean()
}
