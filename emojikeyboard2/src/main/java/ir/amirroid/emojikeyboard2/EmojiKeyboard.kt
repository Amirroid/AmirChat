package ir.amirroid.emojikeyboard2

import android.content.Context
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RichTooltipBox
import androidx.compose.material3.RichTooltipState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberRichTooltipState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun EmojiKeyboard(
    visible: Boolean,
    text: String,
    context: Context = LocalContext.current,
    placeHolder: String = "",
    keyboardSize: Float,
    onDelete: () -> Unit,
    onClick: (String) -> Unit
) {
    var search by remember {
        mutableStateOf("")
    }
    val emojiProvider = remember {
        EmojiProvider(context)
    }
    val groups = listOf(
        "smileys-emotion" to R.drawable.outline_emoji_emotions_24,
        "people-body" to R.drawable.round_emoji_people_24,
        "animals-nature" to R.drawable.ic_animal,
        "food-drink" to R.drawable.outline_fastfood_24,
        "travel-places" to R.drawable.outline_place_24,
        "activities" to R.drawable.round_event_note_24,
        "objects" to R.drawable.outline_emoji_objects_24,
        "symbols" to R.drawable.round_emoji_symbols_24,
        "flags" to R.drawable.round_outlined_flag_24
    )
    var deleteVisible by remember {
        mutableStateOf(true)
    }
    val nestedScroll = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (available.y < 0) {
                    deleteVisible = false
                } else if (available.y > 0) {
                    deleteVisible = true
                }
                return super.onPreScroll(available, source)
            }
        }
    }
    val pagerState = rememberPagerState {
        1 + groups.size
    }
    val scope = rememberCoroutineScope()
    AnimatedVisibility(
        visible = visible,
        enter = expandVertically(expandFrom = Alignment.Top),
        exit = shrinkVertically(shrinkTowards = Alignment.Top)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(with(LocalDensity.current) { keyboardSize.toDp() }),
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp))
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                            .height(48.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            IconButton(onClick = {
                                scope.launch { pagerState.animateScrollToPage(0) }
                            }) {
                                Text(
                                    text = "ALL",
                                    color = if (pagerState.currentPage == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        groups.forEachIndexed { index, group ->
                            Box(modifier = Modifier.weight(1f)) {
                                IconButton(onClick = {
                                    scope.launch { pagerState.animateScrollToPage(index.plus(1)) }
                                }) {
                                    Icon(
                                        painter = painterResource(id = group.second),
                                        contentDescription = group.first,
                                        modifier = Modifier.size(24.dp),
                                        tint = if (pagerState.currentPage == index.plus(1)) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                    HorizontalPager(state = pagerState) {
                        var emojiData by remember {
                            mutableStateOf(emptyList<EmojiData>())
                        }
                        var allEmoji by remember {
                            mutableStateOf(emptyList<EmojiData>())
                        }
                        DisposableEffect(key1 = Unit) {
                            emojiProvider.jsonToData(if (it==0) null else groups[it.minus(1)].first) {
                                emojiData = it
                                allEmoji = it
                            }
                            onDispose { emojiData = emptyList() }
                        }
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(8),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 4.dp)
                                .nestedScroll(nestedScroll)
                        ) {
                            item(span = { GridItemSpan(8) }) {
                                Column {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    SearchBarTextField(
                                        value = search, onValueChanged = {
                                            search = it
                                            if (it.isEmpty()) {
                                                emojiData = allEmoji
                                            } else {
                                                scope.launch(Dispatchers.IO) {
                                                    emojiData = allEmoji.filter { emoji ->
                                                        emoji.unicodeName.contains(
                                                            search, true
                                                        )
                                                    }
                                                }
                                            }
                                        }, placeholder = placeHolder
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                            items(emojiData.size, key = { emojiData[it].unicodeName }) {
                                val emoji = emojiData[it]
                                if (emoji.variants.isNullOrEmpty()) {
                                    val tooltipState = rememberRichTooltipState(isPersistent = false)
                                    RichTooltipBox(
                                        text = { Text(emoji.unicodeName) },
                                        tooltipState = tooltipState
                                    ) {
                                        EmojiView(emoji.character, onClick = {
                                            onClick.invoke(emoji.character)
                                            deleteVisible = true
                                        }) {
                                            scope.launch { tooltipState.show() }
                                        }
                                    }
                                } else {
                                    var showPopup by remember {
                                        mutableStateOf(false)
                                    }
                                    PopUpEmojiVariants(emoji, showPopup, onClick = { character ->
                                        onClick.invoke(character)
                                        deleteVisible = true
                                        showPopup = false
                                    }, onDismissRequest = { showPopup = false }) {
                                        EmojiView(emoji.character, onClick = {
                                            onClick.invoke(emoji.character)
                                            deleteVisible = true
                                        }) {
                                            showPopup = true
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                AnimatedVisibility(visible = deleteVisible,
                    enter = slideInVertically { 100 } + fadeIn(),
                    exit = slideOutVertically { 100 } + fadeOut()) {
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
                        modifier = Modifier
                            .padding(12.dp)
                            .size(36.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable(onClick = onDelete),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.outline_backspace_24),
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PopUpEmojiVariants(
    emoji: EmojiData,
    show: Boolean,
    onClick: (String) -> Unit,
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val matrix = context.resources.displayMetrics
    val width = matrix.widthPixels
    var offset by remember {
        mutableStateOf(Offset.Zero)
    }
    var size by remember {
        mutableStateOf(IntSize.Zero)
    }
    var variantsSize by remember {
        mutableStateOf(IntSize.Zero)
    }
    Box(modifier = Modifier.onGloballyPositioned {
        offset = it.positionInWindow()
        size = it.size
    }) {
        content.invoke()
    }
    LaunchedEffect(key1 = size, key2 = variantsSize, key3 = offset) {
        val calculatePopUpEndOffset = (offset.x.toInt() + size.width.div(2)) + variantsSize.width
        if (calculatePopUpEndOffset > width) {
            offset = offset.copy(width - variantsSize.width - 40f)
        }
    }
    var showPopup by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(key1 = show) {
        if (show.not()) delay(300)
        showPopup = show
    }
    Popup {
        if (showPopup) Box(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures {
                        onDismissRequest.invoke()
                    }
                })
            Box(modifier = Modifier.offset {
                IntOffset(
                    (offset.x.toInt() + size.width.div(2)) - variantsSize.width.div(
                        2
                    ), offset.y.toInt() - variantsSize.height - size.height.div(2) - 30
                )
            }) {
                AnimatedVisibility(
                    visible = show,
                    enter = scaleIn(
                        initialScale = 0.6f, animationSpec = tween(300)
                    ),
                    exit = scaleOut(
                        targetScale = 0.6f, animationSpec = tween(300)
                    ) + fadeOut(tween(300)),
                ) {
                    Surface(
                        modifier = Modifier
                            .height(64.dp)
                            .wrapContentWidth()
                            .widthIn(max = 164.dp)
                            .padding(vertical = 4.dp, horizontal = 8.dp)
                            .onSizeChanged { variantsSize = it },
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
                    ) {
                        Row(modifier = Modifier.wrapContentSize()) {
                            emoji.variants!!.forEach { variant ->
                                Box(modifier = Modifier.size(36.dp)) {
                                    EmojiView(emoji = variant.character,
                                        onClick = { onClick.invoke(variant.character) }) {

                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EmojiView(emoji: String, onClick: () -> Unit, onLongClick: () -> Unit) {
    val mis = remember {
        MutableInteractionSource()
    }
    val isPresses by mis.collectIsPressedAsState()
    val textSize by animateFloatAsState(
        targetValue = if (isPresses) 22f else 26f,
        label = "",
        animationSpec = if (isPresses) tween() else spring(
            dampingRatio = 0.3f, stiffness = Spring.StiffnessLow
        )
    )
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .combinedClickable(
                    interactionSource = mis,
                    indication = LocalIndication.current,
                    onClick = onClick,
                    onLongClick = onLongClick
                ), contentAlignment = Alignment.Center
        ) {
            Text(text = emoji, fontSize = textSize.sp)
        }
    }
}
