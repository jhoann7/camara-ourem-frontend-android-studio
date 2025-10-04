package com.example.camaraouremapp.ui.screen.resultado

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.camaraouremapp.ui.components.MainScaffold
import com.example.camaraouremapp.ui.theme.VermelhoCamara

@Composable
fun ResultadoScreen(resultadoViewModel: ResultadoViewModel = viewModel()) {
    val resultadoState by resultadoViewModel.resultadoState.collectAsStateWithLifecycle()

    MainScaffold(screenTitle = "Resultado da Votação") { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            when (val state = resultadoState) {
                is ResultadoUiState.Loading -> CircularProgressIndicator(color = VermelhoCamara)
                is ResultadoUiState.Error -> Text(text = "Erro: ${state.message}", color = MaterialTheme.colorScheme.error)
                is ResultadoUiState.Success -> {
                    val resultado = state.resultado
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = resultado.pauta.descricao, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(32.dp))
                        Text(text = "Resultado Final: ${resultado.resultadoFinal}", fontSize = 20.sp, color = VermelhoCamara)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "Votos SIM: ${resultado.totalSim}")
                        Text(text = "Votos NÃO: ${resultado.totalNao}")
                        Text(text = "Abstenções: ${resultado.totalAbstencoes}")
                    }
                }
            }
        }
    }
}