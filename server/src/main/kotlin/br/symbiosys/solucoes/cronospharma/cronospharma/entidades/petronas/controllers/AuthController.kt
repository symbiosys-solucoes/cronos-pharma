package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.controllers

import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.request.TokenRequest
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.request.TokenResponse
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.services.AuthorizationTokenHandler
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.services.AuthorizationTokenService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.*


@RestController()
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