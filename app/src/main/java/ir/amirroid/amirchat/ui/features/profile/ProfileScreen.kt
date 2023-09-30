package ir.amirroid.amirchat.ui.features.profile

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.ImageLoader
import coil.load
import com.google.gson.Gson
import ir.amirroid.amirchat.R
import ir.amirroid.amirchat.data.models.register.UserModel
import ir.amirroid.amirchat.ui.components.SpannableText
import ir.amirroid.amirchat.ui.components.SwitchText
import ir.amirroid.amirchat.utils.ChatPages
import ir.amirroid.amirchat.utils.getName
import ir.amirroid.amirchat.utils.id
import ir.amirroid.amirchat.utils.setTint
import ir.amirroid.amirchat.viewmodels.profile.ProfileViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


@SuppressLint("InflateParams")
@Composable
fun ProfileScreen(navigation: NavController, user: UserModel) {
    val context = LocalContext.current
    val iconColor = MaterialTheme.colorScheme.onBackground
    val surfaceColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
    val primaryColor = MaterialTheme.colorScheme.primary
    val onPrimaryColor = MaterialTheme.colorScheme.onPrimary
    val backgroundColor = MaterialTheme.colorScheme.background
    val viewModel: ProfileViewModel = hiltViewModel()
    val status by viewModel.status.collectAsStateWithLifecycle()
    val connectingText = stringResource(id = R.string.connecting)
    val room by viewModel.room.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = user) {
        viewModel.observeToStatus(user.token)
        viewModel.connectToRooms(user)
    }
    val layout = remember {
        LayoutInflater.from(context).inflate(R.layout.profile_screen, null) as MotionLayout
    }
    LaunchedEffect(key1 = status) {
        withContext(Dispatchers.Main) {
            val statusText = layout.findViewById<TextView>(R.id.id_text)
            statusText.text = status?.getText(context) ?: connectingText
        }
    }
    AndroidView(
        {
            layout.jumpToState(R.id.end)
            val backIcon = layout.findViewById<ImageButton>(R.id.back)
            val surface = layout.findViewById<View>(R.id.surface)
            val fab = layout.findViewById<CardView>(R.id.floating_action_button)
            val fabImage = layout.findViewById<ImageView>(R.id.fab_image)
            val imageProfile = layout.findViewById<ImageView>(R.id.image)
            val boxInfo = layout.findViewById<ComposeView>(R.id.box_info)
//            val textInfo = layout.findViewById<TextView>(R.id.text_info)
//            val bioUser = layout.findViewById<TextView>(R.id.bio_user)
            val name = layout.findViewById<TextView>(R.id.name)
//            val usernameTextView = layout.findViewById<TextView>(R.id.username_text)
//            val bioTextView = layout.findViewById<TextView>(R.id.bio)
            val call = layout.findViewById<ImageView>(R.id.call)
            val more = layout.findViewById<ImageView>(R.id.more)
//            val qrCodeButton = layout.findViewById<ImageView>(R.id.qr_code)
//            textInfo.setTextColor(primaryColor.toArgb())
//            bioUser.setTextColor(iconColor.toArgb())
//            bioTextView.setTextColor(iconColor.toArgb())
//            usernameTextView.setTextColor(iconColor.toArgb())
//            usernameTextViewUser.setTextColor(iconColor.toArgb())
            name.text = user.getName()
            imageProfile.load(user.profilePictureUrl) {
                placeholder(R.drawable.user_default)
                error(R.drawable.user_default)
            }
            boxInfo.setBackgroundColor(backgroundColor.toArgb())
            backIcon.setTint(iconColor.toArgb())
//            qrCodeButton.setTint(iconColor.toArgb())
            backIcon.setOnClickListener { navigation.popBackStack() }
            more.setTint(iconColor.toArgb())
            call.setTint(iconColor.toArgb())
            surface.setBackgroundColor(surfaceColor.toArgb())
            fab.setCardBackgroundColor(primaryColor.toArgb())
            fab.setOnClickListener {
                if (navigation.previousBackStackEntry?.arguments?.getString("id") == room?.id) {
                    navigation.popBackStack()
                } else {
                    navigation.navigate(
                        ChatPages.ChatScreen.route + "?id=" + room?.id + "&user=" + Gson().toJson(
                            user
                        )
                    )
                }
            }
            fabImage.setTint(onPrimaryColor.toArgb())
            boxInfo.setContent {
                Surface(
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier
                        .focusable(false)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Column {
                        Text(
                            text = stringResource(id = R.string.info),
                            style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.padding(top = 12.dp, start = 12.dp)
                        )
                        Row(modifier = Modifier
                            .padding(top = 8.dp)
                            .fillMaxWidth()
                            .clickable {

                            }
                            .focusable(false)
                            .padding(vertical = 12.dp, horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text(
                                    text = "@" + user.userId,
                                )
                                Text(
                                    text = stringResource(id = R.string.id),
                                    style = MaterialTheme.typography.labelMedium,
                                    modifier = Modifier
                                        .alpha(0.6f)
                                        .padding(top = 4.dp)
                                )
                            }
                            IconButton(onClick = {
                                navigation.navigate(
                                    ChatPages.QrCodeProfileScreen.route + "?user=" + Gson().toJson(
                                        user
                                    )
                                )
                            }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.round_qr_code_24),
                                    contentDescription = null,
                                    tint = primaryColor
                                )
                            }
                        }
                        if (user.bio.isNotEmpty()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp)
                                    .clickable {}
                            ) {
                                SpannableText(
                                    text = user.bio, color = MaterialTheme.colorScheme.onBackground
                                ) { link, isUser ->
                                    if (isUser) {

                                    } else {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                                        context.startActivity(intent)
                                    }
                                }
                                Text(
                                    text = stringResource(id = R.string.bio),
                                    style = MaterialTheme.typography.labelMedium,
                                    modifier = Modifier
                                        .alpha(0.6f)
                                        .padding(top = 4.dp)
                                )
                            }
                        }
                        Divider(
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .align(Alignment.End)
                                .padding(vertical = 8.dp)
                        )

                        AnimatedVisibility(
                            visible = room != null,
                            enter = expandVertically(expandFrom = Alignment.Bottom),

                            ) {
                            SwitchText(text = stringResource(id = R.string.notifications),
                                checked = room?.myNotificationEnabled() == true,
                                onCheckedChange = { viewModel.setMyRoomNotificationEnabled(it) })
                        }

                    }
                }
            }
            layout
        }, modifier = Modifier
            .background(surfaceColor)
            .statusBarsPadding()
    )
}