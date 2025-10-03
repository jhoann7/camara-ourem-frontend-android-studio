package com.example.camaraouremapp.ui.screen.sessaodetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.camaraouremapp.data.network.RetrofitInstance
import com.example.camaraouremapp.dto.Pauta
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SessaoDetailViewModel(
    savedStateHandle: SavedStateHandle // Objeto para receber argumentos de navegação
) : ViewModel() {

    // Extrai o ID da sessão que foi passado pela navegação
    private val sessaoId: Long = checkNotNull(savedStateHandle["sessaoId"])

    private val _pautasState = MutableStateFlow<PautasUiState>(PautasUiState.Loading)
    val pautasState = _pautasState.asStateFlow()

    init {
        fetchPautas()
    }

    private fun fetchPautas() {
        viewModelScope.launch {
            _pautasState.value = PautasUiState.Loading
            try {
                val response = RetrofitInstance.api.getPautasPorSessao(sessaoId)
                if (response.isSuccessful && response.body() != null) {
                    _pautasState.value = PautasUiState.Success(response.body()!!)
                } else {
                    _pautasState.value = PautasUiState.Error("Falha ao carregar as pautas")
                }
            } catch (e: Exception) {
                _pautasState.value = PautasUiState.Error(e.message ?: "Erro de ligação")
            }
        }
    }
}

sealed class PautasUiState {
    object Loading : PautasUiState()
    data class Success(val pautas: List<Pauta>) : PautasUiState()
    data class Error(val message: String) : PautasUiState()
}