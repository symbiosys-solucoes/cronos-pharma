package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.services

import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.cronos.PedidoPalm
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.request.Order

class SFAOrderToPedidoPalm {

    companion object {
        fun convert(order: Order): PedidoPalm {

            return PedidoPalm(
                Origem = "PETRONAS",
                CodFilial = "01",
                NumPedidoPalm = order.orderNumberSfa?:"",
                CodCliFor = order.accountNumber,
                CnpjCpfCliFor = order.accountNumber,
                DataPedido = order.orderDate,
                CodPortador = order.paymentMethod?:"",
                CodCondPag = order.paymentKeyTerms?:"",
                DataEntrega = order.expectedDeliveryDate,
                NumPedidoPalmAux = order.customerOrderNumber,
                CodVendedor = order.userCode,
                TotalPedido = order.totalAmount.toBigDecimal(),
                itens = mutableListOf()

            )
        }
    }
}

