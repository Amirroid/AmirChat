package ir.amirroid.amirchat.ui.components

import android.annotation.SuppressLint
import android.content.Context
import android.media.ThumbnailUtils
import android.net.Uri
import android.provider.MediaStore
import android.widget.ListView
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.gson.Gson
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import ir.amirroid.amirchat.R
import ir.amirroid.amirchat.data.models.chat.FileMessage
import ir.amirroid.amirchat.data.models.media.ContactModel
import ir.amirroid.amirchat.data.models.media.FileModel
import ir.amirroid.amirchat.data.models.media.Location
import ir.amirroid.amirchat.data.models.media.MediaModel
import ir.amirroid.amirchat.data.models.media.MusicModel
import ir.amirroid.amirchat.utils.Constants
import ir.amirroid.amirchat.utils.bytesToHumanReadableSize
import ir.amirroid.amirchat.utils.formatTime
import ir.amirroid.amirchat.utils.getBasicColorsOfTextField
import ir.amirroid.amirchat.utils.getImage
import ir.amirroid.amirchat.utils.getNavigationBarHeight
import ir.amirroid.amirchat.utils.getStatusBarHeight
import ir.amirroid.amirchat.utils.getType
import ir.amirroid.amirchat.utils.getTypeForFile
import ir.amirroid.amirchat.utils.toDp
import ir.amirroid.amirchat.utils.toJsonMusic
import ir.amirroid.amirchat.utils.toMediaJson
import ir.amirroid.amirchat.viewmodels.ChatViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@OptIn(ExperimentalAnimationApi::class)
@SuppressLint("UnusedContentLambdaTargetStateParameter")
@Composable
fun FileSelectorBottomSheet(
    show: Boolean,
    viewModel: ChatViewModel,
    context: Context = LocalContext.current,
    onDismissRequest: () -> Unit,
    onSend: (String, List<FileMessage>) -> Unit
) {
    val scope = rememberCoroutineScope()
    val gallery = stringResource(id = R.string.gallery)
    val location = stringResource(id = R.string.location)
    val files = stringResource(id = R.string.file)
    val contacts = stringResource(id = R.string.contacts)
    val musics = stringResource(id = R.string.music)
    var currentType by remember {
        mutableStateOf(gallery)
    }
    val selectedItems = remember {
        mutableStateListOf<String>()
    }
    var caption by remember {
        mutableStateOf("")
    }
    val initialSize = remember {
        Animatable(0.7f)
    }
    LaunchedEffect(key1 = show) {
        if (show) {
            caption = ""
            currentType = gallery
            selectedItems.clear()
        }
    }
    LaunchedEffect(key1 = currentType) {
        selectedItems.clear()
        caption = ""
        initialSize.animateTo(0.65f, tween(200))
        initialSize.animateTo(0.7f, tween(200))
    }
    val sizeBadge = remember {
        Animatable(24f)
    }
    LaunchedEffect(key1 = selectedItems.count()) {
        sizeBadge.animateTo(26f, tween(80))
        sizeBadge.animateTo(24f, tween(80))
    }
    CustomModalBottomSheet(
        initialSize = initialSize.value,
        show = show,
        onDismissRequest = onDismissRequest,
        enabled = currentType != location,
        bottomBarContent = {
            AnimatedVisibility(
                visible = show && selectedItems.isNotEmpty(),
                enter = slideInVertically { 200 } + fadeIn(),
                exit = slideOutVertically { 200 } + fadeOut()) {
                Column {
                    Box(
                        contentAlignment = Alignment.BottomCenter, modifier = Modifier
                    ) {
                        TextField(value = caption,
                            onValueChange = { value -> caption = value },
                            colors = getBasicColorsOfTextField(),
                            shape = RectangleShape,
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateContentSize(),
                            placeholder = {
                                Text(text = stringResource(id = R.string.add_a_cation))
                            },
                            leadingIcon = {
                                IconButton(onClick = {}) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.outline_insert_emoticon_24),
                                        contentDescription = "emoji"
                                    )
                                }
                            })
                        Box(
                            modifier = Modifier
                                .padding(bottom = 20.dp, end = 12.dp)
                                .align(Alignment.BottomEnd),
                            contentAlignment = Alignment.BottomEnd
                        ) {
                            FloatingActionButton(
                                onClick = {
                                    val type = getTypeForFile(currentType, context)
                                    val selectedItemsFilter =
                                        selectedItems.map { path ->
                                            val data = when (type) {
                                                Constants.MUSIC -> {
                                                    val music =
                                                        viewModel.musics.value[viewModel.musics.value.indexOfFirst { data -> data.data == path }]
                                                    Gson().toJson(music.toJsonMusic())
                                                }

                                                Constants.FILE -> {
                                                    val file =
                                                        viewModel.files.value[viewModel.files.value.indexOfFirst { data -> data.data == path }]
                                                    Gson().toJson(file)
                                                }

                                                Constants.GALLERY -> {
                                                    val file =
                                                        viewModel.medias.value[viewModel.medias.value.indexOfFirst { data -> data.data == path }].toMediaJson()
                                                    Gson().toJson(file)
                                                }

                                                else -> ""
                                            }
                                            FileMessage(
                                                path,
                                                path,
                                                type,
                                                data = data
                                            )
                                        }
                                    onSend.invoke(
                                        caption,
                                        selectedItemsFilter
                                    )
                                    onDismissRequest.invoke()
                                    selectedItems.clear()
                                    caption = ""
                                },
                                shape = CircleShape,
                                containerColor = MaterialTheme.colorScheme.primary,
                                elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.round_send_24),
                                    contentDescription = "send",
                                )
                            }
                            Box(
                                modifier = Modifier.size(26.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary)
                                        .widthIn(sizeBadge.value.dp)
                                        .height(sizeBadge.value.dp)
                                        .border(
                                            2.dp,
                                            MaterialTheme.colorScheme.background,
                                            CircleShape
                                        ), contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = selectedItems.size.toString(),
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
                            )
                            .fillMaxWidth()
                            .height(with(LocalDensity.current) {
                                getNavigationBarHeight(
                                    context = context
                                ).toDp()
                            })
                    )
                }
            }
            AnimatedVisibility(visible = it && selectedItems.isEmpty(),
                enter = slideInVertically { 200 } + fadeIn(),
                exit = slideOutVertically { 200 } + fadeOut()) {
                Row(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background)
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .navigationBarsPadding()
                        .padding(vertical = 8.dp)
                        .horizontalScroll(
                            rememberScrollState()
                        )
                        .pointerInput(Unit) { detectTapGestures() },
                ) {
                    Spacer(modifier = Modifier.width(12.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Constants.typesForSendData.forEach { type ->
                        val text = stringResource(id = type.name)
                        TypeItemButton(
                            selected = text == currentType,
                            color = type.color,
                            image = type.image,
                            text = text
                        ) {
                            currentType = text
                        }
                        Spacer(modifier = Modifier.width(24.dp))
                    }
                }
            }
        },
        title = currentType,
        context = context,
        scope = scope,
        showToolbar = currentType == location || currentType == gallery || currentType == files
    ) {
        AnimatedContent(targetState = currentType, transitionSpec = {
            fadeIn(tween(500)) with fadeOut(tween(500))
        }, label = "") {
            when (it) {
                gallery -> {
                    GalleyView(context, viewModel.medias, selectedItems)
                }

                location -> {
                    LocationView(context) { location ->
                        onSend.invoke("", listOf(location))
                        onDismissRequest.invoke()
                    }
                }

                files -> {
                    viewModel.getFiles()
                    FileView(context, viewModel.files, selectedItems)
                }

                contacts -> {
                    viewModel.getContacts()
                    ContactsView(viewModel.contacts) { contact ->
                        onSend.invoke(
                            "",
                            listOf(
                                FileMessage(
                                    data = Gson().toJson(contact),
                                    type = Constants.CONTACT
                                )
                            )
                        )
                        selectedItems.clear()
                        onDismissRequest.invoke()
                    }
                }

                musics -> {
                    viewModel.getMusics()
                    MusicsView(
                        viewModel.musics, viewModel.currentMusic, selectedItems
                    ) { uri, play ->
                        viewModel.playOrPauseMusic(play, uri)
                    }
                }

            }
        }
    }
}

