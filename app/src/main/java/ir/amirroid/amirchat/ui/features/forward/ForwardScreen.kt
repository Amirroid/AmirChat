package ir.amirroid.amirchat.ui.features.forward

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.gson.Gson
import ir.amirroid.amirchat.R
import ir.amirroid.amirchat.data.models.chat.FileMessage
import ir.amirroid.amirchat.data.models.chat.MessageModel
import ir.amirroid.amirchat.ui.components.LoadingDialog
import ir.amirroid.amirchat.ui.components.UserListItem
import ir.amirroid.amirchat.utils.ChatPages
import ir.amirroid.amirchat.utils.Constants
import ir.amirroid.amirchat.utils.getBasicColorsOfTextField
import ir.amirroid.amirchat.utils.getNavigationBarHeight
import ir.amirroid.amirchat.utils.getTypeForFile
import ir.amirroid.amirchat.utils.id
import ir.amirroid.amirchat.utils.toJsonMusic
import ir.amirroid.amirchat.utils.toMediaJson
import ir.amirroid.amirchat.viewmodels.forward.ForwardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForwardScreen(
    messages: List<MessageModel>,
    navigation: NavController
) {
    val viewModel: ForwardViewModel = hiltViewModel()
    val rooms by viewModel.rooms.collectAsStateWithLifecycle()
    val selectedRooms = viewModel.selectedRooms
    val loading = viewModel.loading
    val context = LocalContext.current
    val appBarColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = {
                Text(text = stringResource(id = R.string.forward_to))
            }, colors = TopAppBarDefaults.centerAlignedTopAppBarColors(appBarColor))
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = selectedRooms.isNotEmpty(),
                enter = scaleIn(),
                exit = scaleOut()
            ) {
                FloatingActionButton(onClick = {
                    viewModel.sendMessages(messages) {
                        if (selectedRooms.size == 1) {
                            if (navigation.previousBackStackEntry?.arguments?.getString("id") == selectedRooms.first().id) {
                                navigation.popBackStack()
                            } else {
                                navigation.navigate(
                                    ChatPages.ChatScreen.route + "?id=" + selectedRooms.first().id + "&user=" + Gson().toJson(
                                        selectedRooms.first().getToChatUser()
                                    )
                                )
                            }
                        } else {
                            navigation.popBackStack()
                        }
                    }
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_done_24),
                        contentDescription = null
                    )
                }
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(rooms.size, key = { rooms[it].id }) {
                val room = rooms[it]
                UserListItem(
                    room = room,
                    selected = selectedRooms.contains(room),
                    selectionMode = selectedRooms.isNotEmpty(),
                    onClick = { viewModel.toggleRoom(room) },
                    onLongClick = { viewModel.toggleRoom(room) },
                    onImageLongClick = { viewModel.toggleRoom(room) },
                    context = context,
                    numbers = 0
                )
            }
        }
    }
    if (loading){
        LoadingDialog()
    }
}