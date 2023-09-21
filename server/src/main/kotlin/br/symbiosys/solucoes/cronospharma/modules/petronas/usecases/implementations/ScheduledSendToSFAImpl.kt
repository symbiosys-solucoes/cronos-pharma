package br.symbiosys.solucoes.cronospharma.modules.petronas.usecases.implementations

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

) : ScheduledSendToSFA {


    @Scheduled(cron = "\${app.cron.petronas.envia.geral}")
    override fun execute() {
        sendAccountsToSFAUseCase.execute()
        sendProductInfoToSFAUseCase.info()
        sendProductInfoToSFAUseCase.prices()
        sendProductInfoToSFAUseCase.inventory()
        sendOrdersToSFAUseCase.execute()
        sendInvoicesToSFAUseCase.execute()
        sendAccountARsToSFAUseCase.execute()
    }

}