@Composable
fun MusicsView(
    musics: StateFlow<List<MusicModel>>,
    currentPlayingMusic: StateFlow<Uri?>,
    selectedItems: SnapshotStateList<String>,
    onPlayRequest: (Uri, Boolean) -> Unit
) {
    val musicsState = remember {
        mutableStateListOf<MusicModel>()
    }

    val currentPlayingMusicState by currentPlayingMusic.collectAsStateWithLifecycle()
    var searchText by remember {
        mutableStateOf("")
    }
    val scope = rememberCoroutineScope()
    DisposableEffect(key1 = Unit) {
        onDispose {
            onPlayRequest.invoke(Uri.EMPTY, false)
        }
    }
    LaunchedEffect(key1 = Unit) {
        musics.collectLatest {
            musicsState.clear()
            musicsState.addAll(it)
        }
    }
    Column {
        SearchBarTextField(
            value = searchText,
            onValueChanged = {
                searchText = it
                if (searchText.isEmpty()) {
                    musicsState.clear()
                    musicsState.addAll(musics.value)
                } else {
                    scope.launch(Dispatchers.IO) {
                        musicsState.clear()
                        musicsState.addAll(musics.value.filter { musicModel ->
                            musicModel.name.contains(
                                searchText, true
                            )
                        })
                    }
                }
            },
            placeholder = stringResource(
                id = R.string.search, " ", stringResource(id = R.string.music)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .padding(vertical = 12.dp)
        )
        LazyColumnWithHelperButton(count = musicsState.count()) {
            if (musicsState.isEmpty() && searchText.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(strokeCap = StrokeCap.Round)
                    }
                }
            }
            items(musicsState.size, key = { musicsState[it].id }) {
                val music = musicsState[it]
                Column {
                    MusicView(
                        music = music,
                        currentPlayingMusicState,
                        onPlayRequest,
                        selectedItems.contains(music.data)
                    ) { select ->
                        if (select) {
                            selectedItems.add(music.data)
                        } else {
                            selectedItems.remove(music.data)
                        }
                    }
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .align(Alignment.End)
                    )
                }
            }
        }
    }
}

