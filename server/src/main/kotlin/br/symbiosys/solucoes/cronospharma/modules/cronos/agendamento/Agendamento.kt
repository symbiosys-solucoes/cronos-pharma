package br.symbiosys.solucoes.cronospharma.modules.cronos.agendamento

class Agendamento {

    var id: Int = 0
    var nomeAgendamento: TipoAgendamentoEnum = TipoAgendamentoEnum.OUTROS
    var ativa = false
    var cron = ""
}