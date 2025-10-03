package com.example.camaraouremapp.ui.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.camaraouremapp.dto.Sessao
import com.example.camaraouremapp.ui.components.MainScaffold
import com.example.camaraouremapp.ui.theme.AzulCamara
import com.example.camaraouremapp.ui.theme.VermelhoCamara
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun HomeScreen(
    onSessaoClick: (Long) -> Unit,
    homeViewModel: HomeViewModel = viewModel()
) {
    val sessoesState by homeViewModel.sessoesState.collectAsStateWithLifecycle()

    // Usa o nosso molde de ecrã
    MainScaffold(screenTitle = "Sessões Disponíveis") { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Usa o padding da Scaffold
                .padding(16.dp)
        ) {
            when (val state = sessoesState) {
                is SessoesUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = VermelhoCamara)
                    }
                }
                is SessoesUiState.Error -> {
                    Text(text = "Erro: ${state.message}", color = MaterialTheme.colorScheme.error)
                }
                is SessoesUiState.Success -> {
                    ListaDeSessoes(sessoes = state.sessoes, onSessaoClick = onSessaoClick)
                }
            }
        }
    }
}

@Composable
fun ListaDeSessoes(sessoes: List<Sessao>, onSessaoClick: (Long) -> Unit) {
    if (sessoes.isEmpty()) {
        Text(text = "Nenhuma sessão agendada.")
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(sessoes) { sessao ->
                SessaoItem(sessao = sessao, onClick = { onSessaoClick(sessao.id) })
            }
        }
    }
}

@Composable
fun SessaoItem(sessao: Sessao, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Barra lateral azul para dar um toque de cor
            Box(
                modifier = Modifier
                    .width(8.dp)
                    .height(80.dp)
                    .background(AzulCamara)
            )
            Column(modifier = Modifier.padding(16.dp)) {
                val dataFormatada = try {
                    val parsedDate = LocalDateTime.parse(sessao.data)
                    parsedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm"))
                } catch (e: Exception) {
                    sessao.data
                }
                Text(
                    text = "Sessão ${sessao.tipo.replaceFirstChar { it.titlecase() }}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = VermelhoCamara
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Data: $dataFormatada", color = Color.DarkGray)
                Text(text = "Status: ${sessao.status}", color = Color.DarkGray)
            }
        }
    }
}