@Composable
fun MusicView(
    music: MusicModel,
    currentPlayingMusic: Uri?,
    onPlayRequest: (Uri, Boolean) -> Unit,
    selected: Boolean,
    onSelect: (Boolean) -> Unit
) {
    ListItem(headlineContent = { Text(text = music.name, maxLines = 1) }, supportingContent = {
        Text(text = music.artistName, maxLines = 1)
    }, leadingContent = {
        Box(contentAlignment = Alignment.BottomEnd) {
            PlayButton(play = currentPlayingMusic == music.uri, onPlayRequest = {
                onPlayRequest.invoke(music.uri, it)
            })
            SelectionBox(checked = selected)
        }
    }, modifier = Modifier.toggleable(selected) {
        onSelect.invoke(it)
    })
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsView(contacts: StateFlow<List<ContactModel>>, onSend: (ContactModel) -> Unit) {
    val contactsState = remember {
        mutableStateListOf<ContactModel>()
    }
    var searchText by remember {
        mutableStateOf("")
    }
    var sheetContact by remember {
        mutableStateOf(false)
    }
    var selectedContact by remember {
        mutableStateOf<ContactModel?>(null)
    }
    val scope = rememberCoroutineScope()
    LaunchedEffect(key1 = Unit) {
        contacts.collectLatest {
            contactsState.clear()
            contactsState.addAll(it)
        }
    }
    Column {
        SearchBarTextField(
            value = searchText,
            onValueChanged = {
                searchText = it
                if (searchText.isEmpty()) {
                    contactsState.clear()
                    contactsState.addAll(contacts.value)
                } else {
                    scope.launch(Dispatchers.IO) {
                        contactsState.clear()
                        contactsState.addAll(contacts.value.filter { musicModel ->
                            musicModel.name.contains(
                                searchText, true
                            )
                        })
                    }
                }
            },
            placeholder = stringResource(
                id = R.string.search, " ", stringResource(id = R.string.music)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .padding(vertical = 12.dp)
        )
        LazyColumnWithHelperButton(
            modifier = Modifier.fillMaxSize(), count = contactsState.count()
        ) {
            if (contactsState.isEmpty() && searchText.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(strokeCap = StrokeCap.Round)
                    }
                }
            }
            items(contactsState.size, key = { contactsState[it].id }) {
                val contact = contactsState[it]
                val brush = Constants.randomBrush[it % 3]
                Column {
                    ContactView(contact = contact, brush) {
                        selectedContact = contact
                        sheetContact = true
                    }
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .align(Alignment.End)
                    )
                }
            }
        }
        if (sheetContact) {
            val selectedNumbers = remember {
                mutableStateListOf<String>()
            }
            LaunchedEffect(key1 = selectedContact) {
                selectedNumbers.clear()
                selectedNumbers.addAll(selectedContact?.numbers ?: emptyList())
            }
            ModalBottomSheet(
                onDismissRequest = { sheetContact = false },
                windowInsets = WindowInsets(0),
                dragHandle = null
            ) {
                Column(
                    modifier = Modifier
                        .clip(
                            MaterialTheme.shapes.small.copy(
                                bottomStart = CornerSize(0.dp),
                                bottomEnd = CornerSize(0.dp)
                            )
                        )
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    NameView(
                        name = selectedContact!!.name,
                        circleShape = true,
                        brush = Constants.randomBrush.first(),
                        modifier = Modifier.size(84.dp)
                    )
                    Text(
                        text = selectedContact!!.name,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(top = 12.dp)
                    )
                    if (selectedContact!!.numbers.size == 1) {
                        Text(
                            text = selectedContact!!.numbers.first(),
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier
                                .alpha(0.6f)
                                .padding(top = 8.dp)
                        )
                    } else {
                        Column(modifier = Modifier.padding(top = 8.dp)) {
                            selectedContact!!.numbers.forEach { number ->
                                ListItem(
                                    headlineContent = { Text(text = number) },
                                    trailingContent = {
                                        Switch(
                                            checked = selectedNumbers.contains(number),
                                            onCheckedChange = null
                                        )
                                    },
                                    modifier = Modifier.toggleable(selectedNumbers.contains(number)) {
                                        if (it) {
                                            selectedNumbers.add(number)
                                        } else {
                                            selectedNumbers.remove(number)
                                        }
                                    },
                                )
                            }
                        }
                    }
                    Button(
                        onClick = {
                            onSend.invoke(selectedContact!!.copy(numbers = selectedNumbers))
                            sheetContact = false
                        }, modifier = Modifier
                            .padding(top = 12.dp)
                            .padding(horizontal = 12.dp)
                            .fillMaxWidth()
                    ) {
                        Text(text = stringResource(id = R.string.share_contact))
                    }
                }
            }
        }
    }
}

