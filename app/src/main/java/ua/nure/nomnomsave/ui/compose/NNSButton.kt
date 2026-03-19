package ua.nure.nomnomsave.ui.compose

import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ua.nure.nomnomsave.R
import ua.nure.nomnomsave.ui.theme.AppTheme

@Composable
fun NNSButton(
    modifier: Modifier = Modifier,
    textModifier: Modifier = Modifier,
    @DrawableRes icon: Int? = null,
    text: String,
    textColor: Color = AppTheme.color.background,
    enabled: Boolean = true,
    color: Color = AppTheme.color.active,
    disabledContainerColor: Color = AppTheme.color.grey,
    borderColor: Color? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .clip(shape = AppTheme.shape.buttonShape)
            .clickable(enabled = enabled, onClick = onClick,)
            .border(width = 1.dp, color = borderColor ?: if (enabled) color else disabledContainerColor, shape = AppTheme.shape.buttonShape)
            .background(color = if(enabled) color else disabledContainerColor, shape = AppTheme.shape.buttonShape)
            .padding(horizontal = AppTheme.dimension.normal, vertical = AppTheme.dimension.small),
        horizontalArrangement = Arrangement.Center
    ) {
        icon?.let {
            Image(
                modifier = Modifier
                    .padding(end = AppTheme.dimension.normal)
                    .size(24.dp),
                painter = painterResource(icon),
                contentDescription = null
            )
        }
        Text(
            modifier = textModifier,
            text = text,
            style = AppTheme.typography.regular.copy(
                color = textColor
            )
        )

    }
}

@Preview(showBackground = true)
@Composable
fun NNSButtonPreview(modifier: Modifier = Modifier) {
    AppTheme {
        Box(modifier = modifier.background(color = AppTheme.color.background)) {
            NNSButton(
                text = "Log in"
            ) {

            }
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun NNSButtonDarkPreview(modifier: Modifier = Modifier) {
    AppTheme {
        Box(modifier = modifier.background(color = AppTheme.color.background)) {
            NNSButton(
                icon = R.drawable.google,
                text = "Log in"
            ) {

            }
        }
    }
}
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun NNSButtonDarkBorderPreview(modifier: Modifier = Modifier) {
    AppTheme {
        Box(modifier = modifier.background(color = AppTheme.color.background)) {
            NNSButton(
                icon = R.drawable.google,
                text = "Log in",
                borderColor = AppTheme.color.grey
            ) {

            }
        }
    }
}