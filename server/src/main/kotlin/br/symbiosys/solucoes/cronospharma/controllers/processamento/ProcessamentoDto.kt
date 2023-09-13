package br.symbiosys.solucoes.cronospharma.controllers.processamento

import br.symbiosys.solucoes.cronospharma.entidades.TipoIntegracao
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class ProcessamentoDto(
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    @JsonProperty("data_ultima_execucao")
    val dataExecucao: LocalDateTime = LocalDateTime.now(),
    var arquivos: List<String?>? = mutableListOf(),
    @JsonProperty("pedidos_gerados")
    var pedidosGerados: List<String?>? = mutableListOf(),
    @JsonProperty("prevendas_geradas")
    var preVendasGeradas: List<String?>? = mutableListOf(),
    @JsonProperty("integrador")
    var tipoIntegracao: TipoIntegracao? = null,
)


