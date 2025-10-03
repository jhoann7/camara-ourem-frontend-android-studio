package com.example.camaraouremapp.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.camaraouremapp.data.network.RetrofitInstance
import com.example.camaraouremapp.dto.Sessao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val _sessoesState = MutableStateFlow<SessoesUiState>(SessoesUiState.Loading)
    val sessoesState = _sessoesState.asStateFlow()

    // O bloco init é executado assim que o ViewModel é criado
    init {
        fetchSessoes()
    }

    private fun fetchSessoes() {
        viewModelScope.launch {
            _sessoesState.value = SessoesUiState.Loading
            try {
                val response = RetrofitInstance.api.getSessoes()
                if (response.isSuccessful && response.body() != null) {
                    _sessoesState.value = SessoesUiState.Success(response.body()!!)
                } else {
                    _sessoesState.value = SessoesUiState.Error("Falha ao carregar as sessões")
                }
            } catch (e: Exception) {
                _sessoesState.value = SessoesUiState.Error(e.message ?: "Erro de ligação")
            }
        }
    }
}

sealed class SessoesUiState {
    object Loading : SessoesUiState()
    data class Success(val sessoes: List<Sessao>) : SessoesUiState()
    data class Error(val message: String) : SessoesUiState()
}