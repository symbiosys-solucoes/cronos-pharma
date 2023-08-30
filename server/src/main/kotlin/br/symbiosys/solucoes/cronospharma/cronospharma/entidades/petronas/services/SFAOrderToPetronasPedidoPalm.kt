package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.services

import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.request.Order
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.repositories.PedidoPalmPetronas
import br.symbiosys.solucoes.cronospharma.cronospharma.sym.model.SymParametros
import java.time.LocalDateTime

class SFAOrderToPetronasPedidoPalm {

    companion object {
        fun convert(order: Order, symParametros: SymParametros): PedidoPalmPetronas {

            return PedidoPalmPetronas().apply {
                logImportacao = order.salesForceId
                codigoFilial = symParametros.codigoFilial
                numeroPedido = order.orderNumberSfa
                codigoCliente = order.accountNumber
                dataPedido = order.orderDate ?: LocalDateTime.now()
                codigoPortador = order.paymentMethod?:""
                condicaoPagamento = order.paymentKeyTerms?:""
                dataEntrega = order.expectedDeliveryDate
                numeroPedidoPalmAux = order.customerOrderNumber
                codigoVendedor = order.userCode
                totalPedido = order.totalAmount
                itens = mutableListOf()
            }

        }
    }
}
