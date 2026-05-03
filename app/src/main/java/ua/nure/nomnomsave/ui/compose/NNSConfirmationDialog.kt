package ua.nure.nomnomsave.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import ua.nure.nomnomsave.ui.theme.AppTheme

@Composable
fun NNSConfirmationDialog(
    title: String,
    message: String,
    confirmButtonText: String = "Delete",
    dismissButtonText: String = "Cancel",
    isDestructive: Boolean = true,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .background(
                    color = AppTheme.color.cardBackground,
                    shape = RoundedCornerShape(AppTheme.dimension.normal)
                )
                .padding(AppTheme.dimension.normal)
        ) {
            Text(
                text = title,
                style = AppTheme.typography.large.copy(fontWeight = FontWeight.SemiBold),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(AppTheme.dimension.small))

            Text(
                text = message,
                style = AppTheme.typography.regular.copy(color = AppTheme.color.grey),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(AppTheme.dimension.large))

            if (dismissButtonText.isEmpty()) {
                NNSButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    text = confirmButtonText,
                    color = AppTheme.color.active,
                    textColor = AppTheme.color.background,
                    onClick = onConfirm
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    NNSButton(
                        modifier = Modifier
                            .weight(1f)
                            .height(37.dp),
                        text = dismissButtonText,
                        color = AppTheme.color.background,
                        textColor = AppTheme.color.foreground,
                        borderColor = AppTheme.color.grey,
                        onClick = onDismiss
                    )

                    Spacer(modifier = Modifier.width(AppTheme.dimension.normal))

                    NNSButton(
                        modifier = Modifier
                            .weight(1f)
                            .height(37.dp),
                        text = confirmButtonText,
                        color = if (isDestructive) AppTheme.color.error else AppTheme.color.active,
                        textColor = AppTheme.color.background,
                        onClick = onConfirm
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NNSConfirmationDialogPreview() {
    AppTheme {
        NNSConfirmationDialog(
            title = "Delete Order?",
            message = "Are you sure you want to delete this order from card?",
            confirmButtonText = "Delete",
            dismissButtonText = "Cancel",
            onConfirm = {},
            onDismiss = {}
        )
    }
}

