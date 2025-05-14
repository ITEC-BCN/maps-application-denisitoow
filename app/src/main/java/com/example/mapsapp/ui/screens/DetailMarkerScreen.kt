package com.example.mapsapp.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.mapsapp.viewmodels.MarkerViewModel
import java.io.File

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DetailMarkerScreen(markerId: Int, onBack: () -> Unit) {
    val markerViewModel: MarkerViewModel = viewModel()
    val context = LocalContext.current
    val markers by markerViewModel.markerList.observeAsState(emptyList())

    LaunchedEffect(Unit) {
        markerViewModel.refreshMarkers()
    }

    if (markers.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF000000)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color(0xFF6200EE))
        }
        return
    }

    val selected = markers.find { it.id == markerId } ?: return
    var nombre by remember { mutableStateOf(selected.nombre) }
    var descripcion by remember { mutableStateOf(selected.descripcion) }
    var newImage by remember { mutableStateOf<Bitmap?>(null) }
    // Control para abrir el diálogo de selección de imagen al pulsar el botón
    var showDialog by remember { mutableStateOf(false) }
    val imageUri = remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            newImage = imageUri.value?.let { uri ->
                context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it) }
            }
        }
    }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            imageUri.value = it
            newImage = context.contentResolver.openInputStream(it)?.use { BitmapFactory.decodeStream(it) }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF000000))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Editar Marcador",
            fontSize = 24.sp,
            color = Color(0xFF6200EE),
            fontWeight = FontWeight.Bold
        )

        newImage?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = "Imagen Seleccionada",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
        }

        TextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )

        TextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth()
        )

        // Botón para abrir el diálogo de cambiar imagen
        Button(
            onClick = { showDialog = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
        ) {
            Text(text = "Cambiar Imagen", color = Color.Black, fontWeight = FontWeight.Bold)
        }

        // Estado para mostrar mensaje si no hay imagen seleccionada
        var showNoImageMessage by remember { mutableStateOf(false) }
        if (showNoImageMessage) {
            Text(
                text = "Debes seleccionar una imagen antes de guardar",
                color = Color.Red,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // Botón para guardar cambios
        Button(
            onClick = {
                if (newImage != null) {
                    markerViewModel.updateMarker(
                        selected.copy(nombre = nombre, descripcion = descripcion),
                        newImage
                    )
                    onBack()
                } else {
                    showNoImageMessage = true
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
        ) {
            Text(text = "Guardar Cambios", color = Color.Black, fontWeight = FontWeight.Bold)
        }

        // Botón para eliminar el marcador
        Button(
            onClick = {
                markerViewModel.deleteMarker(selected)
                onBack()
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Text(text = "Eliminar Marcador", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Selecciona una imagen", color = Color.White) },
            text = { Text("Debes tomar una foto o elegir de galería", color = Color.LightGray) },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    val uri = createImageUri(context)
                    imageUri.value = uri
                    launcher.launch(uri!!)
                }) {
                    Text("Tomar Foto", color = Color(0xFF6200EE))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDialog = false
                    pickImageLauncher.launch("image/*")
                }) {
                    Text("Elegir de Galería", color = Color(0xFF6200EE))
                }
            },
            containerColor = Color(0xFF1F1B24)
        )
    }
}
