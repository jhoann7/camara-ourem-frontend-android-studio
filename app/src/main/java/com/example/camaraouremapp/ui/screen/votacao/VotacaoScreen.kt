package com.example.camaraouremapp.ui.screen.votacao

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.camaraouremapp.ui.components.MainScaffold
import com.example.camaraouremapp.ui.theme.CinzaAbster
import com.example.camaraouremapp.ui.theme.VerdeConfirmar
import com.example.camaraouremapp.ui.theme.VermelhoCamara

@Composable
fun VotacaoScreen(
    onNavigateBack: () -> Unit,
    votacaoViewModel: VotacaoViewModel = viewModel()
) {
    val pautaState by votacaoViewModel.pautaState.collectAsStateWithLifecycle()
    val votacaoState by votacaoViewModel.votacaoState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    MainScaffold(screenTitle = "Votação") { paddingValues ->
        // Mostra um indicador de progresso a cobrir o ecrã se estiver a registar um voto
        if (votacaoState is VotacaoUiState.Loading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = VermelhoCamara)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Usa um 'when' para mostrar o estado do carregamento da pauta
            when (val state = pautaState) {
                is PautaUiState.Loading -> {
                    CircularProgressIndicator(color = VermelhoCamara)
                }
                is PautaUiState.Error -> {
                    Text(text = "Erro: ${state.message}", color = MaterialTheme.colorScheme.error)
                }
                is PautaUiState.Success -> {
                    // Mostra o cartão com a descrição real da pauta
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Text(
                            text = state.pauta.descricao,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(24.dp)
                        )
                    }
                }
            }

            // Espaçador com peso para empurrar os botões para baixo
            Spacer(modifier = Modifier.weight(1f))

            // Só mostra os botões se o voto não tiver sido registado com sucesso
            if (votacaoState !is VotacaoUiState.Success) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    VotoButton(text = "SIM", backgroundColor = VerdeConfirmar, onClick = { votacaoViewModel.registarVoto("SIM") })
                    VotoButton(text = "NÃO", backgroundColor = VermelhoCamara, onClick = { votacaoViewModel.registarVoto("NÃO") })
                    VotoButton(text = "ABSTENÇÃO", backgroundColor = CinzaAbster, onClick = { votacaoViewModel.registarVoto("ABSTENCAO") })
                }
            }
        }
    }

    // Bloco que reage às mudanças de estado da votação
    LaunchedEffect(votacaoState) {
        when (val state = votacaoState) {
            is VotacaoUiState.Success -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                onNavigateBack() // Volta para a tela de detalhes da sessão
            }
            is VotacaoUiState.Error -> {
                Toast.makeText(context, "Erro: ${state.message}", Toast.LENGTH_LONG).show()
            }
            else -> Unit
        }
    }
}

@Composable
fun VotoButton(
    text: String,
    backgroundColor: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor)
    ) {
        Text(text = text, fontSize = 24.sp, fontWeight = FontWeight.Bold)
    }
}