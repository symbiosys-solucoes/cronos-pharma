package br.symbiosys.solucoes.cronospharma.modules.petronas.usecases

import br.symbiosys.solucoes.cronospharma.modules.petronas.models.request.OrderItem
import br.symbiosys.solucoes.cronospharma.modules.petronas.models.response.UpsertResponse

interface SendOrderItemToERPUseCase {

    fun execute (request: List<OrderItem>): List<UpsertResponse>
}