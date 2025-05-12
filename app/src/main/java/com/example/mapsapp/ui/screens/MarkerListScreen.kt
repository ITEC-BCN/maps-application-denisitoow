package com.example.mapsapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mapsapp.viewmodels.MarkerViewModel

@Composable
fun MarkerListScreen() {
    // Aquí obtengo la instancia de mi ViewModel
    val markerViewModel: MarkerViewModel = viewModel()

    // Observo la lista de marcadores como estado (para que Compose se entere de los cambios)
    val markers by markerViewModel.markerList.observeAsState(emptyList())

    // Pido al ViewModel que cargue los marcadores de Supabase
    markerViewModel.refreshMarkers()

    // LazyColumn minimalista, fondo negro, alineación centrada
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF000000))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(markers) { marker ->
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .wrapContentHeight(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1F1B24)),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Icono de ubicación en morado
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color(0xFF6200EE),
                        modifier = Modifier.size(40.dp)
                    )
                    // Nombre del marcador en morado y negrita, centrado
                    Text(
                        text = marker.nombre,
                        fontSize = 18.sp,
                        color = Color(0xFF6200EE),
                        fontWeight = FontWeight.Bold
                    )
                    // Descripción en gris claro, centrado
                    Text(
                        text = marker.descripcion,
                        fontSize = 14.sp,
                        color = Color(0xFFCCCCCC)
                    )
                }
            }
        }
    }
}