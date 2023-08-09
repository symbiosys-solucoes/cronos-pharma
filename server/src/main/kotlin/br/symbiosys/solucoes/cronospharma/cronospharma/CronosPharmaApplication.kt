package br.symbiosys.solucoes.cronospharma.cronospharma

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableFeignClients
class CronosPharmaApplication

fun main(args: Array<String>) {
	val runApplication = runApplication<CronosPharmaApplication>(*args)
}
