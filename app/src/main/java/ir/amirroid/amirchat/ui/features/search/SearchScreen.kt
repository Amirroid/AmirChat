package ir.amirroid.amirchat.ui.features.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Badge
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
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
import ir.amirroid.amirchat.data.models.register.CurrentUser
import ir.amirroid.amirchat.ui.components.UserListItem
import ir.amirroid.amirchat.utils.ChatPages
import ir.amirroid.amirchat.utils.getBasicColorsOfTextField
import ir.amirroid.amirchat.utils.getName
import ir.amirroid.amirchat.viewmodels.search.SearchViewModel

@Composable
fun SearchScreen(
    navigation: NavController
) {
    val viewModel: SearchViewModel = hiltViewModel()
    val loading = viewModel.loading
    val searchText = viewModel.text
    val users by viewModel.users.collectAsStateWithLifecycle()
    val focusRequester = remember {
        FocusRequester()
    }
    val context = LocalContext.current
    DisposableEffect(key1 = Unit) {
        focusRequester.requestFocus()
        onDispose { }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp))
                .statusBarsPadding()
        ) {

            TextField(
                value = searchText,
                onValueChange = { viewModel.text = it; viewModel.search() },
                colors = getBasicColorsOfTextField(),
                placeholder = {
                    Text(text = stringResource(id = R.string.search))
                },
                leadingIcon = {
                    IconButton(onClick = {
                        navigation.popBackStack()
                    }) {
                        Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = "back")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
            )
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
            items(users.size, key = { users[it].userId }) {
                val user = users[it]
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
                            navigation.navigate(ChatPages.ChatScreen.route + "?user=" + Gson().toJson(user))
                        },
                )
            }
        }
    }
}