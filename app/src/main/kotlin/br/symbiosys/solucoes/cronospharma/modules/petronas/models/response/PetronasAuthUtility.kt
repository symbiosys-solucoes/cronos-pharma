package br.symbiosys.solucoes.cronospharma.modules.petronas.models.response

import java.time.LocalDateTime

class PetronasAuthUtility {

    companion object {
        var accessToken: String = ""

        var expireDate: LocalDateTime? = null

        var instanceUrl: String = ""


    }


}