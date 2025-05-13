package com.example.mapsapp.viewmodels

import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mapsapp.MyApp
import com.example.mapsapp.data.Marker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class MarkerViewModel : ViewModel() {

    // Acceso al cliente de Supabase
    private val database = MyApp.database

    // Lista de marcadores
    private val _markerList = MutableLiveData<List<Marker>>()
    val markerList = _markerList

    // Marcador seleccionado
    private val _selectedMarker = MutableLiveData<Marker?>()
    val selectedMarker = _selectedMarker

    // Función para insertar un nuevo marcador siguiendo el modelo del usuario (nombre, descripcion, latlng, imageUrl)

    @RequiresApi(Build.VERSION_CODES.O)
    fun insertNewMarker(nombre: String, descripcion: String, latlng: String, image: Bitmap?) {
        CoroutineScope(Dispatchers.IO).launch {
            if (image != null) {
                // Convertimos la imagen a bytes para subirla a Supabase
                val stream = ByteArrayOutputStream()
                image.compress(Bitmap.CompressFormat.PNG, 100, stream)
                val imageBytes = stream.toByteArray()

                // Subimos la imagen y obtenemos la URL pública
                val imageUrl = database.uploadImage(imageBytes)

                // Creamos el objeto Marker con los campos del modelo del usuario
                val marker = Marker(
                    nombre = nombre,
                    descripcion = descripcion,
                    latlng = latlng,
                    imageUrl = imageUrl
                )

                // Insertamos el nuevo marcador en la base de datos
                database.insertMarker(marker)

                // Refrescamos la lista de marcadores para actualizar la interfaz
                refreshMarkers()
            }
        }
    }

    // Función para obtener todos los marcadores de Supabase y actualizar el LiveData
    fun refreshMarkers() {
        CoroutineScope(Dispatchers.IO).launch {
            val markers = database.getMarkers()
            withContext(Dispatchers.Main) {
                _markerList.value = markers
            }
        }
    }

    // Cuando seleccionamos un marcador para ver el detalle o editarlo
    fun selectMarker(marker: Marker) {
        _selectedMarker.value = marker
    }

    // Función para actualizar un marcador según el modelo del usuario (nombre, descripcion, latlng, imageUrl)
    // Si se pasa una nueva imagen, también la sube y actualiza la URL
    @RequiresApi(Build.VERSION_CODES.O)
    fun updateMarker(marker: Marker, newImage: Bitmap?) {
        CoroutineScope(Dispatchers.IO).launch {
            // Extraemos solo el nombre de la imagen de la URL para actualizarla en Supabase
            val imageName = marker.imageUrl.removePrefix("${database.supabaseUrl}/storage/v1/object/public/imagenes/")

            // Si se pasa una nueva imagen, la convertimos a bytes
            val stream = ByteArrayOutputStream()
            newImage?.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val imageBytes = stream.toByteArray()

            // Actualizamos el marcador usando los campos del modelo Marker del usuario
            database.updateMarker(
                id = marker.id ?: 0,
                nombre = marker.nombre,
                descripcion = marker.descripcion,
                latlng = marker.latlng,
                imageName = imageName,
                imageFile = imageBytes
            )

            // Refrescamos la lista de marcadores para reflejar los cambios
            refreshMarkers()
        }
    }

    // Función para eliminar un marcador (y su imagen del storage)
    fun deleteMarker(marker: Marker) {
        CoroutineScope(Dispatchers.IO).launch {
            // Borro primero la imagen del bucket en Supabase
            database.deleteImage(marker.imageUrl)
            // Luego borro el marcador de la base de datos
            database.deleteMarker(marker.id.toString())
            // Refresco la lista para que se actualice en la interfaz
            refreshMarkers()
        }
    }
}