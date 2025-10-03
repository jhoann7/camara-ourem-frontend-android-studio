package com.example.camaraouremapp.ui.screen.votacao

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.camaraouremapp.data.network.RetrofitInstance
import com.example.camaraouremapp.data.network.UserManager
import com.example.camaraouremapp.dto.Pauta
import com.example.camaraouremapp.dto.VotoRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VotacaoViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val pautaId: Long = checkNotNull(savedStateHandle["pautaId"])

    private val _pautaState = MutableStateFlow<PautaUiState>(PautaUiState.Loading)
    val pautaState = _pautaState.asStateFlow()

    private val _votacaoState = MutableStateFlow<VotacaoUiState>(VotacaoUiState.Idle)
    val votacaoState = _votacaoState.asStateFlow()

    init {
        fetchPautaDetails()
    }

    private fun fetchPautaDetails() {
        viewModelScope.launch {
            _pautaState.value = PautaUiState.Loading
            try {
                val response = RetrofitInstance.api.getPautaById(pautaId)
                if (response.isSuccessful && response.body() != null) {
                    _pautaState.value = PautaUiState.Success(response.body()!!)
                } else {
                    _pautaState.value = PautaUiState.Error("Falha ao carregar detalhes da pauta.")
                }
            } catch (e: Exception) {
                _pautaState.value = PautaUiState.Error(e.message ?: "Erro de ligação")
            }
        }
    }

    fun registarVoto(tipoVoto: String) {
        _votacaoState.value = VotacaoUiState.Loading
        val usuarioId = UserManager.currentUser?.id
        if (usuarioId == null) {
            _votacaoState.value = VotacaoUiState.Error("Não foi possível identificar o utilizador.")
            return
        }

        viewModelScope.launch {
            try {
                val request = VotoRequest(tipoVoto = tipoVoto)
                val response = RetrofitInstance.api.registarVoto(pautaId, usuarioId, request)

                if (response.isSuccessful) {
                    _votacaoState.value = VotacaoUiState.Success("Voto registado com sucesso!")
                } else {
                    val errorMsg = if (response.code() == 409) "Você já votou nesta pauta."
                    else response.errorBody()?.string() ?: "Erro ao registar o voto."
                    _votacaoState.value = VotacaoUiState.Error(errorMsg)
                }
            } catch (e: Exception) {
                _votacaoState.value = VotacaoUiState.Error(e.message ?: "Falha na ligação")
            }
        }
    }
}

// Estados para o carregamento da Pauta
sealed class PautaUiState {
    object Loading : PautaUiState()
    data class Success(val pauta: Pauta) : PautaUiState()
    data class Error(val message: String) : PautaUiState()
}

// Estados para a ação de Votar
sealed class VotacaoUiState {
    object Idle : VotacaoUiState()
    object Loading : VotacaoUiState()
    data class Success(val message: String) : VotacaoUiState()
    data class Error(val message: String) : VotacaoUiState()
}