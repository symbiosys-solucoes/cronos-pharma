package br.symbiosys.solucoes.cronospharma.modules.petronas.usecases

import br.symbiosys.solucoes.cronospharma.modules.petronas.models.request.Accounts

interface SendAccountsToSFAUseCase {

    fun execute(full: Boolean = false)

    suspend fun executeAsync()
    fun sendAccount(code: String): Accounts
}