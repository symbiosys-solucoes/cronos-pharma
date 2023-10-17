package br.symbiosys.solucoes.cronospharma.modules.petronas.usecases.implementations

import br.symbiosys.solucoes.cronospharma.modules.petronas.ports.repositories.PetronasAccountsARRepository
import br.symbiosys.solucoes.cronospharma.modules.petronas.ports.api.ApiPetronasUpsertAccountAR
import br.symbiosys.solucoes.cronospharma.modules.petronas.usecases.SendAccountARsToSFAUseCase
import br.symbiosys.solucoes.cronospharma.sym.gateway.repository.SymErrosRepository
import br.symbiosys.solucoes.cronospharma.sym.model.SymErros
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class SendAccountARsToSFAUseCaseImpl
    (
    private val symErrosRepository: SymErrosRepository,
    private val petronasAccountsARRepository: PetronasAccountsARRepository,
    private val apiPetronasUpsertAccountAR: ApiPetronasUpsertAccountAR
) : SendAccountARsToSFAUseCase {


    val logger = LoggerFactory.getLogger(SendAccountARsToSFAUseCaseImpl::class.java)
    val mapper = ObjectMapper()

    override fun execute() {
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
                body.filter { !it.isCreated && it.isSuccess }.forEach {
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

    @Async
    override suspend fun executeAsync() {
        this.execute()
    }
}
