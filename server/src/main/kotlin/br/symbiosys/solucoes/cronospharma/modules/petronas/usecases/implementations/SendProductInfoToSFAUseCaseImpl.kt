package br.symbiosys.solucoes.cronospharma.modules.petronas.usecases.implementations

import br.symbiosys.solucoes.cronospharma.modules.petronas.ports.api.ApiPetronasUpsertProducts
import br.symbiosys.solucoes.cronospharma.modules.petronas.ports.repositories.ProductsInventoryPetronasRepository
import br.symbiosys.solucoes.cronospharma.modules.petronas.ports.repositories.ProductsKeyPetronasRepository
import br.symbiosys.solucoes.cronospharma.modules.petronas.ports.repositories.ProductsPetronasRepository
import br.symbiosys.solucoes.cronospharma.modules.petronas.usecases.SendProductInfoToSFAUseCase
import br.symbiosys.solucoes.cronospharma.sym.gateway.repository.SymErrosRepository
import br.symbiosys.solucoes.cronospharma.sym.model.SymErros
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class SendProductInfoToSFAUseCaseImpl (
    private val productsRepository: ProductsPetronasRepository,
    private val keyPetronasRepository: ProductsKeyPetronasRepository,
    private val productsInventoryPetronasRepository: ProductsInventoryPetronasRepository,
    private val symErrosRepository: SymErrosRepository,
    private val apiPetronasUpsertProducts: ApiPetronasUpsertProducts
) : SendProductInfoToSFAUseCase {

    private val logger = LoggerFactory.getLogger(SendProductInfoToSFAUseCase::class.java)

    val mapper = ObjectMapper()


    override  fun info(full: Boolean)  {
        val products = productsRepository.findAll(full).chunked(100).toList()
        var i = 1
        val erros = mutableListOf<SymErros>()
        for (p in products) {
            logger.info("enviando request {$i} de ${products.size} para SFA")
            i++
            val response = apiPetronasUpsertProducts.upsertProducts(p)
            val body = response.body!!
            body.filter { it.isSuccess && it.isCreated }.forEach {
                val codProduto = it.externalId!!.split("-")[1]
                logger.info("Produto criado com sucesso no SFA: $codProduto")
                productsRepository.markAsCreated(codProduto)
            }
            body.filter { it.isSuccess && !it.isCreated }.forEach {
                val codProduto = it.externalId!!.split("-")[1]
                logger.info("Produto atualizado com sucesso no SFA: $codProduto")
                productsRepository.markAsUpdated(codProduto)
            }
            body.filter { !it.isSuccess }.forEach {
                logger.error("produto ${it.externalId} não foi atualizado nem cadastrado")
                erros.add(SymErros().apply {
                    dataOperacao = LocalDateTime.now()
                    tipoOperacao = "CADASTRO PRODUTO SFA"
                    petronasResponse = mapper.writeValueAsString(it)
                })
            }
        }
        symErrosRepository.saveAll(erros)

    }

    @Async
    override suspend fun infoAsync(full: Boolean) {
        this.info(full)
    }

    override fun prices(full: Boolean) {
        val keyProducts = keyPetronasRepository.findAll().chunked(100).toList()
        var i = 1
        val erros = mutableListOf<SymErros>()
        for (request in keyProducts) {
            val response = apiPetronasUpsertProducts.upsertKeyProducts(request)
            val body = response.body!!
            body.filter { it.isSuccess && it.isCreated }.forEach {
                val codProduto = it.externalId!!.split("-")[1]
                logger.info("Preco criado com sucesso no SFA: $codProduto")
                keyPetronasRepository.markAsCreated(codProduto)
            }
            body.filter { it.isSuccess && !it.isCreated }.forEach {
                val codProduto = it.externalId!!.split("-")[1]
                logger.info("Preco atualizado com sucesso no SFA: $codProduto")
                keyPetronasRepository.markAsUpdated(codProduto)
            }
            body.filter { !it.isSuccess }.forEach {
                logger.error("Preco ${it.externalId} não foi atualizado nem cadastrado")
                erros.add(SymErros().apply {
                    dataOperacao = LocalDateTime.now()
                    tipoOperacao = "CADASTRO PRECO SFA"
                    petronasResponse = mapper.writeValueAsString(it)
                })
            }
        }
        symErrosRepository.saveAll(erros)
    }

    @Async
    override suspend fun pricesAsync(full: Boolean) {
        this.prices(full)
    }


    override fun inventory(full: Boolean){
        val estoques = productsInventoryPetronasRepository.findAll().chunked(100).toList()

        var i = 1
        val erros = mutableListOf<SymErros>()
        for (request in estoques) {
            logger.info("enviando request {$i} de ${estoques.size} para SFA")
            i++
            val response = apiPetronasUpsertProducts.upsertKeyProductsInventory(request)
            val body = response.body!!
            body.filter { it.isSuccess }.forEach {
                logger.info("Estoque atualizado com sucesso no SFA: ${it.externalId}")
            }
            body.filter { !it.isSuccess }.forEach {
                logger.error("Preco ${it.externalId} não foi atualizado nem cadastrado")
                erros.add(SymErros().apply {
                    dataOperacao = LocalDateTime.now()
                    tipoOperacao = "CADASTRO ESTOQUE SFA"
                    petronasResponse = mapper.writeValueAsString(it)

                })
            }
        }
        symErrosRepository.saveAll(erros)
    }

    @Async
    override suspend fun inventoryAsync(full: Boolean) {
        this.inventory(full)
    }

}