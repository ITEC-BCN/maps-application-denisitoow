package com.example.mapsapp

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.ComponentActivity
import com.example.mapsapp.ui.screens.MapScreen
import com.example.mapsapp.ui.theme.MapsAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MapsAppTheme {
                MapScreen()
            }
        }
    }
}


