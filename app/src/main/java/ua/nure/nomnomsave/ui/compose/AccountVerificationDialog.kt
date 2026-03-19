package ua.nure.nomnomsave.ui.compose

import android.content.res.Configuration
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
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.UiMode
import ua.nure.nomnomsave.R
import ua.nure.nomnomsave.ui.auth.register.Register
import ua.nure.nomnomsave.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountVerificationDialog(
    modifier: Modifier = Modifier,
    email: String,
    onVerify: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var code by remember() {
        mutableStateOf("")
    }
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = AppTheme.color.background,
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppTheme.dimension.normal),
            text = stringResource(R.string.codeVerification),
            style = AppTheme.typography.large.copy(
                textAlign = TextAlign.Center
            )
        )
        val message = buildAnnotatedString {
            withStyle(AppTheme.typography.regular.copy(
                color = AppTheme.color.grey
            ).toSpanStyle()) {
                append(stringResource(R.string.accountVerificationMessagePart1))
                append(" ")
            }
            withStyle(AppTheme.typography.regular.toSpanStyle()) {
                append(email)
                append(" ")
            }
            withStyle(AppTheme.typography.regular.copy(
                color = AppTheme.color.grey
            ).toSpanStyle()) {
                append(stringResource(R.string.accountVerificationMessagePart2))
                append(" ")
            }

        }
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppTheme.dimension.normal, vertical = AppTheme.dimension.normal),
            text = message,
        )
        NNSInputField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppTheme.dimension.normal)
                .padding(top = AppTheme.dimension.normal),
            label = stringResource(R.string.code),
            value = code
        ) {
            code = it
        }

        NNSButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppTheme.dimension.normal, vertical = AppTheme.dimension.normal),
            text = stringResource(R.string.confirmCode),
            enabled = code.isNotEmpty()
        ) {
            onVerify(code)
        }
    }
}

@Preview
@Composable
fun AccountVerificationDialogPreview(modifier: Modifier = Modifier) {
    AppTheme {
        AccountVerificationDialog(
            email = "test@gmail.com",
            onVerify = {},
            onDismiss = {}
        )
    }
}
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun AccountVerificationDialogDarkPreview(modifier: Modifier = Modifier) {
    AppTheme {
        AccountVerificationDialog(
            email = "test@gmail.com",
            onVerify = {},
            onDismiss = {}
        )
    }
}