@Composable
fun ContactView(contact: ContactModel, brush: Brush, onSend: () -> Unit) {
    ListItem(headlineContent = {
        Text(
            text = contact.name, maxLines = 1, overflow = TextOverflow.Ellipsis
        )
    }, supportingContent = {
        Text(text = contact.numbers.first())
    }, modifier = Modifier.clickable { onSend.invoke() }, leadingContent = {
        NameView(name = contact.name, circleShape = true, brush = brush)
    })
}

@Composable
fun FileView(
    context: Context, files: StateFlow<List<FileModel>>, selectedItems: SnapshotStateList<String>
) {
    val filesState by files.collectAsStateWithLifecycle()
    LazyColumnWithHelperButton(modifier = Modifier.fillMaxSize(), count = filesState.count()) {
        if (filesState.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(strokeCap = StrokeCap.Round)
                }
            }
        }
        items(filesState.size, key = { filesState[it].id }) {
            val file = filesState[it]
            FileView(
                file = file,
                context = context,
                selected = selectedItems.contains(file.data),
            ) { select ->
                if (select) {
                    selectedItems.add(file.data)
                } else {
                    selectedItems.remove(file.data)
                }
            }
        }
    }
}


@Composable
fun FileView(file: FileModel, selected: Boolean, context: Context, onSelect: (Boolean) -> Unit) {
    ListItem(headlineContent = {
        Text(
            text = file.name, maxLines = 1, overflow = TextOverflow.Ellipsis
        )
    }, supportingContent = {
        Text(text = file.size.bytesToHumanReadableSize())
    }, leadingContent = {
        Box(contentAlignment = Alignment.BottomStart) {
            AsyncImage(
                model = file.getImage(),
                contentDescription = null,
                modifier = Modifier
                    .size(44.dp)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = if (file.mimeType.startsWith("image")) ContentScale.Crop else ContentScale.FillBounds
            )
            SelectionBox(checked = selected)
        }
    }, modifier = Modifier.toggleable(selected) {
        if (file.size > 1024 * 1024 * 100) {
            Toast.makeText(context, context.getString(R.string.file_big), Toast.LENGTH_SHORT).show()
        } else {
            onSelect.invoke(selected.not())
        }
    })
}

