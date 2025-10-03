package com.example.camaraouremapp.ui.screen.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.camaraouremapp.data.network.AuthManager
import com.example.camaraouremapp.data.network.RetrofitInstance
import com.example.camaraouremapp.dto.LoginRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.camaraouremapp.data.network.UserManager

class LoginViewModel : ViewModel() {

    // StateFlow para guardar o estado do login
    private val _loginState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val loginState = _loginState.asStateFlow()

    // Função que a nossa UI irá chamar para tentar fazer o login
    fun doLogin(login: String, senha: String) {
        _loginState.value = LoginUiState.Loading

        // CORREÇÃO 1: "launch" em minúsculas
        viewModelScope.launch {
            try {
                val request = LoginRequest(login = login, senha = senha)
                // 1. Faz o pedido de login
                val loginResponse = RetrofitInstance.api.login(request)

                if (loginResponse.isSuccessful && loginResponse.body() != null) {
                    val token = loginResponse.body()!!.token
                    AuthManager.authToken = token // Guarda o token

                    // 2. AGORA, VAI BUSCAR OS DADOS DO UTILIZADOR
                    val meResponse = RetrofitInstance.api.getMe()
                    if (meResponse.isSuccessful && meResponse.body() != null) {
                        UserManager.currentUser = meResponse.body() // Guarda os dados do utilizador
                        _loginState.value = LoginUiState.Success(token) // Sinaliza o sucesso do login
                    } else {
                        _loginState.value = LoginUiState.Error("Falha ao obter dados do utilizador.")
                    }
                } else {
                    val errorMsg = loginResponse.errorBody()?.string() ?: "Login ou senha inválidos"
                    _loginState.value = LoginUiState.Error(errorMsg)
                }
            } catch (e: Exception) {
                _loginState.value = LoginUiState.Error(e.message ?: "Falha na ligação")
            }
        }
    }
}

// Classe selada para representar os diferentes estados da UI de login
sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    data class Success(val token: String) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}