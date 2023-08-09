package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas

import java.time.LocalDateTime

class PetronasAuthUtility {

    companion object {
        var accessToken: String = ""

        var expireDate: LocalDateTime? = null

        var instanceUrl: String = ""


    }


}