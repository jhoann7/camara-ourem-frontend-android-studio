package com.example.camaraouremapp.dto

// Representa uma Pauta que vem do nosso backend
data class Pauta(
    val id: Long,
    val descricao: String,
    val status: String,
    val sessao: Sessao // Inclui a sessão a que pertence
)

data class ResultadoVotacao(
    val pauta: Pauta,
    val totalSim: Int,
    val totalNao: Int,
    val totalAbstencoes: Int,
    val resultadoFinal: String
)