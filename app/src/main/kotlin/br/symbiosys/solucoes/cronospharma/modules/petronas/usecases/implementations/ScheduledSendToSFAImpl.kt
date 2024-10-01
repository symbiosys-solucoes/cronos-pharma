package br.symbiosys.solucoes.cronospharma.modules.petronas.usecases.implementations

import br.symbiosys.solucoes.cronospharma.modules.petronas.ports.repositories.PedidoPalmPetronasRepository
import br.symbiosys.solucoes.cronospharma.modules.petronas.usecases.*
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
@EnableScheduling
class ScheduledSendToSFAImpl
    (
    private val sendAccountsToSFAUseCase: SendAccountsToSFAUseCase,
    private val sendAccountARsToSFAUseCase: SendAccountARsToSFAUseCase,
    private val sendInvoicesToSFAUseCase: SendInvoicesToSFAUseCase,
    private val sendOrdersToSFAUseCase: SendOrdersToSFAUseCase,
    private val sendProductInfoToSFAUseCase: SendProductInfoToSFAUseCase,
    private val pedidoPalmPetronasRepository: PedidoPalmPetronasRepository

) : ScheduledSendToSFA {


    @Scheduled(cron = "\${app.cron.petronas.envia.geral}")
    override fun execute() {
        sendAccountsToSFAUseCase.execute()
        sendProductInfoToSFAUseCase.info()
        sendProductInfoToSFAUseCase.prices()
        sendProductInfoToSFAUseCase.inventory()

    }

    @Scheduled(cron = "\${app.cron.petronas.envia.pedidos}")
    fun sendOrdersAndARs(){
        sendOrdersToSFAUseCase.execute()
        sendInvoicesToSFAUseCase.execute()
        sendAccountARsToSFAUseCase.execute()
    }

    @Scheduled(cron = "\${app.cron.petronas.envia.notas}")
    fun sendOrdersInvoicesAndARs(){
        sendInvoicesToSFAUseCase.execute()
    }

    @Scheduled(cron = "\${app.cron.petronas.converte.pedido}")
    fun convertOrderToMovimento() {
        pedidoPalmPetronasRepository.convertAll()
    }

}