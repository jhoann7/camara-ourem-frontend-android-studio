package com.example.camaraouremapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.camaraouremapp.ui.screen.home.HomeScreen
import com.example.camaraouremapp.ui.screen.login.LoginScreen
import com.example.camaraouremapp.ui.theme.CamaraOuremAppTheme
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.camaraouremapp.ui.screen.sessaodetail.SessaoDetailScreen
import com.example.camaraouremapp.ui.screen.votacao.VotacaoScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CamaraOuremAppTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    // Inicia o nosso sistema de navegação
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
                    // CÓDIGO CORRIGIDO AQUI
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
        ) {
            SessaoDetailScreen(onPautaClick = { pautaId ->
                navController.navigate("votacao/$pautaId")
            })
        }
        composable(
            route = "votacao/{pautaId}",
            arguments = listOf(navArgument("pautaId") { type = NavType.LongType })
        ) {
            VotacaoScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}