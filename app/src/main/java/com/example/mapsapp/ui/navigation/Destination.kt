package com.example.mapsapp.ui.navigation

import kotlinx.serialization.Serializable

sealed class Destination {
    @Serializable
    object Permissions : Destination()
    @Serializable
    object Drawer : Destination()
    @Serializable
    object Map : Destination()
    @Serializable
    object List : Destination()
    @Serializable
    data class MarkerCreation(val coordenadas: String) : Destination()
    @Serializable
    data class MarkerDetails(val id: Int) : Destination()
}