package com.example.camaraouremapp.dto

data class Sessao(
    val id: Long,
    val data: String, // Recebemos como String e podemos formatar depois
    val tipo: String,
    val status: String
)