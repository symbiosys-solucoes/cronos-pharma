package br.symbiosys.solucoes.cronospharma.commons

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class AuthorizationTokenService {

    @Value("\${app.petronas.auth.token}")
    lateinit var accessToken: String

        fun validate(header: String) {
        val token = header.replace("Bearer ", "")
        if (token == accessToken) {
            return
        }
        throw TokenException("Invalid token")

    }
}