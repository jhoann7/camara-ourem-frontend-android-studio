package com.example.camaraouremapp.data.network

import android.util.Log
import com.example.camaraouremapp.ui.screen.sessaodetail.SolicitacaoAparte
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient

object CronometroService {
    private var stompClient: StompClient? = null
    private val compositeDisposable = CompositeDisposable()

    private val _tempoRestante = MutableStateFlow(0)
    val tempoRestante = _tempoRestante.asStateFlow()

    private const val WEBSOCKET_URL = "ws://camara-ourem-backend-env.eba-gmycdjz7.us-east-2.elasticbeanstalk.com/ws"

    // Função ÚNICA de conexão
    fun connect(
        sessaoId: Long,
        onTipoChange: (String) -> Unit,
        onSolicitacao: (SolicitacaoAparte) -> Unit
    ) {
        if (stompClient?.isConnected == true) {
            return // Já está ligado
        }

        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, WEBSOCKET_URL)
        stompClient?.connect()

        // 1. Subscrição ao tópico do CRONÔMETRO
        val cronometroSubscription = stompClient?.topic("/topic/cronometro/$sessaoId")
            ?.subscribe({ stompMessage ->
                try {
                    val json = JSONObject(stompMessage.payload)
                    val tipo = json.getString("tipo")
                    val tempo = json.getInt("tempo")

                    _tempoRestante.value = tempo
                    onTipoChange(tipo)

                } catch (e: Exception) {
                    Log.e("CronometroService", "Erro ao processar mensagem do cronómetro", e)
                }
            }, { throwable ->
                Log.e("CronometroService", "Erro na subscrição do cronómetro", throwable)
            })
        compositeDisposable.add(cronometroSubscription!!)

        // 2. Subscrição ao tópico de SOLICITAÇÕES
        val solicitacaoSubscription = stompClient?.topic("/topic/sessoes/$sessaoId/solicitacoes")
            ?.subscribe({ stompMessage ->
                try {
                    val json = JSONObject(stompMessage.payload)
                    val solicitacao = SolicitacaoAparte(
                        solicitanteId = json.getLong("solicitanteId"),
                        solicitanteNome = json.getString("solicitanteNome")
                    )
                    onSolicitacao(solicitacao) // Envia o objeto da solicitação para o ViewModel
                } catch (e: Exception) {
                    Log.e("CronometroService", "Erro ao processar solicitação de aparte", e)
                }
            }, { throwable ->
                Log.e("CronometroService", "Erro na subscrição de solicitações", throwable)
            })
        compositeDisposable.add(solicitacaoSubscription!!)
    }

    fun disconnect() {
        compositeDisposable.clear() // Limpa todas as subscrições
        stompClient?.disconnect()
        stompClient = null
        _tempoRestante.value = 0
    }
}