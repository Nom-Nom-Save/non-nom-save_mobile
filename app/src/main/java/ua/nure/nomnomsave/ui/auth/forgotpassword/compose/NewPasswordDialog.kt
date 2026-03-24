package ua.nure.nomnomsave.ui.auth.forgotpassword.compose

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import ua.nure.nomnomsave.R
import ua.nure.nomnomsave.ui.compose.NNSButton
import ua.nure.nomnomsave.ui.compose.NNSInputField
import ua.nure.nomnomsave.ui.theme.AppTheme

private val TAG by lazy { "NewPasswordDialog" }

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("ASSIGNED_VALUE_NEVER_READ")
@Composable
fun NewPasswordDialog(
    onDismiss: () -> Unit,
    onNewPassword: (String) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = AppTheme.color.background,
    ) {
        var password by remember { mutableStateOf("") }
        var confirmPassword by remember { mutableStateOf("") }
        var passwordError by remember { mutableStateOf<String?>(null) }

        val validateAndSetError = remember {
            {
                passwordError = when {
                    password.isEmpty() || confirmPassword.isEmpty() -> "Please fill in all fields"
                    password != confirmPassword -> "Passwords do not match"
                    else -> null
                }
            }
        }

        Text(
            modifier = Modifier
                .padding(horizontal = AppTheme.dimension.normal)
                .padding(top = AppTheme.dimension.small),
            text = stringResource(R.string.resetYourPassword),
            style = AppTheme.typography.large
        )
        Text(
            modifier = Modifier
                .padding(horizontal = AppTheme.dimension.normal)
                .padding(top = AppTheme.dimension.extraSmall),
            text = stringResource(R.string.resetPasswordSubtitle),
            style = AppTheme.typography.small.copy(color = AppTheme.color.grey)
        )
        NNSInputField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppTheme.dimension.normal)
                .padding(top = AppTheme.dimension.normal),
            label = stringResource(R.string.newPassword),
            value = password,
            isPassword = true,
            errorText = null,
        ) {
            password = it
            passwordError = null
        }
        NNSInputField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppTheme.dimension.normal)
                .padding(top = AppTheme.dimension.normal),
            label = stringResource(R.string.confirmNewPassword),
            value = confirmPassword,
            isPassword = true,
            errorText = passwordError,
        ) {
            confirmPassword = it
        }
        NNSButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = AppTheme.dimension.normal),
            text = stringResource(R.string.updatePassword)
        ) {
            validateAndSetError()
            if (passwordError == null) {
                Log.d(TAG, "NewPasswordDialog: match!")
                onNewPassword(password)
            } else {
                Log.d(TAG, "NewPasswordDialog: Error - ${passwordError}")
            }
        }
    }
}

@Preview
@Composable
private fun NewPasswordDialogPreview() {
    AppTheme {
        NewPasswordDialog(
            onDismiss = {},
            onNewPassword = {}
        )
    }
}