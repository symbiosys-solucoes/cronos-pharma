package br.symbiosys.solucoes.cronospharma.modules.petronas.usecases.implementations

import br.symbiosys.solucoes.cronospharma.modules.petronas.exceptions.PetronasException
import br.symbiosys.solucoes.cronospharma.modules.petronas.models.request.Accounts
import br.symbiosys.solucoes.cronospharma.modules.petronas.models.response.UpsertResponse
import br.symbiosys.solucoes.cronospharma.modules.petronas.ports.api.ApiPetronasUpsertAccounts
import br.symbiosys.solucoes.cronospharma.modules.petronas.ports.repositories.PetronasAccountsRepository
import br.symbiosys.solucoes.cronospharma.modules.petronas.usecases.SendAccountsToSFAUseCase
import br.symbiosys.solucoes.cronospharma.sym.gateway.repository.SymErrosRepository
import br.symbiosys.solucoes.cronospharma.sym.model.SymErros
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class SendAccountsToSFAUseCaseImpl(
    private val symErrosRepository: SymErrosRepository,
    private val petronasAccountsRepository: PetronasAccountsRepository,
    private val apiPetronasUpsertAccounts: ApiPetronasUpsertAccounts,
) : SendAccountsToSFAUseCase {
    val logger = LoggerFactory.getLogger(SendAccountsToSFAUseCaseImpl::class.java)
    val mapper = ObjectMapper()

    override fun execute(full: Boolean) {
        val customers = petronasAccountsRepository.findAll(full)
        logger.info("Foram encontrados ${customers.size} clientes")

        var i = 1
        val erros = mutableListOf<SymErros>()
        val accounts = customers.chunked(200).toList()
        for (request in accounts) {
            logger.info("enviando request {$i} de ${accounts.size} para SFA")
            i++
            try {
                val response = apiPetronasUpsertAccounts.upsertAccounts(request)
                if (response.statusCode == HttpStatus.OK) {
                    val body = response.body!!
                    body.filter { it.isSuccess && it.isCreated }.forEach {
                        val accountNumber = it.externalId!!.split("-")[1]
                        petronasAccountsRepository.markAsCreated(it.sfdcId!!, accountNumber)
                    }
                    body.filter { !it.isCreated && it.isSuccess }.forEach {
                        val accountNumber = it.externalId!!.split("-")[1]
                        petronasAccountsRepository.markAsCreated(it.sfdcId!!, accountNumber)
                    }
                    body.filter { !it.isSuccess }.forEach {
                        logger.error("cliente ${it.externalId} não foi atualizado nem cadastrado")
                        erros.add(
                            SymErros().apply {
                                dataOperacao = LocalDateTime.now()
                                tipoOperacao = "CADASTRO CLIENTE SFA"
                                petronasResponse = mapper.writeValueAsString(it)
                            },
                        )
                    }
                }
                symErrosRepository.saveAll(erros)
                erros.clear()
            } catch (e: Exception) {
                logger.error("erro ao enviar request {$i} de ${accounts.size} para SFA", e)
                symErrosRepository.save(
                    SymErros().apply {
                        dataOperacao = LocalDateTime.now()
                        tipoOperacao = "CADASTRO CLIENTE SFA"
                        petronasResponse = mapper.writeValueAsString(e.message)
                    },
                )
            }
        }
    }

    @Async
    override suspend fun executeAsync() {
        this.execute(full = true)
    }

    override fun sendAccount(code: String): Accounts {
        val customer = petronasAccountsRepository.findByCode(code)
        logger.info("Cliente com o codigo $code foi encontrado")
        logger.info("enviando request de cliente $code para SFA")

        val response = apiPetronasUpsertAccounts.upsertAccounts(listOf(customer))
        val result = handleSfaResponse(response)
        if (!result.first().isSuccess) throw PetronasException(result.first().errors)
        return petronasAccountsRepository.findByCode(code)
    }

    private fun handleSfaResponse(
        response: ResponseEntity<List<UpsertResponse>>,
        code: String? = null,
    ): MutableList<UpsertResponse> {
        val result = mutableListOf<UpsertResponse>()
        try {
            if (response.statusCode == HttpStatus.OK) {
                val body = response.body!!
                body.filter { it.isSuccess && it.isCreated }.forEach {
                    val accountNumber = it.externalId!!.split("-")[1]
                    result.add(it)
                    petronasAccountsRepository.markAsCreated(it.sfdcId!!, accountNumber)
                }
                body.filter { !it.isCreated && it.isSuccess }.forEach {
                    val accountNumber = it.externalId!!.split("-")[1]
                    result.add(it)
                    petronasAccountsRepository.markAsCreated(it.sfdcId!!, accountNumber)
                }
                body.filter { !it.isSuccess }.forEach {
                    logger.error("cliente ${it.externalId} não foi atualizado nem cadastrado")
                    result.add(it)
                    symErrosRepository.save(
                        SymErros().apply {
                            dataOperacao = LocalDateTime.now()
                            tipoOperacao = "CADASTRO CLIENTE SFA"
                            petronasResponse = mapper.writeValueAsString(it)
                            cronosId = it.externalId!!.split("-")[1]
                        },
                    )
                }
            }
        } catch (e: Exception) {
            logger.error("erro ao enviar cliente para SFA", e)
            symErrosRepository.save(
                SymErros().apply {
                    dataOperacao = LocalDateTime.now()
                    tipoOperacao = "CADASTRO CLIENTE SFA"
                    petronasResponse = mapper.writeValueAsString(e.message)
                    cronosId = code
                },
            )
        }
        return result
    }
}
