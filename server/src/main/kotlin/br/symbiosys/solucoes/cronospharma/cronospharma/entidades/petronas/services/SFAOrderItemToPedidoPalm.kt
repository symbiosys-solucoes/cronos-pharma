package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.services

import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.cronos.ItemPedidoPalm
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.request.OrderItem
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.repositories.ItemPedidoPalmPetronas

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
            }
        }
    }
}

