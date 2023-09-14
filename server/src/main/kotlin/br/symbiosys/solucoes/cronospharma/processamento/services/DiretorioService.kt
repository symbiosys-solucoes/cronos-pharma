package br.symbiosys.solucoes.cronospharma.processamento.services

import br.symbiosys.solucoes.cronospharma.entidades.diretorios.Diretorio
import br.symbiosys.solucoes.cronospharma.entidades.diretorios.DiretoriosRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service


@Service
class DiretorioService
    (val repository: DiretoriosRepository)
{
        val logger: Logger = LoggerFactory.getLogger(DiretorioService::class.java)

        fun findAll(): List<Diretorio> {
            try {
                logger.info("Iniciando a carregar OLs")
                return repository.findAll()
            } catch (e: Exception) {
                logger.error("Erro ao carregar OLs", e)
            }
            return emptyList()
        }

    fun findOne(id: Long): Diretorio? {
        try {
            logger.info("Iniciando a carregar OLs")
            return repository.findById(id)
        } catch (e: Exception) {
            logger.error("Erro ao carregar OL de id: $id", e)
        }
        return null
    }
}