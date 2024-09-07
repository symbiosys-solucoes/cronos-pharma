package br.symbiosys.solucoes.cronospharma.modules.petronas.usecases

import java.time.LocalDate

interface SendInvoicesToSFAUseCase {

    fun execute()
    fun execute(initialDate: LocalDate?, endDate: LocalDate?, erpInvoiceNumber: String?)
}