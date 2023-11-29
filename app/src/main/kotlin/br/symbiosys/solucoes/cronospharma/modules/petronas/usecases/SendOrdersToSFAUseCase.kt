package br.symbiosys.solucoes.cronospharma.modules.petronas.usecases

import java.time.LocalDate

interface SendOrdersToSFAUseCase {

    fun execute()

    fun execute(initialDate: LocalDate? = null, endDate: LocalDate? = null, erpOrderNumber: String? = null)


    suspend fun executeAsync()

    suspend fun executeAsync(initialDate: LocalDate? = null, endDate: LocalDate? = null, erpOrderNumber: String? = null)

    fun delete(sfaOrderNumber: String)

}