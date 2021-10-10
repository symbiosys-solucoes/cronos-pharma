package br.symbiosys.solucoes.cronospharma.cronospharma.controllers.processamento

import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.TipoIntegracao
import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

data class ProcessamentoDto(
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    val dataExecucao: LocalDateTime = LocalDateTime.now(),
    var arquivos: List<String?>? = mutableListOf(),
    var pedidosGerados: List<String?>? = mutableListOf(),
    var preVendasGeradas: List<String?>? = mutableListOf(),
    var tipoIntegracao: TipoIntegracao? = null,
)


