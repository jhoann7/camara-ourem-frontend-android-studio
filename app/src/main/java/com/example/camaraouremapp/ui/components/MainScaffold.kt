package com.example.camaraouremapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.camaraouremapp.R
import com.example.camaraouremapp.ui.theme.VermelhoCamara
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(
    screenTitle: String,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = screenTitle) },
                navigationIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.logo_camara),
                        contentDescription = "Logo Câmara de Ourém",
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = VermelhoCamara, // Cor de fundo da barra
                    titleContentColor = Color.White, // Cor do texto do título
                )
            )
        }
    ) { paddingValues ->
        content(paddingValues)
    }
}