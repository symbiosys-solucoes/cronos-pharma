package br.symbiosys.solucoes.cronospharma.modules.petronas.usecases

import java.time.LocalDate

interface SendOrdersToSFAUseCase {

    fun execute()

    fun execute(initialDate: LocalDate? = null, endDate: LocalDate? = null, erpOrderNumber: String? = null)
}