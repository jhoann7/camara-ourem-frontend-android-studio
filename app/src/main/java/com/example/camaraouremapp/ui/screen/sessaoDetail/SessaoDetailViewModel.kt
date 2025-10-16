package com.example.camaraouremapp.ui.screen.sessaodetail

import android.util.Log
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

data class SolicitacaoAparte(
    val solicitanteId: Long,
    val solicitanteNome: String
)

class SessaoDetailViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val sessaoId: Long = checkNotNull(savedStateHandle["sessaoId"])

    private val _pautasState = MutableStateFlow<PautasUiState>(PautasUiState.Loading)
    val pautasState = _pautasState.asStateFlow()

    private val _tempoRestante = MutableStateFlow(0)
    val tempoRestante = _tempoRestante.asStateFlow()

    private val _tipoCronometro = MutableStateFlow("PRINCIPAL")
    val tipoCronometro = _tipoCronometro.asStateFlow()

    private val _solicitacaoAparte = MutableStateFlow<SolicitacaoAparte?>(null)
    val solicitacaoAparte = _solicitacaoAparte.asStateFlow()

    init {
        fetchPautas()
        fetchTempoInicial()
        conectarServicosWebSocket()
    }

    private fun conectarServicosWebSocket() {
        CronometroService.connect(
            sessaoId = sessaoId,
            onTipoChange = { novoTipo ->
                _tipoCronometro.value = novoTipo
            },
            onSolicitacao = { solicitacao ->
                _solicitacaoAparte.value = solicitacao
            }
        )

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
                Log.e("SessaoDetailVM", "Erro ao buscar tempo inicial", e)
            }
        }
    }

    fun iniciarCronometro() {
        viewModelScope.launch {
            try {
                Log.d("SessaoDetailVM", "Enviando pedido para iniciar cronómetro...")
                RetrofitInstance.api.iniciarCronometro(sessaoId)
            } catch (e: Exception) {
                Log.e("SessaoDetailVM", "Erro ao iniciar cronómetro", e)
            }
        }
    }

    fun pausarCronometro() {
        viewModelScope.launch {
            try {
                val tempoAtual = _tempoRestante.value
                RetrofitInstance.api.pausarCronometro(sessaoId, mapOf("tempoRestante" to tempoAtual))
            } catch (e: Exception) {
                Log.e("SessaoDetailVM", "Erro ao pausar cronómetro", e)
            }
        }
    }

    fun resetarCronometro() {
        viewModelScope.launch {
            try {
                Log.d("SessaoDetailVM", "Enviando pedido para resetar cronómetro...")
                RetrofitInstance.api.resetarCronometro(sessaoId)
            } catch (e: Exception) {
                Log.e("SessaoDetailVM", "Erro ao resetar cronómetro", e)
            }
        }
    }

    fun iniciarAparte() {
        viewModelScope.launch {
            try {
                _solicitacaoAparte.value = null
                Log.d("SessaoDetailVM", "Enviando pedido para iniciar aparte...")
                RetrofitInstance.api.iniciarAparte(sessaoId)
            } catch (e: Exception) {
                Log.e("SessaoDetailVM", "Erro ao iniciar aparte", e)
            }
        }
    }

    fun pararAparte() {
        viewModelScope.launch {
            try {
                Log.d("SessaoDetailVM", "Enviando pedido para parar aparte...")
                RetrofitInstance.api.pararAparte(sessaoId)
            } catch (e: Exception) {
                Log.e("SessaoDetailVM", "Erro ao parar aparte", e)
            }
        }
    }

    fun solicitarAparte() {
        viewModelScope.launch {
            try {
                Log.d("SessaoDetailVM", "Enviando pedido para solicitar aparte...")
                RetrofitInstance.api.solicitarAparte(sessaoId)
            } catch (e: Exception) {
                Log.e("SessaoDetailVM", "Erro ao solicitar aparte", e)
            }
        }
    }

    // FUNÇÃO MODIFICADA
    fun negarAparte() {
        viewModelScope.launch {
            try {
                // Limpa a notificação localmente
                _solicitacaoAparte.value = null
                // Envia o comando para o servidor retomar o cronómetro principal
                Log.d("SessaoDetailVM", "Enviando pedido para negar aparte e retomar cronómetro...")
                RetrofitInstance.api.negarAparte(sessaoId)
            } catch (e: Exception) {
                Log.e("SessaoDetailVM", "Erro ao negar aparte", e)
            }
        }
    }

    fun fetchPautas() {
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

    fun mudarStatusPauta(pautaId: Long, novoStatus: String) {
        viewModelScope.launch {
            try {
                val requestBody = mapOf("status" to novoStatus)
                val response = RetrofitInstance.api.mudarStatusPauta(pautaId, requestBody)
                if (response.isSuccessful) {
                    fetchPautas()
                } else {
                    Log.e("SessaoDetailVM", "Erro ao mudar status: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("SessaoDetailVM", "Falha de rede ao mudar status", e)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        CronometroService.disconnect()
    }
}

sealed class PautasUiState {
    object Loading : PautasUiState()
    data class Success(val pautas: List<Pauta>) : PautasUiState()
    data class Error(val message: String) : PautasUiState()
}