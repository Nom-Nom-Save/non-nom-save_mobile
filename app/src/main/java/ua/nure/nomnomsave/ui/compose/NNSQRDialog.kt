package ua.nure.nomnomsave.ui.compose

import android.content.res.Configuration
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.graphics.createBitmap
import coil3.compose.AsyncImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ua.nure.nomnomsave.App
import ua.nure.nomnomsave.R
import ua.nure.nomnomsave.repository.resource.ResourceRepository
import ua.nure.nomnomsave.repository.resource.ResourceRepositoryImpl
import ua.nure.nomnomsave.ui.theme.AppTheme
import javax.inject.Inject

@Composable
fun NNSQRDialog(
    modifier: Modifier = Modifier,
    title: String,
    bitmap: Bitmap,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = modifier
                .clip(RoundedCornerShape(40.dp))
                .background(color = AppTheme.color.cardBackground, shape = RoundedCornerShape(40.dp)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
            ) {
            Image(
                modifier = Modifier
                    .padding(AppTheme.dimension.normal)
                    .size(200.dp)
                    .clip(shape = RoundedCornerShape(40.dp)),
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = AppTheme.dimension.normal),
                text = title,
                style = AppTheme.typography.regular.copy(
                    textAlign = TextAlign.Center
                )
            )

        }

    }
}

@Preview(showBackground = true)
@Composable
private fun NNSQRDialogPreview(modifier: Modifier = Modifier) {
    AppTheme {
        Box(
            modifier = Modifier.background(color = AppTheme.color.background)
        ) {
                NNSQRDialog(
                    modifier = modifier,
                    title = "Pastry Surprise Box",
                    bitmap = createBitmap(512, 512, Bitmap.Config.RGB_565),
                    onDismiss = {}
                )
        }
    }
}