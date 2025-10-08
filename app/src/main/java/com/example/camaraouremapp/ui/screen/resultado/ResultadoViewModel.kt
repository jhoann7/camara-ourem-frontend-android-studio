package com.example.camaraouremapp.ui.screen.resultado

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.camaraouremapp.data.network.RetrofitInstance
import com.example.camaraouremapp.dto.ResultadoVotacao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ResultadoViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
    private val pautaId: Long = checkNotNull(savedStateHandle["pautaId"])

    private val _resultadoState = MutableStateFlow<ResultadoUiState>(ResultadoUiState.Loading)
    val resultadoState = _resultadoState.asStateFlow()

    init {
        fetchResultado()
    }

    private fun fetchResultado() {
        viewModelScope.launch {
            _resultadoState.value = ResultadoUiState.Loading
            try {
                val response = RetrofitInstance.api.getResultado(pautaId)
                if (response.isSuccessful && response.body() != null) {
                    _resultadoState.value = ResultadoUiState.Success(response.body()!!)
                } else {
                    _resultadoState.value = ResultadoUiState.Error("Falha ao carregar resultado")
                }
            } catch (e: Exception) {
                _resultadoState.value = ResultadoUiState.Error(e.message ?: "Erro de ligação")
            }
        }
    }
}

sealed class ResultadoUiState {
    object Loading : ResultadoUiState()
    data class Success(val resultado: ResultadoVotacao) : ResultadoUiState()
    data class Error(val message: String) : ResultadoUiState()
}