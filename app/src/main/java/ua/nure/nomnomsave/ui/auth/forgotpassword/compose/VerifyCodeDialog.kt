package ua.nure.nomnomsave.ui.auth.forgotpassword.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import ua.nure.nomnomsave.R
import ua.nure.nomnomsave.ui.compose.NNSButton
import ua.nure.nomnomsave.ui.compose.NNSInputField
import ua.nure.nomnomsave.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifyCodeDialog(
    email: String,
    codeError: String? = null,
    onResend: () -> Unit,
    onVerify: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = AppTheme.color.background,
    ) {
        var code by remember { mutableStateOf("") }

        Text(
            modifier = Modifier
                .padding(horizontal = AppTheme.dimension.normal)
                .padding(top = AppTheme.dimension.small),
            text = stringResource(R.string.enterVerificationCode),
            style = AppTheme.typography.large
        )
        Text(
            modifier = Modifier
                .padding(horizontal = AppTheme.dimension.normal)
                .padding(top = AppTheme.dimension.extraSmall),
            text = stringResource(R.string.verifyCodeSubtitle, email),
            style = AppTheme.typography.small.copy(color = AppTheme.color.grey)
        )
        NNSInputField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppTheme.dimension.normal)
                .padding(top = AppTheme.dimension.normal),
            label = stringResource(R.string.verificationCode),
            value = code,
            errorText = codeError,
        ) {
            code = it
        }
        NNSButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppTheme.dimension.normal)
                .padding(top = AppTheme.dimension.normal),
            text = stringResource(R.string.verify),
        ) {
            onVerify(code)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = AppTheme.dimension.normal,
                    vertical = AppTheme.dimension.normal
                ),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier.padding(end = AppTheme.dimension.small),
                text = stringResource(R.string.didntReceiveCode),
                style = AppTheme.typography.regular.copy(color = AppTheme.color.grey)
            )
            Text(
                modifier = Modifier.clickable { onResend() },
                text = stringResource(R.string.resend),
                style = AppTheme.typography.regular.copy(color = AppTheme.color.active)
            )
        }
    }
}

@Preview
@Composable
private fun VerifyCodeDialogPreview() {
    AppTheme {
        VerifyCodeDialog(
            email = "example@gmail.com",
            onResend = {},
            onVerify = {},
            onDismiss = {}
        )
    }
}