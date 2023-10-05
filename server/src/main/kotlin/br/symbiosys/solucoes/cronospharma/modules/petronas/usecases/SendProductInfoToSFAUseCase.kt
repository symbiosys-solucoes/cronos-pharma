package br.symbiosys.solucoes.cronospharma.modules.petronas.usecases

interface SendProductInfoToSFAUseCase {

    fun info(full: Boolean = false)

    fun prices(full: Boolean = false)

    fun inventory(full: Boolean = false)
}