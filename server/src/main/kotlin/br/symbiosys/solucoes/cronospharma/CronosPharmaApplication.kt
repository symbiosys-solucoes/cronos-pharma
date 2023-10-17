package br.symbiosys.solucoes.cronospharma

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.scheduling.annotation.EnableAsync

@SpringBootApplication
@EnableFeignClients
@EnableAsync
class CronosPharmaApplication

fun main(args: Array<String>) {
	val runApplication = runApplication<br.symbiosys.solucoes.cronospharma.CronosPharmaApplication>(*args)
}
