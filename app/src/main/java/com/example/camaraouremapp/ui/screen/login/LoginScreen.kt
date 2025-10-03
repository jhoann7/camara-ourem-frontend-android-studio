package com.example.camaraouremapp.ui.screen.login

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.camaraouremapp.R
import com.example.camaraouremapp.ui.theme.VermelhoCamara
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit, // 1. Recebe a função de navegação
    loginViewModel: LoginViewModel = viewModel()
) {
    // Variáveis para guardar o que o utilizador digita
    var login by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }

    // "Ouve" o estado do login a partir do ViewModel
    val loginState by loginViewModel.loginState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Estrutura a tela numa coluna vertical
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_camara),
            contentDescription = "Logo da Câmara de Ourém",
            modifier = Modifier.size(150.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text("Sistema de Votação Eletrónica", fontSize = 20.sp)

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = login,
            onValueChange = { login = it },
            label = { Text("Login") },
            modifier = Modifier.fillMaxWidth(),
            isError = loginState is LoginUiState.Error // Mostra erro se o estado for de erro
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = senha,
            onValueChange = { senha = it },
            label = { Text("Senha") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            isError = loginState is LoginUiState.Error // Mostra erro se o estado for de erro
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Se estiver a carregar, mostra um indicador de progresso. Senão, mostra o botão.
        when (loginState) {
            is LoginUiState.Loading -> {
                CircularProgressIndicator(color = VermelhoCamara)
            }
            else -> {
                Button(
                    onClick = { loginViewModel.doLogin(login, senha) }, // Chama a função do ViewModel
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VermelhoCamara)
                ) {
                    Text("ENTRAR", color = Color.White)
                }
            }
        }
    }

    // Observa o estado para mostrar mensagens ou navegar
    LaunchedEffect(loginState) {
        when (val state = loginState) {
            is LoginUiState.Success -> {
                Toast.makeText(context, "Login bem-sucedido!", Toast.LENGTH_SHORT).show()
                onLoginSuccess() // NAVEGA PARA A TELA SEGUINTE!
            }
            is LoginUiState.Error -> {
                Toast.makeText(context, "Erro: ${state.message}", Toast.LENGTH_LONG).show()
            }
            else -> Unit
        }
    }
}