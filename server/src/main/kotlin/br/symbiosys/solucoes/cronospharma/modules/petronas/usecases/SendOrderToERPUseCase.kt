package br.symbiosys.solucoes.cronospharma.modules.petronas.usecases

import br.symbiosys.solucoes.cronospharma.modules.petronas.models.request.Order
import br.symbiosys.solucoes.cronospharma.modules.petronas.models.response.UpsertResponse

interface SendOrderToERPUseCase {

    fun execute(request: List<Order>): List<UpsertResponse>
}