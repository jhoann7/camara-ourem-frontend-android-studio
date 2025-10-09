package com.example.camaraouremapp.data.network

import android.util.Log
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient

object CronometroService {
    private var stompClient: StompClient? = null
    private var topicSubscription: Disposable? = null

    private val _tempoRestante = MutableStateFlow(0)
    val tempoRestante = _tempoRestante.asStateFlow()

    // ATENÇÃO: Use a URL base do seu backend, mas troque http:// por ws://
    private const val WEBSOCKET_URL = "ws://camara-ourem-backend-env.eba-gmycdjz7.us-east-2.elasticbeanstalk.com/ws"

    fun connect(sessaoId: Long) {
        if (stompClient?.isConnected == true) {
            return // Já está ligado
        }

        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, WEBSOCKET_URL)
        stompClient?.connect()

        topicSubscription = stompClient?.topic("/topic/cronometro/$sessaoId")
            ?.subscribe({ stompMessage ->
                try {
                    // O Spring envia um JSON como {"tempoRestante":599}
                    // O código abaixo extrai o número
                    val tempo = stompMessage.payload
                        .substringAfter(":")
                        .replace("}", "")
                        .trim()
                        .toInt()
                    _tempoRestante.value = tempo
                } catch (e: Exception) {
                    Log.e("CronometroService", "Erro ao processar mensagem", e)
                }
            }, { throwable ->
                Log.e("CronometroService", "Erro na subscrição", throwable)
            })
    }

    fun disconnect() {
        topicSubscription?.dispose()
        stompClient?.disconnect()
        stompClient = null
        _tempoRestante.value = 0 // Reseta o tempo ao desligar
    }
}