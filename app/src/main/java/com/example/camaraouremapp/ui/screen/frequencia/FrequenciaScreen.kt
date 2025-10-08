package com.example.camaraouremapp.ui.screen.frequencia

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.camaraouremapp.dto.MeResponse
import com.example.camaraouremapp.ui.components.MainScaffold
import com.example.camaraouremapp.ui.theme.VermelhoCamara

@Composable
fun FrequenciaScreen(
    frequenciaViewModel: FrequenciaViewModel = viewModel()
) {
    val frequenciaState by frequenciaViewModel.frequenciaState.collectAsStateWithLifecycle()

    MainScaffold(screenTitle = "Frequência da Sessão") { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            when (val state = frequenciaState) {
                is FrequenciaUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = VermelhoCamara)
                    }
                }
                is FrequenciaUiState.Error -> {
                    Text(text = "Erro: ${state.message}", color = MaterialTheme.colorScheme.error)
                }
                is FrequenciaUiState.Success -> {
                    val presentes = state.frequencia.presentes
                    val ausentes = state.frequencia.ausentes

                    ListaFrequencia(titulo = "Presentes (${presentes.size})", membros = presentes)
                    Spacer(modifier = Modifier.height(24.dp))
                    ListaFrequencia(titulo = "Ausentes (${ausentes.size})", membros = ausentes)
                }
            }
        }
    }
}

@Composable
fun ListaFrequencia(titulo: String, membros: List<MeResponse>) {
    Column {
        Text(text = titulo, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = VermelhoCamara)
        Spacer(modifier = Modifier.height(8.dp))
        if (membros.isEmpty()) {
            Text(text = "Nenhum membro nesta lista.")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(membros) { membro ->
                    MembroItem(membro = membro)
                }
            }
        }
    }
}

@Composable
fun MembroItem(membro: MeResponse) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = membro.nome, fontWeight = FontWeight.Bold)
            Text(text = membro.funcao.replace("_", " ").lowercase()
                .replaceFirstChar { it.titlecase() }, fontSize = 14.sp)
        }
    }
}