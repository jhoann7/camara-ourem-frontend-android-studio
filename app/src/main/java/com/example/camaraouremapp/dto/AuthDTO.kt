package com.example.camaraouremapp.dto

// Classe para receber os dados do pedido de login
data class LoginRequest(
    val login: String,
    val senha: String
)

// Classe para enviar a resposta do login
data class LoginResponse(
    val token: String
)

// ADICIONE ESTA CLASSE
// Classe para devolver os dados do utilizador autenticado
data class MeResponse(
    val id: Long,
    val nome: String,
    val login: String,
    val funcao: String
)

data class FrequenciaResponse(
    val presentes: List<MeResponse>,
    val ausentes: List<MeResponse>
)