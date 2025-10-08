package com.example.camaraouremapp.ui.screen.sessaodetail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.camaraouremapp.data.network.UserManager
import com.example.camaraouremapp.dto.Pauta
import com.example.camaraouremapp.ui.components.MainScaffold
import com.example.camaraouremapp.ui.theme.AzulCamara
import com.example.camaraouremapp.ui.theme.VermelhoCamara
import androidx.compose.runtime.LaunchedEffect // <-- Verifique se este import foi adicionado
@Composable
fun SessaoDetailScreen(
    onPautaClickParaVotar: (Long) -> Unit,
    onPautaClickParaResultado: (Long) -> Unit,
    onVerFrequenciaClick: (Long) -> Unit,
    sessaoDetailViewModel: SessaoDetailViewModel = viewModel()
) {
    val pautasState by sessaoDetailViewModel.pautasState.collectAsStateWithLifecycle()
    val tempoRestante by sessaoDetailViewModel.tempoRestante.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        sessaoDetailViewModel.fetchPautas()
    }

    MainScaffold(screenTitle = "Pautas da Sessão") { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            CronometroDisplay(tempoRestante = tempoRestante)

            Spacer(modifier = Modifier.height(16.dp))

            if (UserManager.currentUser?.funcao == "PRESIDENTE" || UserManager.currentUser?.funcao == "ADMINISTRADOR") {
                ControlesPresidente(
                    onPlay = { sessaoDetailViewModel.iniciarCronometro() },
                    onPause = { sessaoDetailViewModel.pausarCronometro() },
                    onReset = { sessaoDetailViewModel.resetarCronometro() }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            Button(
                onClick = { onVerFrequenciaClick(sessaoDetailViewModel.sessaoId) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver Frequência da Sessão")
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (val state = pautasState) {
                is PautasUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = VermelhoCamara)
                    }
                }
                is PautasUiState.Error -> {
                    Text(text = "Erro: ${state.message}", color = MaterialTheme.colorScheme.error)
                }
                is PautasUiState.Success -> {
                    ListaDePautas(
                        pautas = state.pautas,
                        onPautaClickParaVotar = onPautaClickParaVotar,
                        onPautaClickParaResultado = onPautaClickParaResultado,
                        sessaoDetailViewModel = sessaoDetailViewModel
                    )
                }
            }
        }
    }
}

@Composable
fun CronometroDisplay(tempoRestante: Int) {
    val minutos = tempoRestante / 60
    val segundos = tempoRestante % 60
    val tempoFormatado = String.format("%02d:%02d", minutos, segundos)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AzulCamara)
    ) {
        Text(
            text = tempoFormatado,
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .padding(vertical = 24.dp)
                .align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun ControlesPresidente(onPlay: () -> Unit, onPause: () -> Unit, onReset: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPlay) {
            Icon(Icons.Default.PlayArrow, contentDescription = "Iniciar", modifier = Modifier.size(48.dp))
        }
        IconButton(onClick = onPause) {
            Icon(Icons.Default.Pause, contentDescription = "Pausar", modifier = Modifier.size(48.dp))
        }
        IconButton(onClick = onReset) {
            Icon(Icons.Default.Refresh, contentDescription = "Resetar", modifier = Modifier.size(48.dp))
        }
    }
}

@Composable
fun ListaDePautas(
    pautas: List<Pauta>,
    onPautaClickParaVotar: (Long) -> Unit,
    onPautaClickParaResultado: (Long) -> Unit,
    sessaoDetailViewModel: SessaoDetailViewModel
) {
    if (pautas.isEmpty()) {
        Text(text = "Nenhuma pauta encontrada para esta sessão.")
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(pautas) { pauta ->
                PautaItem(
                    pauta = pauta,
                    onClick = {
                        if (pauta.status == "AGUARDANDO_VOTACAO" || pauta.status == "EM_VOTACAO") {
                            onPautaClickParaVotar(pauta.id)
                        } else {
                            onPautaClickParaResultado(pauta.id)
                        }
                    },
                    onStatusChange = { novoStatus ->
                        sessaoDetailViewModel.mudarStatusPauta(pauta.id, novoStatus)
                    }
                )
            }
        }
    }
}

@Composable
fun PautaItem(
    pauta: Pauta,
    onClick: () -> Unit,
    onStatusChange: (novoStatus: String) -> Unit
) {
    val usuarioLogado = UserManager.currentUser
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .width(8.dp)
                        .height(80.dp)
                        .background(AzulCamara)
                )
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = pauta.descricao,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = VermelhoCamara
                    )
                    Text(
                        text = "Status: ${pauta.status}",
                        fontSize = 14.sp,
                        color = Color.DarkGray
                    )
                }
            }
            if (usuarioLogado?.funcao == "PRESIDENTE" || usuarioLogado?.funcao == "ADMINISTRADOR") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = { onStatusChange("EM_VOTACAO") },
                        enabled = pauta.status == "AGUARDANDO_VOTACAO"
                    ) {
                        Text("Abrir Votação")
                    }
                }
            }
        }
    }
}