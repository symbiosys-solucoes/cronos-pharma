package br.symbiosys.solucoes.cronospharma.modules.petronas.usecases

interface SendOrderItemToSFAUseCase {

    fun execute(numeroPedido: String)
}