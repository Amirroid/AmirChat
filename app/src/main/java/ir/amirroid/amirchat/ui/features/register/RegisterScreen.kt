package ir.amirroid.amirchat.ui.features.register

import android.annotation.SuppressLint
import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import ir.amirroid.amirchat.R
import ir.amirroid.amirchat.ui.components.AnimatedTextField
import ir.amirroid.amirchat.ui.components.CheckboxText
import ir.amirroid.amirchat.ui.components.NumberKeyboard
import ir.amirroid.amirchat.ui.components.OtpView
import ir.amirroid.amirchat.ui.components.SnackBar
import ir.amirroid.amirchat.ui.components.TextAnimation
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
    var snackBarMessage by remember {
        mutableStateOf("")
    }
    var snackBarVisible by remember {
        mutableStateOf(false)
    }
    val focusRequester = FocusRequester()
    val hapticFeedback = LocalHapticFeedback.current
    val pagerState = rememberPagerState {
        2
    }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    LaunchedEffect(key1 = Unit) {
        try {
            focusRequester.requestFocus()
        } catch (_: Exception) {
        }
    }
    LaunchedEffect(key1 = verifyCode) {
        if (verifyCode.length == 6) {
            navigation.navigate(ChatPages.HomeScreen.route)
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
            if (it == 0) {
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
                            scope.launch {
                                if (phoneNumber.checkMobile()) {
                                    pagerState.animateScrollToPage(2)
                                } else {
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
                        Icon(
                            imageVector = Icons.Rounded.ArrowForward,
                            contentDescription = "`next`"
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            } else {
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
                    OtpView(code = verifyCode, length = 6, modifier = Modifier.padding(top = 24.dp))
                }
            }
        }
        SnackBar(text = snackBarMessage, visible = snackBarVisible, onVisibleChanged = {
            snackBarVisible = false
        })
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
                    viewModel.phoneNumber = phoneNumber.substring(0, phoneNumber.length.minus(1))
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                } else {
                    viewModel.verifyCode = verifyCode.substring(0, verifyCode.length.minus(1))
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                }
            } catch (e: Exception) {
            }
        }
    }
}