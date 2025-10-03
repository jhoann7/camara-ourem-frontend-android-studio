package com.example.camaraouremapp.data.network

import com.example.camaraouremapp.dto.MeResponse

// Objeto singleton para guardar os dados do utilizador logado
object UserManager {
    var currentUser: MeResponse? = null
}