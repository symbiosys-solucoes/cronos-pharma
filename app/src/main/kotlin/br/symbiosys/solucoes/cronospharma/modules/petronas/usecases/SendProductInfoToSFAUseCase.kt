package br.symbiosys.solucoes.cronospharma.modules.petronas.usecases

interface SendProductInfoToSFAUseCase {

    fun info(full: Boolean = false)

    suspend fun infoAsync(full: Boolean = false)

    fun prices(full: Boolean = false)

    suspend fun pricesAsync(full: Boolean = false)

    fun inventory(full: Boolean = false)

    suspend fun inventoryAsync(full: Boolean = false)
}