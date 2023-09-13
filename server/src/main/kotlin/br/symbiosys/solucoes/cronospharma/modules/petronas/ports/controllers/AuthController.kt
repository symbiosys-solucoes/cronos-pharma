package br.symbiosys.solucoes.cronospharma.modules.petronas.ports.controllers

import br.symbiosys.solucoes.cronospharma.modules.petronas.models.request.TokenRequest
import br.symbiosys.solucoes.cronospharma.modules.petronas.models.request.TokenResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/petronas/token")
class AuthController {

    @Value("\${app.petronas.auth.user}")
    lateinit var petronasUser: String

    @Value("\${app.petronas.auth.password}")
    lateinit var petronasPassword: String

    @Value("\${app.petronas.auth.token}")
    lateinit var petronasToken: String


    @PostMapping
    fun generateToken(@RequestBody tokenRequest: TokenRequest): TokenResponse {

        return if (tokenRequest.username == petronasUser && tokenRequest.password == petronasPassword) {
            TokenResponse().apply { token = petronasToken }
        } else {
            TokenResponse().apply { error = "invalide username or password" }
        }
    }

}