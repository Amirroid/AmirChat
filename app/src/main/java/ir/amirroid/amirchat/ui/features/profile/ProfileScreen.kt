package ir.amirroid.amirchat.ui.features.profile

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
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
import androidx.navigation.NavController
import coil.ImageLoader
import coil.load
import ir.amirroid.amirchat.R
import ir.amirroid.amirchat.ui.components.SwitchText
import ir.amirroid.amirchat.utils.ChatPages
import ir.amirroid.amirchat.utils.setTint


@SuppressLint("InflateParams")
@Composable
fun ProfileScreen(navigation: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val iconColor = MaterialTheme.colorScheme.onBackground
    val surfaceColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
    val primaryColor = MaterialTheme.colorScheme.primary
    val onPrimaryColor = MaterialTheme.colorScheme.onPrimary
    val backgroundColor = MaterialTheme.colorScheme.background
    AndroidView(
        {
            val layout: MotionLayout = LayoutInflater.from(context)
                .inflate(R.layout.profile_screen, null) as MotionLayout
            layout.jumpToState(R.id.end)
            val backIcon = layout.findViewById<ImageButton>(R.id.back)
            val surface = layout.findViewById<View>(R.id.surface)
            val fab = layout.findViewById<CardView>(R.id.floating_action_button)
            val fabImage = layout.findViewById<ImageView>(R.id.fab_image)
            val imageProfile = layout.findViewById<ImageView>(R.id.image)
            val boxInfo = layout.findViewById<ComposeView>(R.id.box_info)
//            val textInfo = layout.findViewById<TextView>(R.id.text_info)
//            val bioUser = layout.findViewById<TextView>(R.id.bio_user)
//            val usernameTextViewUser = layout.findViewById<TextView>(R.id.username)
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
            boxInfo.setBackgroundColor(backgroundColor.toArgb())
            backIcon.setTint(iconColor.toArgb())
//            qrCodeButton.setTint(iconColor.toArgb())
            backIcon.setOnClickListener { navigation.popBackStack() }
            more.setTint(iconColor.toArgb())
            call.setTint(iconColor.toArgb())
            surface.setBackgroundColor(surfaceColor.toArgb())
            fab.setCardBackgroundColor(primaryColor.toArgb())
            fabImage.setTint(onPrimaryColor.toArgb())
            imageProfile.load("https://www.alamto.com/wp-content/uploads/2023/05/flower-9.jpg")
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
                        Column(
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .fillMaxWidth()
                                .clickable {

                                }
                                .focusable(false)
                                .padding(vertical = 12.dp, horizontal = 12.dp)
                        ) {
                            Text(
                                text = "Amirreza Gholami bio",
                            )
                            Text(
                                text = stringResource(id = R.string.bio),
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier
                                    .alpha(0.6f)
                                    .padding(top = 4.dp)
                            )
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {

                                }
                                .padding(vertical = 12.dp, horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                            ) {
                                Text(
                                    text = "@Amirreza",
                                )
                                Text(
                                    text = stringResource(id = R.string.bio),
                                    style = MaterialTheme.typography.labelMedium,
                                    modifier = Modifier
                                        .alpha(0.6f)
                                        .padding(top = 4.dp)
                                )
                            }
                            IconButton(onClick = {
                                navigation.navigate(ChatPages.QrCodeProfileScreen.route)
                            }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.round_qr_code_24),
                                    contentDescription = null,
                                    tint = primaryColor
                                )
                            }
                        }
                        Divider(
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .align(Alignment.End)
                                .padding(vertical = 8.dp)
                        )
                        var c by remember {
                            mutableStateOf(false)
                        }
                        SwitchText(
                            text = stringResource(id = R.string.notifications),
                            checked = c,
                            onCheckedChange = { c = it })

                    }
                }
            }
            layout
        }, modifier = Modifier
            .background(surfaceColor)
            .statusBarsPadding()
    )
}