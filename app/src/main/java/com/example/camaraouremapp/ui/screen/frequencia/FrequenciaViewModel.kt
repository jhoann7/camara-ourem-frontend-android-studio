package com.example.camaraouremapp.ui.screen.frequencia

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.camaraouremapp.data.network.RetrofitInstance
import com.example.camaraouremapp.dto.FrequenciaResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FrequenciaViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
    private val sessaoId: Long = checkNotNull(savedStateHandle["sessaoId"])

    private val _frequenciaState = MutableStateFlow<FrequenciaUiState>(FrequenciaUiState.Loading)
    val frequenciaState = _frequenciaState.asStateFlow()

    init {
        fetchFrequencia()
    }

    fun fetchFrequencia() {
        viewModelScope.launch {
            _frequenciaState.value = FrequenciaUiState.Loading
            try {
                val response = RetrofitInstance.api.getFrequencia(sessaoId)
                if (response.isSuccessful && response.body() != null) {
                    _frequenciaState.value = FrequenciaUiState.Success(response.body()!!)
                } else {
                    _frequenciaState.value = FrequenciaUiState.Error("Falha ao carregar a frequência")
                }
            } catch (e: Exception) {
                _frequenciaState.value = FrequenciaUiState.Error(e.message ?: "Erro de ligação")
            }
        }
    }
}

sealed class FrequenciaUiState {
    object Loading : FrequenciaUiState()
    data class Success(val frequencia: FrequenciaResponse) : FrequenciaUiState()
    data class Error(val message: String) : FrequenciaUiState()
}