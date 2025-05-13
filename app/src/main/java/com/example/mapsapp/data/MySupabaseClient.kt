package com.example.mapsapp.data

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.mapsapp.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MySupabaseClient {

    var client: SupabaseClient
    var storage: Storage
    val supabaseUrl = BuildConfig.SUPABASEURL
    private val supabaseKey = BuildConfig.SUPABASE_KEY

    constructor() {
        client = createSupabaseClient(supabaseUrl = supabaseUrl, supabaseKey = supabaseKey) {
            install(Postgrest)
            install(Storage)
        }
        storage = client.storage
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun uploadImage(imageFile: ByteArray): String {
        Log.d("SUPABASE", "Uploading image to: $supabaseUrl")
        val fechaHoraActual = LocalDateTime.now()
        val formato = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
        val imageName = storage.from("imagenes")
            .upload(path = "image_${fechaHoraActual.format(formato)}.png", data = imageFile)
        return buildImageUrl(imageFileName = imageName.path)
    }

    fun buildImageUrl(imageFileName: String) =
        "${this.supabaseUrl}/storage/v1/object/public/imagenes/${imageFileName}"

    suspend fun deleteImage(imageName: String) {
        val imgName = imageName.removePrefix("${this.supabaseUrl}/storage/v1/object/public/imagenes/")
        storage.from("imagenes").delete(imgName)
    }

    suspend fun getMarkers(): List<Marker> {
        return client.from("Marker").select().decodeList<Marker>()
    }

    suspend fun insertMarker(marker: Marker) {
        client.from("Marker").insert(marker)
    }

    suspend fun updateMarker(
        id: Int,
        nombre: String,
        descripcion: String,
        latlng: String,
        imageName: String,
        imageFile: ByteArray
    ) {
        val imageResult = storage.from("imagenes").update(path = imageName, data = imageFile)
        client.from("Marker").update({
            set("nombre", nombre)
            set("descripcion", descripcion)
            set("latlng", latlng)
            set("imageUrl", buildImageUrl(imageFileName = imageResult.path))
        }) {
            filter {
                eq("id", id)
            }
        }
    }

    suspend fun deleteMarker(id: String) {
        client.from("Marker").delete {
            filter {
                eq("id", id)
            }
        }
    }
}
