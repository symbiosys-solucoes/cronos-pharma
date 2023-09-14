package br.symbiosys.solucoes.cronospharma.modules.petronas.ports.repositories

import br.symbiosys.solucoes.cronospharma.modules.petronas.models.request.Order
import br.symbiosys.solucoes.cronospharma.modules.petronas.models.request.OrderItem
import br.symbiosys.solucoes.cronospharma.modules.petronas.ports.repositories.dao.ItemPedidoPalmPetronas
import br.symbiosys.solucoes.cronospharma.modules.petronas.ports.repositories.dao.PedidoPalmPetronas
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import java.sql.ResultSet

interface PedidoPalmPetronasRepository {

    fun findItems(numPedidoCronos: String): List<OrderItem>
    fun markAsSent(idItem: Int, SFAOrderItemNumber: String)
    fun markAsSent(numPedidoCronos: String, atualizado: Boolean = false)
    fun findAll(enviados: Boolean = false): List<Order>
    fun save(pedido: PedidoPalmPetronas): PedidoPalmPetronas
    fun save(item: ItemPedidoPalmPetronas, numeroPedidoPetronas: String, codigoFilial: String): ItemPedidoPalmPetronas
    fun toMovimento(pedido: PedidoPalmPetronas)

    fun convertAll()

}
