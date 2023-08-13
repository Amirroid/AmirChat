package ir.amirroid.amirchat.ui.features.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ir.amirroid.amirchat.R
import ir.amirroid.amirchat.utils.getBasicColorsOfTextField

@Composable
fun SearchScreen(
    navigation: NavController
) {
    var searchText by remember {
        mutableStateOf("")
    }
    val focusRequester = remember {
        FocusRequester()
    }
    DisposableEffect(key1 = Unit){
        focusRequester.requestFocus()
        onDispose {  }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp))
                .statusBarsPadding()
        ) {

            TextField(
                value = searchText,
                onValueChange = { searchText = it },
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
                modifier = Modifier.fillMaxWidth().focusRequester(focusRequester)
            )
        }
    }
}