package com.example.camaraouremapp.dto

// Objeto que enviaremos para registar o voto
data class VotoRequest(
    val tipoVoto: String
)

// A resposta completa do voto (já que o backend devolve o voto criado)
data class VotoResponse(
    val id: Long,
    val tipoVoto: String,
    val dataHora: String,
    val pauta: Pauta,
    val usuario: MeResponse // Podemos reutilizar o MeResponse aqui
)