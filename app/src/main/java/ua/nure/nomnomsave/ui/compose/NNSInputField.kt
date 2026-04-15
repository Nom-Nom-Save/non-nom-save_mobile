package ua.nure.nomnomsave.ui.compose

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import ua.nure.nomnomsave.R
import ua.nure.nomnomsave.ui.theme.AppTheme

@Composable
fun NNSInputField(
    modifier: Modifier = Modifier,
    label: String? = null,
    value: String? = null,
    isPassword: Boolean = false,
    errorText: String? = null,
    readOnly: Boolean = false,
    trailingIcon: @Composable (() -> Unit)? = null,
    onValueChange: (String) -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    val isError = errorText != null

    val errorSupportingText: (@Composable () -> Unit)? = if (isError) {
        {
            Text(
                text = errorText ?: "",
                style = AppTheme.typography.small.copy(
                    color = AppTheme.color.error
                )
            )
        }
    } else null

    if (isPassword) {
        OutlinedTextField(
            modifier = modifier,
            textStyle = AppTheme.typography.regular,
            value = value ?: "",
            onValueChange = onValueChange,
            isError = isError,
            supportingText = errorSupportingText,
            readOnly = readOnly,

            label = {
                Text(
                    text = label ?: "",
                    style = AppTheme.typography.regular.copy(
                        color = AppTheme.color.grey
                    )
                )
            },
            visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                Icon(
                    modifier = Modifier
                        .clip(shape = CircleShape)
                        .clickable {
                            isVisible = !isVisible
                        },
                    painter = painterResource(if (isVisible) R.drawable.show else R.drawable.hide),
                    contentDescription = null
                )
            },
        )
    }
    else {
        OutlinedTextField(
            modifier = modifier,
            textStyle = AppTheme.typography.regular,
            value = value ?: "",
            onValueChange = onValueChange,
            isError = isError,
            supportingText = errorSupportingText,
            readOnly = readOnly,

            label = {
                Text(
                    text = label ?: "",
                    style = AppTheme.typography.regular.copy(
                        color = AppTheme.color.grey
                    )
                )
            },
            trailingIcon = trailingIcon
        )
    }
}

@Preview (showBackground = true)
@Composable
fun BonteInputFieldPreview(modifier: Modifier = Modifier) {
    AppTheme() {
        Box(modifier = modifier.background(color = AppTheme.color.background)) {
            NNSInputField(
                label = "input password",
                value = "secret1",
                onValueChange = {},
                isPassword = true,
                errorText = "Passwords do not match"
            )
        }
    }
}

@Preview (showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun BonteInputFieldDarkPreview(modifier: Modifier = Modifier) {
    AppTheme() {
        Box(modifier = modifier.background(color = AppTheme.color.background)) {
            NNSInputField(
                label = "input password",
                value = "secret1",
                onValueChange = {},
                isPassword = true,
                errorText = "Passwords do not match"
            )
        }
    }
}