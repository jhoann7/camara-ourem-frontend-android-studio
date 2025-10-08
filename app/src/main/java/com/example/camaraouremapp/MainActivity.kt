package com.example.camaraouremapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.camaraouremapp.ui.screen.frequencia.FrequenciaScreen // <-- IMPORTE O NOVO ECRÃ
import com.example.camaraouremapp.ui.screen.home.HomeScreen
import com.example.camaraouremapp.ui.screen.login.LoginScreen
import com.example.camaraouremapp.ui.screen.resultado.ResultadoScreen
import com.example.camaraouremapp.ui.screen.sessaodetail.SessaoDetailScreen
import com.example.camaraouremapp.ui.screen.votacao.VotacaoScreen
import com.example.camaraouremapp.ui.theme.CamaraOuremAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CamaraOuremAppTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(onLoginSuccess = {
                navController.navigate("home") {
                    popUpTo(navController.graph.startDestinationId) {
                        inclusive = true
                    }
                }
            })
        }
        composable("home") {
            HomeScreen(onSessaoClick = { sessaoId ->
                navController.navigate("sessaodetail/$sessaoId")
            })
        }
        composable(
            route = "sessaodetail/{sessaoId}",
            arguments = listOf(navArgument("sessaoId") { type = NavType.LongType })
        ) { backStackEntry -> // Precisamos do backStackEntry para obter o ID
            val sessaoId = backStackEntry.arguments?.getLong("sessaoId") ?: 0
            SessaoDetailScreen(
                onPautaClickParaVotar = { pautaId ->
                    navController.navigate("votacao/$pautaId")
                },
                onPautaClickParaResultado = { pautaId ->
                    navController.navigate("resultado/$pautaId")
                },
                // AGORA A CHAMADA ESTÁ CORRETA
                onVerFrequenciaClick = {
                    navController.navigate("frequencia/$sessaoId")
                }
            )
        }
        composable(
            route = "votacao/{pautaId}",
            arguments = listOf(navArgument("pautaId") { type = NavType.LongType })
        ) {
            VotacaoScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(
            route = "resultado/{pautaId}",
            arguments = listOf(navArgument("pautaId") { type = NavType.LongType })
        ) {
            ResultadoScreen()
        }
        // ESTE É O NOVO BLOCO CORRIGIDO
        composable(
            route = "frequencia/{sessaoId}",
            arguments = listOf(navArgument("sessaoId") { type = NavType.LongType }) // Corrigido: LongType
        ) {
            FrequenciaScreen()
        }
    }
}