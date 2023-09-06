package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.services

import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.cronos.ProdutosRepository
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.api.ApiPetronasUpsertProducts
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.KeyProductsInventory
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.request.KeyProducts
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.request.Products
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.response.UpsertResponse
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.repositories.ProductsInventoryPetronasRepository
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.repositories.ProductsKeyPetronasRepository
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.repositories.ProductsPetronasRepository
import br.symbiosys.solucoes.cronospharma.cronospharma.sym.gateway.repository.SymErrosRepository
import br.symbiosys.solucoes.cronospharma.cronospharma.sym.model.SymErros
import br.symbiosys.solucoes.cronospharma.cronospharma.sym.model.SymParametros
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class ProductsService {

    private val logger = LoggerFactory.getLogger(ProductsService::class.java)

    @Autowired
    lateinit var productsRepository: ProductsPetronasRepository

    @Autowired
    lateinit var keyPetronasRepository: ProductsKeyPetronasRepository

    @Autowired
    lateinit var productsInventoryPetronasRepository: ProductsInventoryPetronasRepository

    @Autowired
    lateinit var symErrosRepository: SymErrosRepository

    @Autowired
    lateinit var apiPetronasUpsertProducts: ApiPetronasUpsertProducts

    val mapper = ObjectMapper()

    fun sendProductsToSFA() {
        val products = productsRepository.findAll().chunked(50).toList()
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

    fun sendKeyProductsToSFA() {
        val keyProducts = keyPetronasRepository.findAll().chunked(50).toList()
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

    fun sendKeyProductsInventoryToSFA(){
        val estoques = productsInventoryPetronasRepository.findAll().chunked(50).toList()

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
}