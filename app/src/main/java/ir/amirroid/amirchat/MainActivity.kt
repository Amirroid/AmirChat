package ir.amirroid.amirchat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import dagger.hilt.android.AndroidEntryPoint
import ir.amirroid.amirchat.data.auth.AuthManager
import ir.amirroid.amirchat.data.helpers.TokenHelper
import ir.amirroid.amirchat.data.models.register.CurrentUser
import ir.amirroid.amirchat.ui.features.chat.ChatScreen
import ir.amirroid.amirchat.ui.features.home.HomeScreen
import ir.amirroid.amirchat.ui.features.profile.ProfileScreen
import ir.amirroid.amirchat.ui.features.qr_code.QrCodeProfileScreen
import ir.amirroid.amirchat.ui.features.register.RegisterScreen
import ir.amirroid.amirchat.ui.features.search.SearchScreen
import ir.amirroid.amirchat.ui.theme.AmirChatTheme
import ir.amirroid.amirchat.utils.ChatPages
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var tokenHelper: TokenHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AmirChatTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    color = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .imePadding()
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        MainScreen(CurrentUser.token?.isNotEmpty() == true)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainScreen(isUser: Boolean) {
    val navController = rememberAnimatedNavController()
    AnimatedNavHost(
        navController = navController,
        startDestination = if (isUser) ChatPages.HomeScreen.route else ChatPages.RegisterScreen.route,
        enterTransition = { slideInHorizontally(tween(300)) { 200 } + fadeIn(tween(200)) },
        exitTransition = { slideOutHorizontally(tween(300)) { -200 } + fadeOut(tween(200)) },
        popExitTransition = { slideOutHorizontally(tween(300)) { 200 } + fadeOut(tween(200)) },
        popEnterTransition = { slideInHorizontally(tween(300)) { -200 } + fadeIn(tween(200)) },
        modifier = Modifier.fillMaxSize()
    ) {
        composable(ChatPages.RegisterScreen.route) {
            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onBackground) {
                RegisterScreen(navController)
            }
        }
        composable(ChatPages.HomeScreen.route) {
            HomeScreen(navController)
        }
        composable(ChatPages.ChatScreen.route) {
            ChatScreen(navController)
        }
        composable(ChatPages.ProfileScreen.route,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popExitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None }
        )
        {
            ProfileScreen(navController)
        }
        composable(ChatPages.QrCodeProfileScreen.route)
        {
            QrCodeProfileScreen(navController)
        }
        composable(ChatPages.SearchScreen.route)
        {
            SearchScreen(navController)
        }
    }
}
