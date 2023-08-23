package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.services

import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.cronos.ProdutosRepository
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.api.ApiPetronasUpsertProducts
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.KeyProductsInventory
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.request.KeyProducts
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.request.Products
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
            productCode = it.codigoProduto
            basePrice = when (it.activePrice) {
                "1" -> it.preco1
                "2" -> it.preco2
                "3" -> it.preco3
                "4" -> it.preco4
                "5" -> it.preco5
                "6" -> it.preco6
                else -> {it.preco1}
            }
            maxDiscount = it.descontoMaximo
            promotionPrice = if(it.promocaoAtiva) it.preco6 else 0.0
            promotionStartDate = it.dataInicioPromocao!!.toLocalDate()
            promotionEndDate = it.dataFimPromocao.toLocalDate()
            active = true
            dtCode = parametros.codigoDistribuidorPetronas
            priceBookName = it.nomeTabela

        }}.chunked(50).toList()

        for (request in tabelas) {
            val response = apiPetronasUpsertProducts.upsertKeyProducts(request)
            logger.info(response.body!!.get(0).toString())
        }
    }

    fun sendKeyProductsInventoryToSFA(parametros: SymParametros) {
        val estoques = produtosRepository.findEstoqueByCodFilialAndCodLocal(parametros.codigoFilial?: "01", parametros.codigoLocal ?: "01").map {
            KeyProductsInventory().apply {
                productCode = it.codigoProduto
                inventoryQuantity = it.sdoAtual
                dtCode = parametros.codigoDistribuidorPetronas
                active = true
            }
        }.chunked(50).toList()

        for (request in estoques) {
            val response = apiPetronasUpsertProducts.upsertKeyProductsInventory(request)
            logger.info(response.body!!.get(0).toString())
        }
    }
}