package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.services

import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.cronos.ProdutosRepository
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.api.ApiPetronasUpsertProducts
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.KeyProductsInventory
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.request.KeyProducts
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.request.Products
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.response.UpsertResponse
import br.symbiosys.solucoes.cronospharma.cronospharma.sym.model.SymParametros
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class ProductsService  {

    private val logger = LoggerFactory.getLogger(ProductsService::class.java)

    @Autowired
    lateinit var produtosRepository: ProdutosRepository

    @Autowired
    lateinit var apiPetronasUpsertProducts: ApiPetronasUpsertProducts

    fun sendProductsToSFA(parametros: SymParametros) {
       val products = produtosRepository.findAll().map {
            Products().apply {
                productCode = it.codProduto
                dtCode = parametros.codigoDistribuidorPetronas
                name = it.nomeProduto
                description = it.descricaoProduto
                unid = it.unidadeMedida
                unidQuantity = it.volume.toString() + "L"
                category = it.codigoGrupo
                brand = it.fabricante
                manufacture = "NON PETRONAS"
                active = it.ativo
            }
        }.chunked(50).toList()

        for (p in products) {
           val response = apiPetronasUpsertProducts.upsertProducts(p)
            response.body?.filter { it.isSuccess && it.isCreated }?.forEach {
                val codProduto = it.externalId?.replace(parametros.codigoDistribuidorPetronas + "-", "")
                if (codProduto != null) {
                    produtosRepository.updateSalesID(codProduto, it.sfdcId!!)
                }
            }
        }

    }

    fun sendKeyProductsToSFA(parametros: SymParametros) {
        val tabelas = produtosRepository.findPrecos().map { KeyProducts().apply {
            productCode = if(it.fabricante == "PETRONAS") it.referenciaFabricante else it.codigoProduto
            basePrice = it.preco1
            manufacture = if(it.fabricante == "PETRONAS") "PETRONAS" else "NON PETRONAS"
            maxDiscount = it.descontoMaximo
            active = true
            dtCode = parametros.codigoDistribuidorPetronas
            priceBookName = "GeneralPriceBook"

        }}.chunked(50).toList()
        println(tabelas.size)
        for (request in tabelas) {
            val response = apiPetronasUpsertProducts.upsertKeyProducts(request)
            logger.info(response.body!!.get(0).toString())
        }
    }

    fun sendKeyProductsInventoryToSFA(parametros: SymParametros): MutableList<List<UpsertResponse>> {
        val estoques = produtosRepository.findEstoqueByCodFilialAndCodLocal(parametros.codigoFilial?: "01", parametros.codigoLocal ?: "01")
            .map {
            KeyProductsInventory().apply {
                productCode = if(it.fabricante == "PETRONAS") it.referenciaFabricante else it.codigoProduto
                inventoryQuantity = it.sdoAtual
                dtCode = parametros.codigoDistribuidorPetronas
                active = true
            }
        }.chunked(50).toList()
        var payload = mutableListOf<List<UpsertResponse>>()
        for (request in estoques) {
            val response = apiPetronasUpsertProducts.upsertKeyProductsInventory(request)
            payload.add(response.body!!)
            logger.info(response.body!!.get(0).toString())
        }
        return payload
    }
}