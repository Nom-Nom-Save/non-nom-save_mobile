package ua.nure.nomnomsave.ui.profile

import android.content.res.Configuration
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import ua.nure.nomnomsave.R
import ua.nure.nomnomsave.ui.compose.ChangeAvatarDialog
import ua.nure.nomnomsave.ui.compose.NNSButton
import ua.nure.nomnomsave.ui.compose.NNSInputField
import ua.nure.nomnomsave.ui.compose.NNSScreen
import ua.nure.nomnomsave.ui.compose.NNSSwitch
import ua.nure.nomnomsave.ui.compose.NNSTitle
import ua.nure.nomnomsave.ui.theme.AppTheme

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    navController: NavController
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        viewModel.event.collect {
            when (it) {
                Profile.Event.OnBack -> navController.navigateUp()
                is Profile.Event.OnNavigate -> navController.navigate(route = it.route)
            }
        }
    }
    ProfileScreenContent(
        state = state,
        onAction = viewModel::onAction
    )
}


private val TAG by lazy { "ProfileScreen" }
@Composable
fun ProfileScreenContent(
    state: Profile.State,
    onAction: (Profile.Action) -> Unit
) {
    Log.d(TAG, "ProfileScreenContent: state: ${state.profile?.fullName}, ${state.profile?.email}")

    var headerVisibility by remember { mutableStateOf(true) }
    val columntState = rememberScrollState()

//    LaunchedEffect(key1 = listState) {
//        snapshotFlow {
//            listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 0
//        }.distinctUntilChanged()
//            .collect {
//                headerVisibility = !it
//            }
//    }

    LaunchedEffect(key1 = columntState) {
        snapshotFlow {
            columntState.value > 0
        }.distinctUntilChanged()
            .collect { isScrolling ->
                headerVisibility = !isScrolling
            }
    }

    NNSScreen {
        NNSTitle(
            title = stringResource(R.string.editProfile)
        )
        AnimatedVisibility(
            visible = headerVisibility
        ) {
            Box(
                modifier = Modifier.padding(all = AppTheme.dimension.normal),
                contentAlignment = Alignment.BottomEnd
            ) {
                AsyncImage(
                    modifier = Modifier
                        .size(130.dp)
                        .clip(shape = CircleShape)
                        .border(width = 1.dp, color = AppTheme.color.active, shape = CircleShape),
//                    model = state.profile?.avatarUrl,
                    model = "https://avatarfiles.alphacoders.com/374/thumb-1920-374883.png",
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
//                Icon(
//                    modifier = Modifier
//                        .size(36.dp)
//                        .clip(shape = CircleShape)
//                        .background(color = AppTheme.color.active)
//                        .padding(2.dp)
//                        .clickable {
//                            onAction(Profile.Action.OnShowChangeAvatarDialog)
//                        },
//                    painter = painterResource(R.drawable.edit_icon),
//                    tint = AppTheme.color.background,
//                    contentDescription = null
//                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1F)
                .verticalScroll(state = columntState)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppTheme.dimension.normal),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = stringResource(R.string.personalInformation),
                    style = AppTheme.typography.small.copy(
                        color = AppTheme.color.active
                    ),
                    modifier = Modifier
                        .padding(
                            top = AppTheme.dimension.normal,
                            bottom = AppTheme.dimension.small
                        )
                        .padding(start = AppTheme.dimension.normal)
                )
                NNSInputField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = AppTheme.dimension.normal,
                            vertical = AppTheme.dimension.small
                        ),
                    label = stringResource(R.string.name),
                    value = state.profile?.fullName ?: "",
                    errorText = state.nameError,
                    onValueChange = {
                        onAction(Profile.Action.OnNameChange(name = it))
                    }
                )
                NNSInputField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = AppTheme.dimension.normal,
                            vertical = AppTheme.dimension.small
                        ),
                    label = stringResource(R.string.email),
                    value = state.profile?.email ?: "",
                    errorText = state.emailError,
                    onValueChange = {
                        onAction(Profile.Action.OnEmailChange(email = it))
                    }
                )
            }


            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppTheme.dimension.normal),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = stringResource(R.string.notification),
                    style = AppTheme.typography.small.copy(
                        color = AppTheme.color.active
                    ),
                    modifier = Modifier
                        .padding(
                            top = AppTheme.dimension.normal,
                            bottom = AppTheme.dimension.small
                        )
                        .padding(start = AppTheme.dimension.normal)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = AppTheme.dimension.normal,
                            vertical = AppTheme.dimension.small
                        ),
                ) {
                    Column(
                        modifier = Modifier.weight(1F)
                    ) {
                        Text(
                            text = stringResource(R.string.nearbyDeals),
                            style = AppTheme.typography.regular
                        )
                        Text(
                            text = stringResource(R.string.nearbyDealsDetails),
                            style = AppTheme.typography.small.copy(
                                color = AppTheme.color.grey
                            )
                        )
                    }
                    NNSSwitch(
                        checked = state.nearbyDealsNotifications,
                        onCheckChange = {
                            onAction(Profile.Action.OnNearbyDealsChange)
                        }
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = AppTheme.dimension.normal,
                            vertical = AppTheme.dimension.small
                        ),) {
                    Column(
                        modifier = Modifier.weight(1F)
                    ) {
                        Text(
                            text = stringResource(R.string.closeTime),
                            style = AppTheme.typography.regular
                        )
                        Text(
                            text = stringResource(R.string.closeTimeDetails),
                            style = AppTheme.typography.small.copy(
                                color = AppTheme.color.grey
                            )
                        )
                    }
                    NNSSwitch(
                        checked = state.closedTimeNotifications,
                        onCheckChange = {
                            onAction(Profile.Action.OnCloseTimeChange)
                        }
                    )
                }

                NNSButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = AppTheme.dimension.normal,
                            vertical = AppTheme.dimension.large
                        ),
                    text = stringResource(R.string.saveChanges)
                ) {
                    onAction(Profile.Action.OnSave)
                }

                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                )
            }
        }

        if (state.showChangeAvatarDialog) {
            ChangeAvatarDialog(
                avatar = state.profile?.avatarUrl,
                onDismiss ={
                    onAction(Profile.Action.OnDismissChangeAvatarDialog)
                },
                onAvatarChange = {
                    onAction(Profile.Action.OnAvatarChange(avatarUrl = it))
                }
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun ProfileScreenContentPreview(modifier: Modifier = Modifier) {
    AppTheme() {
        Box(
            modifier = modifier.background(color = AppTheme.color.background)
        ) {
            ProfileScreenContent(
                state = Profile.State(),
                onAction = {}
            )
        }
    }
}

@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ProfileScreenContentDarkPreview(modifier: Modifier = Modifier) {
    AppTheme() {
        Box(
            modifier = modifier.background(color = AppTheme.color.background)
        ) {
            ProfileScreenContent(
                state = Profile.State(),
                onAction = {}
            )
        }
    }
}