package br.symbiosys.solucoes.cronospharma.cronospharma.sym.gateway.repository

import br.symbiosys.solucoes.cronospharma.cronospharma.sym.model.SymEventos
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SymEventosRepository: JpaRepository<SymEventos, Long>{

}