package com.example.camaraouremapp.ui.screen.sessaodetail

import androidx.compose.animation.AnimatedVisibility
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
import com.example.camaraouremapp.ui.theme.CinzaAbster
import com.example.camaraouremapp.ui.theme.VerdeConfirmar
import com.example.camaraouremapp.ui.theme.VermelhoCamara

@Composable
fun SessaoDetailScreen(
    onPautaClickParaVotar: (Long) -> Unit,
    onPautaClickParaResultado: (Long) -> Unit,
    onVerFrequenciaClick: (Long) -> Unit,
    sessaoDetailViewModel: SessaoDetailViewModel = viewModel()
) {
    val pautasState by sessaoDetailViewModel.pautasState.collectAsStateWithLifecycle()
    val tempoRestante by sessaoDetailViewModel.tempoRestante.collectAsStateWithLifecycle()
    val tipoCronometro by sessaoDetailViewModel.tipoCronometro.collectAsStateWithLifecycle()
    val solicitacao by sessaoDetailViewModel.solicitacaoAparte.collectAsStateWithLifecycle()
    val usuarioLogado = UserManager.currentUser

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
            CronometroDisplay(
                tempoRestante = tempoRestante,
                tipoCronometro = tipoCronometro
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (usuarioLogado?.funcao == "PRESIDENTE" || usuarioLogado?.funcao == "ADMINISTRADOR") {
                // Notificação para o Presidente (só aparece quando há uma solicitação)
                AnimatedVisibility(visible = solicitacao != null) {
                    SolicitacaoAparteCard(
                        solicitanteNome = solicitacao?.solicitanteNome ?: "",
                        onConceder = { sessaoDetailViewModel.iniciarAparte() },
                        onNegar = { sessaoDetailViewModel.negarAparte() }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Controles do Presidente (agora se adaptam ao contexto)
                ControlesPresidente(
                    onPlay = { sessaoDetailViewModel.iniciarCronometro() },
                    onPause = { sessaoDetailViewModel.pausarCronometro() },
                    onReset = { sessaoDetailViewModel.resetarCronometro() },
                    onPararAparte = { sessaoDetailViewModel.pararAparte() },
                    tipoCronometro = tipoCronometro
                )
            } else {
                // Botão para os Vereadores
                Button(
                    onClick = { sessaoDetailViewModel.solicitarAparte() },
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("SOLICITAR APARTE", fontSize = 18.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

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

// Card de Notificação para o Presidente (continua igual)
@Composable
fun SolicitacaoAparteCard(
    solicitanteNome: String,
    onConceder: () -> Unit,
    onNegar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Solicitação de Aparte",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text("O vereador $solicitanteNome solicitou um aparte.")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = onConceder, colors = ButtonDefaults.buttonColors(containerColor = VerdeConfirmar)) {
                    Text("Conceder")
                }
                Button(onClick = onNegar, colors = ButtonDefaults.buttonColors(containerColor = VermelhoCamara)) {
                    Text("Negar")
                }
            }
        }
    }
}

// Display do cronômetro (continua igual)
@Composable
fun CronometroDisplay(tempoRestante: Int, tipoCronometro: String) {
    val minutos = tempoRestante / 60
    val segundos = tempoRestante % 60
    val tempoFormatado = String.format("%02d:%02d", minutos, segundos)
    val corFundo = if (tipoCronometro == "APARTE") CinzaAbster else AzulCamara

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = corFundo)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 24.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (tipoCronometro == "APARTE") {
                Text(
                    text = "APARTE",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Text(
                text = tempoFormatado,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}


// ALTERAÇÃO PRINCIPAL AQUI
@Composable
fun ControlesPresidente(
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onReset: () -> Unit,
    onPararAparte: () -> Unit,
    tipoCronometro: String
) {
    // A visibilidade dos controles agora depende do tipo de cronômetro
    AnimatedVisibility(visible = tipoCronometro == "PRINCIPAL") {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Tempo do Orador", fontWeight = FontWeight.Bold)
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
    }

    AnimatedVisibility(visible = tipoCronometro == "APARTE") {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Controle de Aparte", fontWeight = FontWeight.Bold)
            Button(onClick = onPararAparte) {
                Text("Retomar Tempo do Orador")
            }
        }
    }
}


// O resto do arquivo (ListaDePautas, PautaItem) continua igual...
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