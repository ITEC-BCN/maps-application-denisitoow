package com.example.mapsapp.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mapsapp.viewmodels.MarkerViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapScreen(NavegarAlDetalle: (Int) -> Unit, NavegarACrearMarker: (String) -> Unit) {
    val markerViewModel: MarkerViewModel = viewModel()

    val markers by markerViewModel.markerList.observeAsState(emptyList())

    // Refrescar solo una vez al entrar en la pantalla
    LaunchedEffect(Unit) {
        markerViewModel.refreshMarkers()
    }

    //Carga los marcadores de Supabase
    if (markers.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(androidx.compose.ui.graphics.Color(0xFF000000)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = androidx.compose.ui.graphics.Color(0xFF6200EE))
        }
        return
    }

    Column(modifier = Modifier.fillMaxSize()) {
        val initialPosition = LatLng(41.4534225, 2.1837151)
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(initialPosition, 17f)
        }

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapLongClick = { latLng ->
                val coordenadas = "${latLng.latitude},${latLng.longitude}"
                NavegarACrearMarker(coordenadas)
            }
        ) {
            markers.forEach { marker ->
                Log.d("DEBUG_MARKER", "Marcador: ${marker.nombre} - ${marker.latlng}")
                val latLngParts = marker.latlng.split(",")
                if (latLngParts.size == 2) {
                    val lat = latLngParts[0].trim().toDoubleOrNull()
                    val lon = latLngParts[1].trim().toDoubleOrNull()
                    if (lat != null && lon != null) {
                        val position = LatLng(lat, lon)
                        Marker(
                            state = MarkerState(position = position),
                            title = marker.nombre,
                            snippet = marker.descripcion,
                            onClick = {
                                NavegarAlDetalle(marker.id)
                                false
                            }
                        )
                    } else {
                        Log.e("DEBUG_MARKER", "Error convirtiendo latlng: ${marker.latlng}")
                    }
                } else {
                    Log.e("DEBUG_MARKER", "Formato incorrecto latlng: ${marker.latlng}")
                }
            }
        }
    }
}