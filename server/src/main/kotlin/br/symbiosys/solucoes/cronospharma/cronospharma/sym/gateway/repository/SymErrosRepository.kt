package br.symbiosys.solucoes.cronospharma.cronospharma.sym.gateway.repository

import br.symbiosys.solucoes.cronospharma.cronospharma.sym.model.SymCustomer
import br.symbiosys.solucoes.cronospharma.cronospharma.sym.model.SymErros
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface SymErrosRepository : JpaRepository<SymErros, Long>{


}