package ir.amirroid.amirchat.ui.features.settings

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.Coil
import coil.compose.AsyncImage
import coil.request.ImageRequest
import ir.amirroid.amirchat.MainActivity
import ir.amirroid.amirchat.R
import ir.amirroid.amirchat.utils.ChatPages
import ir.amirroid.amirchat.viewmodels.settings.SettingsViewModel

@SuppressLint("UnusedContentLambdaTargetStateParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navigation: NavController
) {
    val viewModel: SettingsViewModel = hiltViewModel()
    val editMode = viewModel.editMode
    val loading = viewModel.loading
    val firstName = viewModel.firstName
    val lastName = viewModel.lastName
    val id = viewModel.id
    val bio = viewModel.bio
    val idExist = viewModel.idExists
    val imageProfile = viewModel.image
    val imagePicker =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) {
            it?.let { image ->
                viewModel.image = image
            }
        }
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(
                rememberScrollState()
            )
    ) {
        CenterAlignedTopAppBar(
            title = { Text(text = stringResource(id = R.string.settings)) },
            navigationIcon = {
                IconButton(onClick = { navigation.popBackStack() }) {
                    Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = null)
                }
            }, actions = {
                AnimatedContent(
                    targetState = when {
                        loading -> 1
                        editMode -> 2
                        else -> 3
                    },
                    label = "",
                ) {
                    when (it) {
                        1 -> {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .padding(end = 4.dp)
                                    .size(30.dp),
                                strokeCap = StrokeCap.Round,
                                strokeWidth = 3.dp
                            )
                        }

                        2 -> {
                            IconButton(onClick = viewModel::cancelEditMode) {
                                Icon(
                                    imageVector = Icons.Rounded.Close,
                                    contentDescription = null
                                )
                            }
                        }

                        3 -> {
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                IconButton(onClick = {
                                    viewModel.logOut {
                                        val intent = Intent(context, MainActivity::class.java)
                                        intent.flags =
                                            Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                        context.startActivity(intent)
                                    }
                                }) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_log_out),
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                                IconButton(onClick = { viewModel.editMode = true }) {
                                    Icon(
                                        imageVector = Icons.Rounded.Edit,
                                        contentDescription = null
                                    )
                                }
                            }
                        }
                    }
                }
            })
        Column(
            Modifier
                .padding(horizontal = 12.dp)
                .padding(top = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .clip(
                        CircleShape
                    )
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                    .size(108.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context).data(imageProfile)
                        .crossfade(true)
                        .placeholder(R.drawable.user_default)
                        .error(R.drawable.user_default)
                        .crossfade(500)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(enabled = editMode) {
                            imagePicker.launch("image/*")
                        },
                    colorFilter = if (imageProfile?.path.isNullOrEmpty()) ColorFilter.tint(
                        MaterialTheme.colorScheme.onBackground
                    ) else null,
                    contentScale = ContentScale.Crop,
                )
            }
            OutlinedTextField(
                value = firstName,
                onValueChange = { value -> viewModel.firstName = value },
                modifier = Modifier
                    .padding(top = 12.dp)
                    .fillMaxWidth(),
                label = {
                    Text(text = stringResource(id = R.string.first_name))
                },
                shape = MaterialTheme.shapes.medium,
                enabled = editMode
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
                shape = MaterialTheme.shapes.medium,
                enabled = editMode
            )
            OutlinedTextField(
                value = id,
                onValueChange = { value ->
                    viewModel.id = value
                    viewModel.checkId()
                },
                modifier = Modifier
                    .padding(top = 12.dp)
                    .fillMaxWidth(),
                label = {
                    Text(text = stringResource(id = R.string.id))
                },
                shape = MaterialTheme.shapes.medium,
                isError = idExist,
                enabled = editMode
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
                minLines = 3,
                enabled = editMode
            )
            AnimatedVisibility(visible = editMode) {
                Button(
                    onClick = {
                        if (viewModel.validateFields()) {
                            if (loading) {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.wait_for_loading),
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                if (viewModel.user?.profilePictureUrl?.toUri() == imageProfile) {
                                    viewModel.editUser {
                                        navigation.navigate(ChatPages.HomeScreen.route) {
                                            val intent = Intent(context, MainActivity::class.java)
                                            intent.flags =
                                                Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                            context.startActivity(intent)
                                        }
                                    }
                                } else {
                                    viewModel.logIn {
                                        val intent = Intent(context, MainActivity::class.java)
                                        intent.flags =
                                            Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                        context.startActivity(intent)
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(
                                context,
                                context.getString(R.string.please_fill_all_field),
                                Toast.LENGTH_SHORT
                            ).show()
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
            }
        }
    }
}