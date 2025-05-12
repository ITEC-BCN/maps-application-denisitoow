package com.example.mapsapp.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.mapsapp.ui.navigation.DrawerItem
import com.example.mapsapp.ui.navigation.InternalNavigationWrapper
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawerScreen() {
    val navController = rememberNavController()

    // Estado que controla si el Drawer está abierto o cerrado
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    // CoroutineScope para manejar acciones asíncronas como abrir o cerrar el Drawer
    val scope = rememberCoroutineScope()

    // Estado para guardar cuál ítem del Drawer está seleccionado actualmente
    var selectedItemIndex by remember { mutableStateOf(0) }

    // Componente principal que contiene el Drawer y el contenido principal de la pantalla
    ModalNavigationDrawer(
        gesturesEnabled = false, // Desactivamos gestos para controlar el Drawer solo con el botón
        drawerContent = {
            ModalDrawerSheet {
                // Iteramos sobre todos los ítems definidos en DrawerItem para mostrarlos en el Drawer
                DrawerItem.entries.forEachIndexed { index, drawerItem ->
                    NavigationDrawerItem(
                        icon = { Icon(imageVector = drawerItem.icon, contentDescription = drawerItem.text) },
                        label = { Text(text = drawerItem.text) },
                        selected = index == selectedItemIndex,
                        onClick = {
                            // Actualizamos el ítem seleccionado
                            selectedItemIndex = index
                            // Cerramos el Drawer usando una corrutina
                            scope.launch { drawerState.close() }
                            // Navegamos a la ruta asociada al ítem seleccionado
                            navController.navigate(drawerItem.route)
                        }
                    )
                }
            }
        },
        drawerState = drawerState
    ) {
        // Scaffold nos proporciona la estructura básica con AppBar y contenido
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Maps App") },
                    navigationIcon = {
                        // Botón para abrir el Drawer (icono de hamburguesa)
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            }
        ) { innerPadding ->
            InternalNavigationWrapper(
                navController = navController,
                padding = Modifier.padding(innerPadding)
            )
        }
    }
}