package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.services

import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.api.ApiPetronasUpsertAccountAR
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.repositories.PetronasAccountsARRepository
import br.symbiosys.solucoes.cronospharma.cronospharma.sym.gateway.repository.SymErrosRepository
import br.symbiosys.solucoes.cronospharma.cronospharma.sym.model.SymErros
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class AccountsARService {

    @Autowired
    private lateinit var symErrosRepository: SymErrosRepository

    @Autowired
    private lateinit var petronasAccountsARRepository: PetronasAccountsARRepository

    @Autowired
    private lateinit var apiPetronasUpsertAccountAR: ApiPetronasUpsertAccountAR

    val logger = LoggerFactory.getLogger(AccountsARService::class.java)
    val mapper = ObjectMapper()

    fun sendAccountARToSfa() {
        val titulos = petronasAccountsARRepository.findAll()
        logger.info("Foram encontrados ${titulos.size} titulos")

        var i = 1
        val erros = mutableListOf<SymErros>()
        val accounts = titulos.chunked(50).toList()
        for (request in accounts) {
            logger.info("enviando request {$i} de ${accounts.size} para SFA")
            i++
            val response = apiPetronasUpsertAccountAR.upsertAccountAr(request)
            if (response.statusCode == HttpStatus.OK) {
                val body = response.body!!
                body.filter { it.isSuccess && it.isCreated }.forEach {
                    val idCpr = it.externalId!!.split("|")[1].toInt()
                    logger.info("AR ${it.externalId} cadastrado com sucesso")
                    petronasAccountsARRepository.markAsCreated(it.sfdcId!!, idCpr)
                }
                body.filter { !it.isCreated  && it.isSuccess }.forEach {
                    val idCpr = it.externalId!!.split("|")[1].toInt()
                    logger.info("AR ${it.externalId} atualizado com sucesso")
                    petronasAccountsARRepository.markAsUpdated(it.sfdcId!!, idCpr)
                }
                body.filter { !it.isSuccess }.forEach {
                    logger.error("AR ${it.externalId} não foi atualizado nem cadastrado")
                    erros.add(SymErros().apply {
                        dataOperacao = LocalDateTime.now()
                        tipoOperacao = "CADASTRO CPR SFA"
                        petronasResponse = mapper.writeValueAsString(it)
                    })
                }
            }
        }

        symErrosRepository.saveAll(erros)
    }
}
