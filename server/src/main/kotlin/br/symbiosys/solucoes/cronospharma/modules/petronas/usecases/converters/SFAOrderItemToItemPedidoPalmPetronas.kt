package br.symbiosys.solucoes.cronospharma.modules.petronas.usecases.converters

import br.symbiosys.solucoes.cronospharma.modules.petronas.models.request.OrderItem
import br.symbiosys.solucoes.cronospharma.modules.petronas.ports.repositories.dao.ItemPedidoPalmPetronas

class SFAOrderItemToItemPedidoPalmPetronas {

    companion object {
        fun convert(item: OrderItem, sequencia: Int): ItemPedidoPalmPetronas {
            return ItemPedidoPalmPetronas().apply {
                codigoProdutoArquivo = item.productCodeErp ?: ""
                quantidadeSolicitada = item.orderQuantity
                precoUnitario = item.unitPrice
                percentualDesconto = item.discountPercentage
                sequencialItem = sequencia
                idPrecoTabela = 1
                logImportacao = item.orderItemNumberSfa
                descricaoRetornoItem = item.manufacturer
                codigoRetornoItem = item.sfaOrderItemId
            }
        }
    }
}

