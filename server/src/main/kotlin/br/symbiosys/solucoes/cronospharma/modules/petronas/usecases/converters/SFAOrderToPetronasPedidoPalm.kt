package br.symbiosys.solucoes.cronospharma.modules.petronas.usecases.converters

import br.symbiosys.solucoes.cronospharma.modules.petronas.models.request.Order
import br.symbiosys.solucoes.cronospharma.modules.petronas.ports.repositories.dao.PedidoPalmPetronas
import br.symbiosys.solucoes.cronospharma.sym.model.SymParametros
import java.time.LocalDateTime

class SFAOrderToPetronasPedidoPalm {
    companion object {
        fun convert(order: Order, symParametros: SymParametros): PedidoPalmPetronas {

            return PedidoPalmPetronas().apply {
                logImportacao = order.salesForceId
                codigoFilial = symParametros.codigoFilial
                numeroPedido = order.orderNumberSfa
                codigoCliente = order.accountNumber
                dataPedido = order.orderDate?.split("T")?.get(0)
                codigoPortador = order.paymentMethod?:""
                condicaoPagamento = order.paymentKeyTerms?:""
                dataEntrega = if(!order.expectedDeliveryDate.isNullOrBlank()){ LocalDateTime.parse(order.expectedDeliveryDate!!.split("+").get(0))} else null
                numeroPedidoPalmAux = order.customerOrderNumber
                codigoVendedor = order.userCode?.split("-")?.get(1)
                totalPedido = order.totalAmount
                observacoes = order.driverMessage?.substring(0,400)
                itens = mutableListOf()
            }

        }
    }
}

