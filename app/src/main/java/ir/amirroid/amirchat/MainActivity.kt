package ir.amirroid.amirchat

import android.os.Build
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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import ir.amirroid.amirchat.data.auth.AuthManager
import ir.amirroid.amirchat.data.helpers.TokenHelper
import ir.amirroid.amirchat.data.models.chat.ChatRoom
import ir.amirroid.amirchat.data.models.register.CurrentUser
import ir.amirroid.amirchat.data.models.register.UserModel
import ir.amirroid.amirchat.ui.features.chat.ChatScreen
import ir.amirroid.amirchat.ui.features.home.HomeScreen
import ir.amirroid.amirchat.ui.features.profile.ProfileScreen
import ir.amirroid.amirchat.ui.features.qr_code.QrCodeProfileScreen
import ir.amirroid.amirchat.ui.features.register.RegisterScreen
import ir.amirroid.amirchat.ui.features.search.SearchScreen
import ir.amirroid.amirchat.ui.theme.AmirChatTheme
import ir.amirroid.amirchat.utils.ChatPages
import ir.amirroid.amirchat.utils.CircleShape
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var authManager: AuthManager
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
                        MainScreen(authManager)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainScreen(authManager: AuthManager) {
    val isUser = remember {
        CurrentUser.token?.isNotEmpty() == true
    }
    val navController = rememberAnimatedNavController()
    LaunchedEffect(key1 = isUser) {
        if (isUser) {
            authManager.getMyUser {
                navController.navigate(ChatPages.HomeScreen.route)
            }
        }
    }
    AnimatedNavHost(
        navController = navController,
        startDestination = if (isUser) ChatPages.SplashScreen.route else ChatPages.RegisterScreen.route,
        enterTransition = { slideInHorizontally(tween(300)) { 200 } + fadeIn(tween(200)) },
        exitTransition = { slideOutHorizontally(tween(300)) { -200 } + fadeOut(tween(200)) },
        popExitTransition = { slideOutHorizontally(tween(300)) { 200 } + fadeOut(tween(200)) },
        popEnterTransition = { slideInHorizontally(tween(300)) { -200 } + fadeIn(tween(200)) },
        modifier = Modifier.fillMaxSize()
    ) {
        composable(ChatPages.SplashScreen.route) {
            SplashScreen()
        }
        composable(ChatPages.RegisterScreen.route) {
            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onBackground) {
                RegisterScreen(navController)
            }
        }
        composable(ChatPages.HomeScreen.route) {
            HomeScreen(navController)
        }
        composable(
            ChatPages.ChatScreen.route + "?id={id}&user={user}",
            arguments = listOf(navArgument("id") {
                type = NavType.StringType
                nullable = true
            }, navArgument("user") {
                type = NavType.StringType
                nullable = false
            })
        ) {
            val room = it.arguments?.getString("id")
            val user = it.arguments?.getString("user") ?: ""
            ChatScreen(room, Gson().fromJson(user, UserModel::class.java), navController)
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


@Composable
fun SplashScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier
                .size(108.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary

        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Icon(
                    painter = painterResource(id = R.drawable.round_send_24),
                    contentDescription = stringResource(id = R.string.app_name),
                    modifier = Modifier
                        .size(48.dp)
                        .rotate(-45f)
                )
            }
        }
        Text(
            text = stringResource(id = R.string.app_name),
            modifier = Modifier.padding(top = 12.dp),
            style = MaterialTheme.typography.titleLarge.copy(
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold
            )
        )
    }
}