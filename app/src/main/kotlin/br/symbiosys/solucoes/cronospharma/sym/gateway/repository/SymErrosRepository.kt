package br.symbiosys.solucoes.cronospharma.sym.gateway.repository

import br.symbiosys.solucoes.cronospharma.sym.model.SymErros
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SymErrosRepository : JpaRepository<SymErros, Long>{


}