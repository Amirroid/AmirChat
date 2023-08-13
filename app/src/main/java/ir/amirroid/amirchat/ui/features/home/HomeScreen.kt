package ir.amirroid.amirchat.ui.features.home

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import ir.amirroid.amirchat.R
import ir.amirroid.amirchat.ui.components.ChatPopUp
import ir.amirroid.amirchat.ui.components.UserListItem
import ir.amirroid.amirchat.utils.ChatPages
import ir.amirroid.amirchat.utils.SimpleList
import ir.amirroid.amirchat.viewmodels.ChatViewModel
import kotlinx.coroutines.launch
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigation: NavController
) {
    val statusBarColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
        1.dp
    )
    val profiles = SimpleList.listProfiles
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val density = LocalDensity.current
    val context = LocalContext.current
    val matrix = context.resources.displayMetrics
    val widthDrawer = matrix.widthPixels * .7f
    val widthDpDrawer = with(density) { widthDrawer.toDp() }
    var showChatPopUp by remember {
        mutableStateOf(false)
    }
    val blurChats by animateDpAsState(targetValue = if (showChatPopUp) 10.dp else 0.dp, label = "")
    BackHandler {
        if (showChatPopUp) {
            showChatPopUp = false
        } else (context as Activity).finish()
    }
    ModalNavigationDrawer(
        drawerContent = {
            DrawerContent(
                widthDpDrawer,
                context
            )
        },
        drawerState = drawerState,
        modifier = Modifier.blur(blurChats)
    ) {
        Log.d("fsfsdfs", "HomeScreen: ${drawerState.offset.value}  --  $widthDrawer")
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = {}) {
                    Icon(imageVector = Icons.Rounded.Edit, contentDescription = "edit")
                }
            },
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(text = stringResource(id = R.string.app_name))
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                drawerState.open()
                            }
                        }) {
                            Icon(imageVector = Icons.Rounded.Menu, contentDescription = null)
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            navigation.navigate(ChatPages.SearchScreen.route)
                        }) {
                            Icon(imageVector = Icons.Rounded.Search, contentDescription = null)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = statusBarColor
                    )
                )
            },
            modifier = Modifier.offset(
                x = with(density) {
                    (100 * (1 - (abs(drawerState.offset.value) / widthDrawer))).toDp()
                        .coerceAtLeast(0.dp)
                }
            )
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                items(profiles.size) {
                    val user = profiles[it]
                    UserListItem(profile = user, density = density, onClick = {
                        navigation.navigate(ChatPages.ChatScreen.route)
                    }) {
                        showChatPopUp = true
                    }
                }
            }
        }
    }
    ChatPopUp(visible = showChatPopUp) {
        showChatPopUp = false
    }
}

@Composable
fun DrawerContent(
    widthDpDrawer: Dp,
    context: Context
) {
    Surface(
        modifier = Modifier
            .width(widthDpDrawer)
            .fillMaxHeight(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp))
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(vertical = 24.dp, horizontal = 12.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data("https://www.alamto.com/wp-content/uploads/2023/05/flower-9.jpg")
                        .crossfade(true)
                        .crossfade(500)
                        .build(),
                    contentDescription = "profile",
                    modifier = Modifier
                        .size(76.dp)
                        .clip(CircleShape)
                )
                Text(
                    text = "Amirreza Gholami",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 8.dp),
                    maxLines = 1
                )
                Text(
                    text = "09150211935",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier
                        .padding(top = 6.dp)
                        .alpha(0.8f),
                    maxLines = 1
                )
            }
            NavigationDrawerItem(
                label = { Text(text = stringResource(id = R.string.settings)) },
                selected = false,
                onClick = { },
                modifier = Modifier
                    .padding(top = 12.dp)
                    .padding(horizontal = 12.dp)
            )
            NavigationDrawerItem(
                label = { Text(text = stringResource(id = R.string.saved_messages)) },
                selected = false,
                onClick = { },
                modifier = Modifier
                    .padding(top = 6.dp)
                    .padding(horizontal = 12.dp)
            )
        }
    }
}
