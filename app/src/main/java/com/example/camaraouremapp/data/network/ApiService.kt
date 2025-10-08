package com.example.camaraouremapp.data.network

import com.example.camaraouremapp.dto.LoginRequest // Usaremos o DTO que já conhecemos
import com.example.camaraouremapp.dto.LoginResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import okhttp3.OkHttpClient
import com.example.camaraouremapp.dto.Sessao // Importe a nossa nova classe
import retrofit2.http.GET
import com.example.camaraouremapp.dto.Pauta // Importe a nossa nova classe
import retrofit2.http.Path
import retrofit2.http.PATCH
import com.example.camaraouremapp.dto.MeResponse
import com.example.camaraouremapp.dto.VotoRequest
import com.example.camaraouremapp.dto.VotoResponse
import com.example.camaraouremapp.dto.ResultadoVotacao
import com.example.camaraouremapp.dto.FrequenciaResponse

// URL base do nosso backend. ATENÇÃO: 10.0.2.2 é o endereço especial
// que o emulador Android usa para se referir ao 'localhost' do seu computador.
private const val BASE_URL = "http://camara-ourem-backend-env.eba-gmycdjz7.us-east-2.elasticbeanstalk.com/"

// Objeto que constrói e fornece a nossa instância do Retrofit
object RetrofitInstance {
    // Cria um cliente HTTP que inclui o nosso intercetor de autenticação
    private val client = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor())
        .build()

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client) // Usa o nosso cliente personalizado
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}

// Interface que define os nossos endpoints

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("auth/me")
    suspend fun getMe(): Response<MeResponse>

    @GET("sessoes")
    suspend fun getSessoes(): Response<List<Sessao>>

    @GET("sessoes/{id}")
    suspend fun getSessaoPorId(@Path("id") id: Long): Response<Sessao>

    @GET("pautas/sessao/{sessaoId}")
    suspend fun getPautasPorSessao(@Path("sessaoId") sessaoId: Long): Response<List<Pauta>>

    @GET("pautas/{pautaId}")
    suspend fun getPautaById(@Path("pautaId") pautaId: Long): Response<Pauta>

    @POST("votos/pauta/{pautaId}/usuario/{usuarioId}")
    suspend fun registarVoto(@Path("pautaId") pautaId: Long, @Path("usuarioId") usuarioId: Long, @Body request: VotoRequest): Response<VotoResponse>

    @GET("votos/pauta/{pautaId}/resultado")
    suspend fun getResultado(@Path("pautaId") pautaId: Long): Response<ResultadoVotacao>

    @GET("votos/sessao/{sessaoId}/frequencia")
    suspend fun getFrequencia(@Path("sessaoId") sessaoId: Long): Response<FrequenciaResponse>

    @PATCH("pautas/{pautaId}/status")
    suspend fun mudarStatusPauta(@Path("pautaId") pautaId: Long, @Body statusUpdate: Map<String, String>): Response<Pauta>

    @POST("sessoes/{id}/iniciar-cronometro")
    suspend fun iniciarCronometro(@Path("id") id: Long): Response<Sessao>

    @POST("sessoes/{id}/pausar-cronometro")
    suspend fun pausarCronometro(@Path("id") id: Long, @Body body: Map<String, Int>): Response<Sessao>

    @POST("sessoes/{id}/resetar-cronometro")
    suspend fun resetarCronometro(@Path("id") id: Long): Response<Sessao>
}


