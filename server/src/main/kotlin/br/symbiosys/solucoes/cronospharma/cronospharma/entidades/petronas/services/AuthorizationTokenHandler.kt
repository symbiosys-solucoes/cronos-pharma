package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.services

import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.request.TokenResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class AuthorizationTokenHandler {


    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(TokenException::class)
    fun handle(e: TokenException): TokenResponse {
        return TokenResponse().apply { error = "Invalid Token"}
    }



}