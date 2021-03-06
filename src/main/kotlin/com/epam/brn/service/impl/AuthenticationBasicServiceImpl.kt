package com.epam.brn.service.impl

import com.epam.brn.dto.LoginDto
import com.epam.brn.dto.UserAccountDto
import com.epam.brn.service.AuthenticationService
import com.epam.brn.service.UserAccountService
import org.apache.logging.log4j.kotlin.logger
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.util.Base64Utils

@Service
class AuthenticationBasicServiceImpl(
    private val userAccountService: UserAccountService,
    private val authenticationManager: AuthenticationManager
) : AuthenticationService {
    private val log = logger()

    override fun registration(userAccountDto: UserAccountDto): String {
        val newUser = userAccountService.addUser(userAccountDto)
        log.info("created new user id=${newUser.id}")
        return login(LoginDto(username = userAccountDto.email, password = userAccountDto.password))
    }

    override fun login(loginDto: LoginDto): String {
        val token = UsernamePasswordAuthenticationToken(loginDto.username, loginDto.password)
        val auth: Authentication = authenticationManager.authenticate(token)
        SecurityContextHolder.getContext().authentication = auth
        return getBasicHeader(loginDto.username, loginDto.password)
    }

    fun getBasicHeader(userName: String, password: String) =
        Base64Utils.encodeToString("$userName:$password".toByteArray())
}
