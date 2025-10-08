package com.example.camaraouremapp.ui.screen.sessaodetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.camaraouremapp.data.network.CronometroService
import com.example.camaraouremapp.data.network.RetrofitInstance
import com.example.camaraouremapp.dto.Pauta
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import android.util.Log

class SessaoDetailViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val sessaoId: Long = checkNotNull(savedStateHandle["sessaoId"])

    private val _pautasState = MutableStateFlow<PautasUiState>(PautasUiState.Loading)
    val pautasState = _pautasState.asStateFlow()

    // Novo StateFlow para o tempo do cronómetro
    private val _tempoRestante = MutableStateFlow(0)
    val tempoRestante = _tempoRestante.asStateFlow()

    init {
        fetchPautas()
        fetchTempoInicial()
        conectarCronometro()
    }

    private fun conectarCronometro() {
        CronometroService.connect(sessaoId)
        CronometroService.tempoRestante
            .onEach { tempo -> _tempoRestante.value = tempo }
            .launchIn(viewModelScope)
    }

    private fun fetchTempoInicial() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getSessaoPorId(sessaoId)
                if (response.isSuccessful && response.body() != null) {
                    _tempoRestante.value = response.body()!!.tempoRestanteOrador
                }
            } catch (e: Exception) {
                // Lidar com o erro se necessário
            }
        }
    }

    // As funções de controlo do cronómetro
    fun iniciarCronometro() {
        viewModelScope.launch {
            RetrofitInstance.api.iniciarCronometro(sessaoId)
        }
    }

    fun pausarCronometro() {
        viewModelScope.launch {
            val tempoAtual = _tempoRestante.value
            RetrofitInstance.api.pausarCronometro(sessaoId, mapOf("tempoRestante" to tempoAtual))
        }
    }

    fun resetarCronometro() {
        viewModelScope.launch {
            RetrofitInstance.api.resetarCronometro(sessaoId)
        }
    }

    fun fetchPautas() {
        // ... (o seu código para buscar pautas continua igual)
    }

    fun mudarStatusPauta(pautaId: Long, novoStatus: String) {
        viewModelScope.launch {
            try {
                val requestBody = mapOf("status" to novoStatus)
                val response = RetrofitInstance.api.mudarStatusPauta(pautaId, requestBody)
                if (response.isSuccessful) {
                    // Se a atualização funcionou, busca a lista de pautas novamente para atualizar a tela
                    fetchPautas()
                } else {
                    Log.e("SessaoDetailVM", "Erro ao mudar status: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("SessaoDetailVM", "Falha de rede ao mudar status", e)
            }
        }
    }

    // Garante que a ligação é terminada quando o ViewModel é destruído
    override fun onCleared() {
        super.onCleared()
        CronometroService.disconnect()
    }
}

// ... (a sua classe selada PautasUiState continua igual)

sealed class PautasUiState {
    object Loading : PautasUiState()
    data class Success(val pautas: List<Pauta>) : PautasUiState()
    data class Error(val message: String) : PautasUiState()
}