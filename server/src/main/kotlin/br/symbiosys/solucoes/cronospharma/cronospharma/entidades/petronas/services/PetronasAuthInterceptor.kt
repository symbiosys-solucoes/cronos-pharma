package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.services

import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.PetronasAuthUtility
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.api.ApiPetronasAuth
import feign.RequestInterceptor
import feign.RequestTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class PetronasAuthInterceptor: RequestInterceptor {

    @Autowired
    lateinit var apiPetronasAuth: ApiPetronasAuth

    override fun apply(template: RequestTemplate?) {

        if (PetronasAuthUtility.expireDate == null) {
            setAcessToken()
        }
        if (PetronasAuthUtility.expireDate!!.isBefore(LocalDateTime.now())) {
            setAcessToken()
        }

        if (template != null) {
            template.header("Authorization", "Bearer " + PetronasAuthUtility.accessToken)
        }
    }

    private fun setAcessToken() {
        val response = apiPetronasAuth.getAccessToken()
        if (response.statusCode == HttpStatus.OK){
            val body = response.body!!
            PetronasAuthUtility.accessToken = body.accessToken
            PetronasAuthUtility.instanceUrl = body.instanceUrl
            PetronasAuthUtility.expireDate = LocalDateTime.now().plusSeconds(7000)
        }

    }

}