package br.symbiosys.solucoes.cronospharma.cronospharma.sym.gateway.repository

import br.symbiosys.solucoes.cronospharma.cronospharma.sym.model.SymCustomer
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SymCustomerRepository : JpaRepository<SymCustomer, Long>{
}