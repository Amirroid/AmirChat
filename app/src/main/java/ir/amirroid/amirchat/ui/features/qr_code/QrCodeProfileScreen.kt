package ir.amirroid.amirchat.ui.features.qr_code

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import ir.amirroid.amirchat.R
import ir.amirroid.amirchat.ui.theme.AmirChatTheme
import ir.amirroid.amirchat.utils.CircleShape
import ir.amirroid.amirchat.viewmodels.qr_code.QrCodeProfileViewModel
import kotlinx.coroutines.launch

@Composable
fun QrCodeProfileScreen(navigation: NavHostController) {
    val viewModel: QrCodeProfileViewModel = hiltViewModel()
    val bitmap by viewModel.bitmap.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val systemIsDark = isSystemInDarkTheme()
    val scope = rememberCoroutineScope()
    var isDark by remember {
        mutableStateOf(systemIsDark)
    }
    var offsetChange by remember {
        mutableStateOf(Offset.Zero)
    }
    var isThemeChangeInProgress by remember {
        mutableStateOf(false)
    }
    val progressTheme = remember {
        Animatable(0f)
    }
    DisposableEffect(key1 = Unit) {
        viewModel.generateQrCode("Amirreza")
        onDispose { }
    }
    AmirChatTheme(darkTheme = isDark) {
        QrCodeContent(
            context = context,
            bitmap = bitmap,
            navigation = navigation,
            isDark = isDark
        ) { theme, offset ->
            offsetChange = offset
            scope.launch {
                isThemeChangeInProgress = true
                progressTheme.animateTo(1f, tween(1000, easing = EaseIn))
                isDark = theme
                isThemeChangeInProgress = false
                progressTheme.snapTo(0f)
            }
        }
    }
    if (isThemeChangeInProgress) {
        AmirChatTheme(darkTheme = isDark.not()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape(progressTheme.value, offsetChange))
            ) {
                QrCodeContent(
                    context = context,
                    bitmap = bitmap,
                    navigation = navigation,
                    isDark = isDark.not(),
                    { _, _ -> }
                )
            }
        }
    }
}

@Composable
fun QrCodeContent(
    context: Context,
    bitmap: Bitmap?,
    navigation: NavController,
    isDark: Boolean,
    onChangeThemeRequest: (Boolean, Offset) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        AsyncImage(
            model = ImageRequest.Builder(context).data(R.drawable.wallpaper).crossfade(true)
                .crossfade(500).build(), contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.05f),
            contentScale = ContentScale.Crop,
            colorFilter = ColorFilter.tint(
                MaterialTheme.colorScheme.primary,
                BlendMode.SrcIn
            ),
        )
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Box {
                Card(
                    modifier = Modifier
                        .padding(top = 32.dp)
                        .fillMaxWidth(0.7f),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        AsyncImage(
                            model = bitmap,
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(
                                MaterialTheme.colorScheme.primaryContainer,
                                BlendMode.Lighten
                            )
                        )
                        Text(
                            text = "@Amirreza",
                            style = TextStyle(
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold,
                                brush = Brush.linearGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.primaryContainer,
                                    )
                                ),
                            ),
                        )
                    }
                }
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data("https://www.alamto.com/wp-content/uploads/2023/05/flower-9.jpg")
                        .crossfade(true).crossfade(200).build(), contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .clip(CircleShape)
                        .size(64.dp)
                        .border(2.dp, MaterialTheme.colorScheme.primaryContainer, CircleShape)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Surface(
                shape = RoundedCornerShape(
                    topEnd = 16.dp,
                    topStart = 16.dp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                color = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .navigationBarsPadding()
                        .padding(vertical = 16.dp)
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(id = R.string.theme) + ":",
                            style = MaterialTheme.typography.titleMedium.copy(MaterialTheme.colorScheme.onSurface)
                        )
                        var offsetForClick by remember {
                            mutableStateOf(Offset.Zero)
                        }
                        IconButton(onClick = {
                            onChangeThemeRequest.invoke(isDark.not(), offsetForClick)
                        }, modifier = Modifier.onGloballyPositioned {
                            offsetForClick = Offset(
                                it.size.width.div(2) + it.positionInWindow().x,
                                it.size.height.div(2) + it.positionInWindow().y,
                            )
                        }) {
                            Icon(
                                painter = painterResource(
                                    if (isDark) {
                                        R.drawable.outline_wb_sunny_24
                                    } else {
                                        R.drawable.outline_dark_mode_24
                                    }
                                ), contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    Button(
                        onClick = {},
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .fillMaxWidth()
                            .height(64.dp),
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.round_send_24),
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = stringResource(id = R.string.share))
                    }
                }
            }
        }
        IconButton(onClick = { navigation.popBackStack() }, modifier = Modifier.statusBarsPadding().padding(12.dp)) {
            Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = null, tint = MaterialTheme.colorScheme.onBackground)
        }
    }
}