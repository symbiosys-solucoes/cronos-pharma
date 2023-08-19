package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.services

import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.cronos.ItemPedidoPalm
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.request.OrderItem

class SFAOrderItemToPedidoPalm {

    companion object {
        fun convert(item: OrderItem, idPedidoPalm: Long, sequencia: Int): ItemPedidoPalm {

            return ItemPedidoPalm(
                IdPedidoPalm = idPedidoPalm,
                CodProdutoArq = item.productCodeErp?:"",
                Qtd = item.orderQuantity,
                QtdConfirmada = item.confirmedQuantity,
                PrecoUnit = item.unitPrice.toBigDecimal(),
                PercDescontoItem = item.discountPercentage,
                Item = sequencia
            )


        }
    }
}

