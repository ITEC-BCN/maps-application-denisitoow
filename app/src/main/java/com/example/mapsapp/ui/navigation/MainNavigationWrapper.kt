package com.example.mapsapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mapsapp.ui.navigation.Destination.Drawer
import com.example.mapsapp.ui.navigation.Destination.Permissions
import com.example.mapsapp.ui.screens.DrawerScreen
import com.example.mapsapp.ui.screens.PermissionsScreen

@Composable
fun MainNavigationWrapper(){
    val navController = rememberNavController()
    NavHost(navController = navController, Permissions){
        composable<Permissions> {
            PermissionsScreen{
                navController.navigate(Drawer)
            }
        }
        composable<Drawer> {
            DrawerScreen()
        }
    }
}