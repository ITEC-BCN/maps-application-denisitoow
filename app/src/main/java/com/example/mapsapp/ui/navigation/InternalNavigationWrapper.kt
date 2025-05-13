package com.example.mapsapp.ui.navigation


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.mapsapp.ui.screens.CreateMarkerScreen
import com.example.mapsapp.ui.screens.DetailMarkerScreen
import com.example.mapsapp.ui.screens.MapScreen
import com.example.mapsapp.ui.screens.MarkerListScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun InternalNavigationWrapper(
    navController: NavHostController,
    padding: Modifier,
    function: () -> Unit = {}
) {
    NavHost(
        navController = navController,
        startDestination = Destination.Map,
        modifier = padding
    ) {
        composable<Destination.Map> {
            MapScreen(
                NavegarAlDetalle = { markerId ->
                    navController.navigate(Destination.MarkerDetails(markerId))
                },
                NavegarACrearMarker = { coordenadas ->
                    navController.navigate(Destination.MarkerCreation(coordenadas = coordenadas))
                }
            )
        }
        composable<Destination.List> { backStackEntry ->
            MarkerListScreen { markerId ->
                navController.navigate(Destination.MarkerDetails(markerId))
            }
        }
        composable<Destination.MarkerCreation> { backStackEntry ->
            val markerCreation = backStackEntry.toRoute<Destination.MarkerCreation>()
            CreateMarkerScreen(coordenadas = markerCreation.coordenadas)  {
                navController.navigate(Destination.Map) {
                    popUpTo<Destination.Map> { inclusive = true }
                }
            }
        }
        composable<Destination.MarkerDetails> { backStackEntry ->
            val markerDetails = backStackEntry.toRoute<Destination.MarkerDetails>()
            DetailMarkerScreen(markerId = markerDetails.id) {
                navController.navigate(Destination.List) {
                    popUpTo<Destination.List> { inclusive = true }
                }
            }
        }
    }
}
