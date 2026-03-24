package ua.nure.nomnomsave.ui.compose

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import ua.nure.nomnomsave.App
import ua.nure.nomnomsave.R
import ua.nure.nomnomsave.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeAvatarDialog(
    modifier: Modifier = Modifier,
    avatar: String? = null,
    onAvatarChange: (String) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        var avatarLink by remember { mutableStateOf(avatar ?: "") }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.changeProfilePhoto),
                style = AppTheme.typography.large
            )

            NNSInputField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = AppTheme.dimension.normal,
                        vertical = AppTheme.dimension.normal
                    ),
                label = stringResource(R.string.newAvatar),
                value = avatarLink
            ) {
                avatarLink = it
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = AppTheme.dimension.normal,
                        vertical = AppTheme.dimension.normal
                    )
            ) {
                NNSButton(
                    modifier = Modifier
                        .weight(1F)
                        .padding(AppTheme.dimension.small),
                    text = stringResource(R.string.saveChanges)
                ) {
                    onAvatarChange(avatarLink)
                }
            }
        }
    }
}

@Preview
@Composable
fun ChangeAvatarDialogPreview(modifier: Modifier = Modifier) {
    AppTheme {
        ChangeAvatarDialog(
            modifier = modifier,
            avatar = "",
            onAvatarChange = {},
            onDismiss = {}
        )
    }
}