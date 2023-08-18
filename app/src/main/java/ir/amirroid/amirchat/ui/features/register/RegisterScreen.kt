package ir.amirroid.amirchat.ui.features.register

import android.annotation.SuppressLint
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import ir.amirroid.amirchat.R
import ir.amirroid.amirchat.ui.components.AnimatedTextField
import ir.amirroid.amirchat.ui.components.CheckboxText
import ir.amirroid.amirchat.ui.components.NumberKeyboard
import ir.amirroid.amirchat.ui.components.OtpView
import ir.amirroid.amirchat.ui.components.SnackBar
import ir.amirroid.amirchat.utils.ChatPages
import ir.amirroid.amirchat.utils.checkMobile
import ir.amirroid.amirchat.viewmodels.register.RegisterViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("UnusedCrossfadeTargetStateParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun RegisterScreen(
    navigation: NavController
) {
    val viewModel: RegisterViewModel = hiltViewModel()
    var syncContacts by remember {
        mutableStateOf(true)
    }
    val phoneNumber = viewModel.phoneNumber
    val verifyCode = viewModel.verifyCode
    val currentCode = viewModel.currentCode
    val loading = viewModel.loading
    var snackBarMessage by remember {
        mutableStateOf("")
    }
    var snackBarVisible by remember {
        mutableStateOf(false)
    }
    val focusRequester = FocusRequester()
    val hapticFeedback = LocalHapticFeedback.current
    val pagerState = rememberPagerState {
        3
    }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    LaunchedEffect(key1 = Unit) {
        try {
            focusRequester.requestFocus()
        } catch (_: Exception) {
        }
    }
    DisposableEffect(key1 = Unit) {
        viewModel.startBroadCast()
        onDispose {
            viewModel.unRegisterBroadCast()
        }
    }
    LaunchedEffect(key1 = verifyCode) {
        if (verifyCode == currentCode && verifyCode.length == 6) {
            delay(1000)
            viewModel.checkMobile {
                if (it) {
                    viewModel.logInWithMobile {
                        navigation.navigate(ChatPages.HomeScreen.route)
                    }
                } else {
                    scope.launch {
                        pagerState.animateScrollToPage(2)
                    }
                }
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CenterAlignedTopAppBar(title = {
            Text(text = stringResource(id = R.string.app_name), fontWeight = FontWeight.Bold)
        }, navigationIcon = {
            AnimatedVisibility(
                visible = pagerState.currentPage == 1,
                enter = scaleIn(initialScale = 0.6f) + fadeIn(),
                exit = scaleOut(targetScale = 0.6f) + fadeOut()
            ) {
                IconButton(onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(0)
                    }
                }) {
                    Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = "back")
                }
            }
        })
        HorizontalPager(
            state = pagerState, userScrollEnabled = false, modifier = Modifier.weight(1f),
        ) {
            when (it) {
                0 -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                            .fillMaxSize()
                    ) {
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = stringResource(id = R.string.your_phone_number),
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Text(
                            text = stringResource(id = R.string.confirm_phone_number),
                            style = MaterialTheme.typography.labelMedium.copy(textAlign = TextAlign.Center),
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .fillMaxWidth(0.7f)
                                .alpha(0.7f)
                        )
                        Column(modifier = Modifier.padding(top = 24.dp)) {
                            AnimatedTextField(
                                text = phoneNumber,
                                error = phoneNumber.checkMobile().not() && phoneNumber.length == 11
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            CheckboxText(
                                checked = syncContacts,
                                onCheckedChange = { check ->
                                    syncContacts = check
                                    scope.launch {
                                        if (snackBarVisible) {
                                            snackBarVisible = false
                                            delay(300)
                                        }
                                        snackBarMessage =
                                            context.getString(if (check) R.string.contacts_message else R.string.contacts_message_not)
                                        snackBarVisible = true
                                    }
                                },
                                text = stringResource(
                                    id = R.string.sync_contacts,
                                ),
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        FloatingActionButton(
                            onClick = {
                                if (phoneNumber.checkMobile()) {
                                    viewModel.sendCode {
                                        scope.launch { pagerState.animateScrollToPage(1) }
                                    }
                                } else {
                                    scope.launch {
                                        if (snackBarVisible) {
                                            snackBarVisible = false
                                            delay(300)
                                        }
                                        snackBarMessage =
                                            context.getString(R.string.mobile_number_not_valid)
                                        snackBarVisible = true
                                    }
                                }
                            },
                            modifier = Modifier
                                .align(Alignment.End),
                            shape = CircleShape,
                            containerColor = MaterialTheme.colorScheme.primary,
                            elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp)
                        ) {
                            if (loading) {
                                CircularProgressIndicator(
                                    strokeCap = StrokeCap.Round,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Rounded.ArrowForward,
                                    contentDescription = "next"
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                1 -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                            .fillMaxSize()
                    ) {
                        Text(
                            text = stringResource(id = R.string.check_your_messages),
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Text(
                            text = stringResource(id = R.string.verify_messages_description).replace(
                                "(number)",
                                phoneNumber
                            ),
                            style = MaterialTheme.typography.labelMedium.copy(textAlign = TextAlign.Center),
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .fillMaxWidth(0.7f)
                                .alpha(0.7f)
                        )
                        OtpView(
                            code = verifyCode,
                            length = 6,
                            modifier = Modifier.padding(top = 24.dp),
                            correct = verifyCode == currentCode && verifyCode.length == 6
                        )
                    }
                }

                2 -> {
                    val imageProfile = viewModel.imageProfile
                    val firstName = viewModel.firstName
                    val lastName = viewModel.lastName
                    val id by viewModel.id.collectAsStateWithLifecycle()
                    val bio = viewModel.bio
                    val imagePicker =
                        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { image ->
                            if (image != null) {
                                viewModel.imageProfile = image
                            }
                        }
                    val idExist = viewModel.idExist
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 24.dp)
                            .padding(horizontal = 12.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(context).data(imageProfile).crossfade(true)
                                .placeholder(R.drawable.round_image_24)
                                .error(R.drawable.round_image_24)
                                .crossfade(500)
                                .build(),
                            contentDescription = null,
                            modifier = Modifier
                                .clip(
                                    CircleShape
                                )
                                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                                .size(108.dp)
                                .clickable {
                                    imagePicker.launch("image/*")
                                }
                                .padding(if (imageProfile == Uri.EMPTY) 30.dp else 0.dp),
                            colorFilter = if (imageProfile == Uri.EMPTY) ColorFilter.tint(
                                MaterialTheme.colorScheme.onBackground
                            ) else null,
                            contentScale = ContentScale.Crop,
                            filterQuality = FilterQuality.High
                        )
                        OutlinedTextField(
                            value = firstName,
                            onValueChange = { value -> viewModel.firstName = value },
                            modifier = Modifier
                                .padding(top = 12.dp)
                                .fillMaxWidth(),
                            label = {
                                Text(text = stringResource(id = R.string.first_name))
                            },
                            shape = MaterialTheme.shapes.medium
                        )
                        OutlinedTextField(
                            value = lastName,
                            onValueChange = { value -> viewModel.lastName = value },
                            modifier = Modifier
                                .padding(top = 12.dp)
                                .fillMaxWidth(),
                            label = {
                                Text(text = stringResource(id = R.string.last_name))
                            },
                            shape = MaterialTheme.shapes.medium
                        )
                        OutlinedTextField(
                            value = id,
                            onValueChange = { value -> viewModel.id.value = value },
                            modifier = Modifier
                                .padding(top = 12.dp)
                                .fillMaxWidth(),
                            label = {
                                Text(text = stringResource(id = R.string.id))
                            },
                            shape = MaterialTheme.shapes.medium,
                            isError = idExist
                        )
                        OutlinedTextField(
                            value = bio,
                            onValueChange = { value -> viewModel.bio = value },
                            modifier = Modifier
                                .padding(top = 12.dp)
                                .fillMaxWidth(),
                            label = {
                                Text(text = stringResource(id = R.string.bio))
                            },
                            shape = MaterialTheme.shapes.medium,
                            minLines = 3
                        )
                        Button(
                            onClick = {
                                viewModel.logIn {
                                    navigation.navigate(ChatPages.HomeScreen.route)
                                }
                            },
                            modifier = Modifier
                                .padding(top = 12.dp)
                                .height(OutlinedTextFieldDefaults.MinHeight)
                                .fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Text(text = stringResource(id = R.string.register))
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        if (loading) {
                            Dialog(onDismissRequest = {}) {
                                Surface(
                                    modifier = Modifier
                                        .background(MaterialTheme.colorScheme.background)
                                        .fillMaxWidth()
                                        .wrapContentHeight()
                                        .padding(vertical = 12.dp),
                                    shape = MaterialTheme.shapes.medium
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        CircularProgressIndicator(strokeCap = StrokeCap.Round)
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(text = stringResource(id = R.string.loading))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        SnackBar(text = snackBarMessage, visible = snackBarVisible, onVisibleChanged = {
            snackBarVisible = false
        })
        AnimatedVisibility(
            visible = pagerState.currentPage != 2,
            enter = slideInVertically { 300 } + fadeIn(),
            exit = slideOutVertically { 300 } + fadeOut(),
        ) {
            NumberKeyboard(modifier = Modifier
                .padding(horizontal = 12.dp)
                .padding(bottom = 12.dp),
                onNumberClicked = {
                    if (pagerState.currentPage == 0) {
                        if (phoneNumber.length <= 10) {
                            viewModel.phoneNumber += it
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        }
                    } else {
                        if (verifyCode.length != 6) {
                            viewModel.verifyCode += it
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        }
                    }
                }) {
                try {
                    if (pagerState.currentPage == 0) {
                        viewModel.phoneNumber =
                            phoneNumber.substring(0, phoneNumber.length.minus(1))
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    } else {
                        viewModel.verifyCode =
                            verifyCode.substring(0, verifyCode.length.minus(1))
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    }
                } catch (e: Exception) {
                }
            }
        }
    }
}