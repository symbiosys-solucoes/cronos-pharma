package br.symbiosys.solucoes.cronospharma.modules.petronas.usecases.implementations

import br.symbiosys.solucoes.cronospharma.modules.petronas.ports.repositories.PetronasAccountsRepository
import br.symbiosys.solucoes.cronospharma.modules.petronas.ports.api.ApiPetronasUpsertAccounts
import br.symbiosys.solucoes.cronospharma.modules.petronas.usecases.SendAccountsToSFAUseCase
import br.symbiosys.solucoes.cronospharma.sym.gateway.repository.SymErrosRepository
import br.symbiosys.solucoes.cronospharma.sym.model.SymErros
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class SendAccountsToSFAUseCaseImpl(
    private val symErrosRepository: SymErrosRepository,
    private val petronasAccountsRepository: PetronasAccountsRepository,
    private val apiPetronasUpsertAccounts: ApiPetronasUpsertAccounts
) : SendAccountsToSFAUseCase {


    val logger = LoggerFactory.getLogger(SendAccountsToSFAUseCaseImpl::class.java)
    val mapper = ObjectMapper()


    override fun execute() {
        val customers = petronasAccountsRepository.findAll()
        logger.info("Foram encontrados ${customers.size} clientes")

        var i = 1
        val erros = mutableListOf<SymErros>()
        val accounts = customers.chunked(50).toList()
        for (request in accounts) {
            logger.info("enviando request {$i} de ${accounts.size} para SFA")
            i++
            val response = apiPetronasUpsertAccounts.upsertAccounts(request)
            if (response.statusCode == HttpStatus.OK) {
                val body = response.body!!
                body.filter { it.isSuccess && it.isCreated }.forEach {

                    val accountNumber = it.externalId!!.split("-")[1]
                    logger.info("cliente ${it.externalId} cadastrado com sucesso")
                    petronasAccountsRepository.markAsCreated(it.sfdcId!!, accountNumber)

                }
                body.filter { !it.isCreated && it.isSuccess }.forEach {
                    val accountNumber = it.externalId!!.split("-")[1]
                    logger.info("cliente ${it.externalId} atualizado com sucesso")
                    petronasAccountsRepository.markAsCreated(it.sfdcId!!, accountNumber)
                }
                body.filter { !it.isSuccess }.forEach {
                    logger.error("cliente ${it.externalId} não foi atualizado nem cadastrado")
                    erros.add(SymErros().apply {
                        dataOperacao = LocalDateTime.now()
                        tipoOperacao = "CADASTRO CLIENTE SFA"
                        petronasResponse = mapper.writeValueAsString(it)
                    })
                }
            }
        }

        symErrosRepository.saveAll(erros)
    }
}
