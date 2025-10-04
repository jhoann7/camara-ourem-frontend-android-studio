package com.example.camaraouremapp.ui.screen.sessaodetail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.camaraouremapp.dto.Pauta
import com.example.camaraouremapp.ui.components.MainScaffold
import com.example.camaraouremapp.ui.theme.AzulCamara
import com.example.camaraouremapp.ui.theme.VermelhoCamara

@Composable
fun SessaoDetailScreen(
    onPautaClickParaVotar: (Long) -> Unit,
    onPautaClickParaResultado: (Long) -> Unit,
    sessaoDetailViewModel: SessaoDetailViewModel = viewModel()
) {
    val pautasState by sessaoDetailViewModel.pautasState.collectAsStateWithLifecycle()

    MainScaffold(screenTitle = "Pautas da Sessão") { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
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
                    // 2. Passamos as duas funções de clique para a nossa lista
                    ListaDePautas(
                        pautas = state.pautas,
                        onPautaClickParaVotar = onPautaClickParaVotar,
                        onPautaClickParaResultado = onPautaClickParaResultado
                    )
                }
            }
        }
    }
}

// 3. A assinatura da lista também foi atualizada
@Composable
fun ListaDePautas(
    pautas: List<Pauta>,
    onPautaClickParaVotar: (Long) -> Unit,
    onPautaClickParaResultado: (Long) -> Unit
) {
    if (pautas.isEmpty()) {
        Text(text = "Nenhuma pauta encontrada para esta sessão.")
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(pautas) { pauta ->
                PautaItem(
                    pauta = pauta,
                    onClick = {
                        // 4. A lógica de decisão que você tinha na imagem
                        if (pauta.status == "AGUARDANDO_VOTACAO" || pauta.status == "EM_VOTACAO") {
                            onPautaClickParaVotar(pauta.id)
                        } else {
                            onPautaClickParaResultado(pauta.id)
                        }
                    }
                )
            }
        }
    }
}


@Composable
fun PautaItem(pauta: Pauta, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
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
    }
}