import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.example.kmpgame.HomeScreen
import org.example.kmpgame.MainViewModel
import org.example.kmpgame.Routes
import org.example.kmpgame.SignUpScreen

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun App(mainViewModel: MainViewModel = viewModel()) {
    MaterialTheme {
        val greetings by mainViewModel.getGreetingList.collectAsStateWithLifecycle()

         val navController= rememberNavController()

        val snackbarHostState = remember { SnackbarHostState() }

        LaunchedEffect(navController) {
            mainViewModel.userCreated.collect { message ->
                snackbarHostState.showSnackbar(message)
                navController.popBackStack() // Go back to home after success
            }
        }

        Scaffold (snackbarHost = { SnackbarHost(snackbarHostState) }){
            paddingValues ->
            NavHost(
                navController = navController,
                startDestination = Routes.HOME,
                modifier = Modifier.fillMaxSize().padding(paddingValues)
            ) {
                composable(Routes.HOME){
                    HomeScreen(onNavigateToSignUp = {
                        navController.navigate(Routes.SIGN_UP)
                    })
                }
                composable (Routes.SIGN_UP){
                    val isLoading by mainViewModel.isLoading.collectAsStateWithLifecycle()
                    SignUpScreen(isLoading=isLoading,onUserCreate = {name, email->mainViewModel.onCreateUserClick(name,email)})
                }
            }
        }
        
    }
}