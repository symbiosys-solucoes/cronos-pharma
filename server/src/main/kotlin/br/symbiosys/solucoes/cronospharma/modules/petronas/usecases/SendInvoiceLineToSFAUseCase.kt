package br.symbiosys.solucoes.cronospharma.modules.petronas.usecases

interface SendInvoiceLineToSFAUseCase {

    fun execute(sfaId: String)
}