package com.filipsarlej.yourtube.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.filipsarlej.yourtube.domain.repository.AuthRepository
import com.filipsarlej.yourtube.ui.account.AccountScreen
import com.filipsarlej.yourtube.ui.detail.ChannelDetailScreen
import com.filipsarlej.yourtube.ui.login.LoginScreen
import com.filipsarlej.yourtube.ui.subscriptions.SubscriptionListScreen
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val SUBSCRIPTIONS = "subscriptions"
    const val ACCOUNT = "account"
    const val CHANNEL_DETAIL = "channel_detail/{channelId}"

    // Pomocná funkce pro vytvoření cesty s konkrétním ID
    fun channelDetail(channelId: String) = "channel_detail/$channelId"
}



@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    val currentAccount: StateFlow<GoogleSignInAccount?> = authRepository.currentAccount

    fun setAccount(account: GoogleSignInAccount) {
        authRepository.setAccount(account)
    }
}

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        // Startovní destinace je VŽDY splash screen.
        startDestination = Routes.SPLASH
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(navController = navController)
        }

        composable(Routes.LOGIN) {
            val splashViewModel: SplashViewModel = hiltViewModel()

            LoginScreen(
                onLoginSuccess = { account ->
                    splashViewModel.setAccount(account)
                    navController.navigate(Routes.SUBSCRIPTIONS) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onLoginFailed = {  }
            )
        }

        composable(Routes.SUBSCRIPTIONS) {
            SubscriptionListScreen(
                onNavigateToAccount = {
                    navController.navigate(Routes.ACCOUNT)
                },

                onNavigateToDetail = { channelId ->
                    navController.navigate(Routes.channelDetail(channelId))
                }
            )
        }

        // Nový composable pro obrazovku detailu
        composable(
            route = Routes.CHANNEL_DETAIL,
            arguments = listOf(navArgument("channelId") { type = NavType.StringType })
        ) {
            ChannelDetailScreen(
                onNavigateUp = { navController.navigateUp() }
            )
        }

        composable(Routes.ACCOUNT) {
            AccountScreen(
                onLoggedOut = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.SUBSCRIPTIONS) { inclusive = true }
                    }
                }
            )
        }
    }
}

@Composable
fun SplashScreen(
    navController: NavHostController,
    viewModel: SplashViewModel = hiltViewModel()
) {

    val currentAccount by viewModel.currentAccount.collectAsStateWithLifecycle()

    // LaunchedEffect se spustí jednou, když se composable poprvé zobrazí.
    // Klíč `currentAccount` zajistí, že pokud by se stav změnil, zatímco je
    // uživatel na splash screenu, kód se spustí znovu.
    LaunchedEffect(key1 = currentAccount) {
        // Pokud je `currentAccount` null, znamená to, že se stav ještě načítá.
        // Počkáme, až bude mít hodnotu (přihlášen/nepřihlášen).
        if (currentAccount != null) { // Hodnota je k dispozici
            navController.navigate(Routes.SUBSCRIPTIONS) {
                // Vymažeme splash screen z historie, aby se na něj nedalo vrátit.
                popUpTo(Routes.SPLASH) { inclusive = true }
            }
        } else {
            navController.navigate(Routes.LOGIN) {
                popUpTo(Routes.SPLASH) { inclusive = true }
            }

        }


    }

    // Během rozhodování zobrazíme loading.
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}