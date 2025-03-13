package br.symbiosys.solucoes.cronospharma.modules.petronas.ports.repositories

import br.symbiosys.solucoes.cronospharma.modules.petronas.models.request.Order
import br.symbiosys.solucoes.cronospharma.modules.petronas.models.request.OrderItem
import br.symbiosys.solucoes.cronospharma.modules.petronas.ports.repositories.dao.ItemPedidoPalmPetronas
import br.symbiosys.solucoes.cronospharma.modules.petronas.ports.repositories.dao.PedidoPalmPetronas
import java.time.LocalDate

interface PedidoPalmPetronasRepository {

    fun findItems(numPedidoCronos: String, cancelados: Boolean = false): List<OrderItem>
    fun markAsSent(idItem: Int, SFAOrderItemNumber: String)
    fun markAsSent(numPedidoCronos: String, atualizado: Boolean = false, distribuidor: String)
    fun findAll(enviados: Boolean = false): List<Order>
    fun save(pedido: PedidoPalmPetronas): PedidoPalmPetronas
    fun save(item: ItemPedidoPalmPetronas, numeroPedidoPetronas: String, codigoFilial: String): ItemPedidoPalmPetronas
    fun toMovimento(pedido: PedidoPalmPetronas)
    fun markAsSent(idMovCronos: Int)

    fun convertAll()
    fun findAll(enviados: LocalDate?, endDate: LocalDate?, erpOrderNumber: String?): List<Order>

    fun deletePedidoPalmPetronas(numPedido: String)

}