@Composable
fun LocationView(context: Context, onSend: (FileMessage) -> Unit) {
    val camera = rememberCameraPositionState()
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .padding(top = 12.dp)
                .padding(horizontal = 12.dp)
                .fillMaxWidth()
                .height(200.dp)
                .clip(MaterialTheme.shapes.small),
            contentAlignment = Alignment.Center
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                properties = MapProperties(
                    isMyLocationEnabled = true,
                    mapStyleOptions = if (isSystemInDarkTheme()) MapStyleOptions.loadRawResourceStyle(
                        context, R.raw.dark_map
                    ) else null
                ),
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = false,
                    myLocationButtonEnabled = true
                ),
                cameraPositionState = camera
            ) {

            }
            Icon(
                painter = painterResource(id = R.drawable.baseline_location_on_24),
                contentDescription = null,
                tint = Color.Red,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }
        ListItem(headlineContent = { Text(text = stringResource(id = R.string.send_location)) },
            modifier = Modifier
                .padding(top = 12.dp)
                .clickable {
                    onSend.invoke(
                        FileMessage(
                            type = Constants.LOCATION,
                            data = Gson().toJson(
                                Location(
                                    camera.position.zoom,
                                    camera.position.target.latitude,
                                    camera.position.target.longitude,
                                )
                            )
                        )
                    )
                },
            leadingContent = {
                IconButton(onClick = {}) {
                    Icon(imageVector = Icons.Rounded.LocationOn, contentDescription = null)
                }
            })
    }
}


