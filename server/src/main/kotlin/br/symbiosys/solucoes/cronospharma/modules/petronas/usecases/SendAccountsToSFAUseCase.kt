package br.symbiosys.solucoes.cronospharma.modules.petronas.usecases

interface SendAccountsToSFAUseCase {

    fun execute(full: Boolean = false)

    suspend fun executeAsync()
}