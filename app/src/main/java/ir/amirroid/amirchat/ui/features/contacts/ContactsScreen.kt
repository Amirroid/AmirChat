package ir.amirroid.amirchat.ui.features.contacts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.gson.Gson
import ir.amirroid.amirchat.R
import ir.amirroid.amirchat.utils.ChatPages
import ir.amirroid.amirchat.utils.getBasicAlphaColorsOfTextField
import ir.amirroid.amirchat.utils.getBasicColorsOfTextField
import ir.amirroid.amirchat.utils.getName
import ir.amirroid.amirchat.viewmodels.contact.ContactViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsScreen(
    navigation: NavController
) {
    val viewModel: ContactViewModel = hiltViewModel()
    val loading = viewModel.loading
    val contacts by viewModel.contacts.collectAsStateWithLifecycle()
    val searchText = viewModel.searchText
    val context = LocalContext.current
    var searchMode by remember {
        mutableStateOf(false)
    }
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
    ) {
        Box {
            CenterAlignedTopAppBar(
                title = { Text(text = stringResource(id = R.string.contacts)) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
                ),
                actions = {
                    IconButton(onClick = { searchMode = true }) {
                        Icon(imageVector = Icons.Rounded.Search, contentDescription = null)
                    }
                }
            )
            if (searchMode) {
                Surface(
                    modifier = Modifier
                        .statusBarsPadding()
                        .fillMaxWidth()
                        .height(64.dp),
                    color = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
                ) {
                    TextField(
                        value = searchText,
                        onValueChange = viewModel::search,
                        colors = getBasicColorsOfTextField(),
                        modifier = Modifier.fillMaxSize(),
                        trailingIcon = {
                            IconButton(onClick = {
                                searchMode = false
                                viewModel.search("")
                            }) {
                                Icon(imageVector = Icons.Rounded.Close, contentDescription = null)
                            }
                        }
                    )
                }
            }
        }
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            if (loading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillParentMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(strokeCap = StrokeCap.Round)
                    }
                }
            }
            items(contacts.size, key = { contacts[it].userId }) {
                val user = contacts[it]
                ListItem(
                    headlineContent = { Text(text = user.getName()) },
                    leadingContent = {
                        AsyncImage(
                            model = ImageRequest.Builder(context).data(user.profilePictureUrl)
                                .crossfade(true)
                                .placeholder(R.drawable.user_default)
                                .error(R.drawable.user_default)
                                .crossfade(300).build(),
                            contentDescription = null,
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    },
                    supportingContent = {
                        Text(text = user.userId)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            navigation.navigate(
                                ChatPages.ChatScreen.route + "?user=" + Gson().toJson(
                                    user
                                )
                            )
                        },
                )
            }
        }
    }
}