@Composable
fun TypeItemButton(
    selected: Boolean, color: Color, image: Int, text: String, onClick: () -> Unit
) {
    val textColor by animateColorAsState(
        targetValue = if (selected) color else MaterialTheme.colorScheme.onBackground.copy(
            0.8f
        ), label = ""
    )
    val borderSize by animateDpAsState(targetValue = if (selected) 2.dp else 0.dp, label = "")
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier
            .clip(CircleShape)
            .size(56.dp)
            .then(
                if (selected) Modifier.border(
                    borderSize, color, shape = CircleShape
                ) else Modifier
            )
            .pointerInput(Unit) {
                detectTapGestures { onClick.invoke() }
            }) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp)
                    .clip(CircleShape)
                    .background(color),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = image),
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
        Text(text = text, color = textColor, fontSize = 12.sp)
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GalleyView(
    context: Context, medias: StateFlow<List<MediaModel>>, selectedItems: SnapshotStateList<String>
) {
    val mediaState by medias.collectAsStateWithLifecycle(initialValue = emptyList())
    var showPopUpMedia by remember {
        mutableStateOf(false)
    }
    var size by remember {
        mutableStateOf(Size.Zero)
    }
    var offset by remember {
        mutableStateOf(Offset.Zero)
    }
    var indexMediaShowed by remember {
        mutableStateOf(1)
    }
    val pagerState = rememberPagerState {
        mediaState.count()
    }
    val scope = rememberCoroutineScope()
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .padding(horizontal = 8.dp)
    ) {
        items(mediaState.size, key = { mediaState[it].id }) { index ->
            val media = mediaState[index]
            val selected = selectedItems.contains(media.data)
            MediaView(context,
                media = media,
                selected = selected,
                indexSelect = if (selected) selectedItems.indexOf(media.data).plus(1) else 0,
                onClick = { offsetClick, sizeClick ->
                    offset = offsetClick
                    size = sizeClick
                    indexMediaShowed = index
                    showPopUpMedia = true
                    scope.launch { pagerState.scrollToPage(index) }
                }) { select ->
                if (select) {
                    selectedItems.add(media.data)
                } else {
                    selectedItems.remove(media.data)
                }
            }
        }
    }
    val media = mediaState.getOrNull(pagerState.currentPage)
    val selected = selectedItems.contains(media?.data)
    MediaPopUpWithAnimation(show = showPopUpMedia,
        size = size,
        offset = offset,
        mediaList = mediaState,
        pagerState = pagerState,
        selected,
        selectedItems.count(),
        onSelect = { select ->
            if (select) {
                selectedItems.add(media!!.data)
            } else {
                selectedItems.remove(media!!.data)
            }
        }) {
        scope.launch { pagerState.scrollToPage(indexMediaShowed) }
        showPopUpMedia = false
    }
}

@Composable
fun MediaView(
    context: Context, media: MediaModel, selected: Boolean, indexSelect: Int, onClick: (
        offset: Offset, size: Size
    ) -> Unit, onSelect: (Boolean) -> Unit
) {
    var image by remember {
        mutableStateOf<Any?>(null)
    }
    var size by remember {
        mutableStateOf(Size.Zero)
    }
    var offset by remember {
        mutableStateOf(Offset.Zero)
    }
    val type = media.getType()
    val padding by animateDpAsState(targetValue = if (selected) 10.dp else 2.dp, label = "")
    LaunchedEffect(key1 = Unit) {
        if (image == null) {
            if (type.startsWith("video", true)) {
                withContext(Dispatchers.Default) {
                    val bitmap = ThumbnailUtils.createVideoThumbnail(
                        media.data, MediaStore.Images.Thumbnails.MINI_KIND
                    )
                    image = bitmap
                }
            } else {
                image = media.uri
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {
        AsyncImage(model = ImageRequest.Builder(context).crossfade(200).crossfade(true).data(image)
            .build(),
            contentDescription = "Image",
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .onGloballyPositioned {
                    offset = it
                        .positionInRoot()
                    size = it.size.toSize()
                }
                .clickable {
                    onClick.invoke(offset, size)
                },
            contentScale = ContentScale.Crop
        )
        if (type.startsWith("video", true)) {
            Row(
                modifier = Modifier
                    .padding(4.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .align(Alignment.BottomStart)
                    .background(Color.Black.copy(0.6f))
                    .wrapContentSize(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.round_play_arrow_24),
                    contentDescription = "play",
                    modifier = Modifier.size(14.dp),
                    tint = Color.White
                )
                Text(
                    text = media.duration.formatTime(),
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 1.dp, end = 4.dp)
                )
            }
        }
        SelectionNumberButton(
            checked = selected,
            modifier = Modifier
                .padding(4.dp)
                .align(Alignment.TopEnd),
            onCheckedChange = onSelect,
            text = indexSelect.toString()
        )
    }
}