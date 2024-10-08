package br.symbiosys.solucoes.cronospharma.modules.petronas.usecases.implementations

import br.symbiosys.solucoes.cronospharma.modules.petronas.ports.repositories.PedidoPalmPetronasRepository
import br.symbiosys.solucoes.cronospharma.modules.petronas.usecases.*
import org.springframework.context.annotation.Bean
import org.springframework.scheduling.TaskScheduler
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.stereotype.Component

@Component
@EnableScheduling
class ScheduledSendToSFAImpl(
    private val sendAccountsToSFAUseCase: SendAccountsToSFAUseCase,
    private val sendAccountARsToSFAUseCase: SendAccountARsToSFAUseCase,
    private val sendInvoicesToSFAUseCase: SendInvoicesToSFAUseCase,
    private val sendOrdersToSFAUseCase: SendOrdersToSFAUseCase,
    private val sendProductInfoToSFAUseCase: SendProductInfoToSFAUseCase,
    private val pedidoPalmPetronasRepository: PedidoPalmPetronasRepository,
) : ScheduledSendToSFA {
    // private val executor: ExecutorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())

    @Scheduled(cron = "\${app.cron.petronas.envia.geral}")
    override fun execute() {
        sendAccountsToSFAUseCase.execute()
        sendProductInfoToSFAUseCase.info()
        sendProductInfoToSFAUseCase.prices()
        sendProductInfoToSFAUseCase.inventory()
    }

    @Scheduled(cron = "\${app.cron.petronas.envia.pedidos}")
    fun sendOrdersAndARs() {
        // executor.submit {
        sendOrdersToSFAUseCase.execute()
        sendInvoicesToSFAUseCase.execute()
        sendAccountARsToSFAUseCase.execute()
        // }
    }

    @Scheduled(cron = "\${app.cron.petronas.envia.notas}")
    fun sendOrdersInvoicesAndARs() {
        // executor.submit {
        sendInvoicesToSFAUseCase.execute()
        // }
    }

    @Scheduled(cron = "\${app.cron.petronas.converte.pedido}")
    fun convertOrderToMovimento() {
        //  executor.submit {
        pedidoPalmPetronasRepository.convertAll()
        // }
    }

    @Bean
    fun taskScheduler(): TaskScheduler {
        val threadPoolTaskScheduler = ThreadPoolTaskScheduler()
        threadPoolTaskScheduler.poolSize = Runtime.getRuntime().availableProcessors()
        threadPoolTaskScheduler.setThreadNamePrefix("ThreadPoolTaskScheduler")
        return threadPoolTaskScheduler
    }
    /**
     @PreDestroy
     fun shutdownExecutor() {
     executor.shutdown()
     try {
     if (!executor.awaitTermination(180, java.util.concurrent.TimeUnit.SECONDS)) {
     executor.shutdownNow()
     }
     } catch (e: InterruptedException) {
     executor.shutdownNow()
     Thread.currentThread().interrupt()
     }
     }
     */
}
