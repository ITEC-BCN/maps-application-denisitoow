import androidx.compose.animation.AnimatedContentScope
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.mapsapp.ui.navigation.Destination.Map
import com.example.mapsapp.ui.navigation.Destination.List
import com.example.mapsapp.ui.navigation.Destination.MarkerCreation
import com.example.mapsapp.ui.navigation.Destination.MarkerDetails
import com.example.mapsapp.ui.screens.*
import com.example.mapsapp.ui.navigation.*

@Composable
fun InternalNavigationWrapper() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "Map") {

        composable<Map> {
            MapScreen()
        }

        composable<List> { }

        composable<MarkerCreation> { }

        composable<MarkerDetails>{ }
    }
}

