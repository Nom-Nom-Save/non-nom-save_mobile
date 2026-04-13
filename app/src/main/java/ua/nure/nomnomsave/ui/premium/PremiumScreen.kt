package ua.nure.nomnomsave.ui.premium

import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import ua.nure.nomnomsave.ui.compose.NNSScreen

@Composable
fun PremiumScreen(
    viewModel: PremiumViewModel,
    navController: NavController
) {
    NNSScreen() {
        var isLoading by remember { mutableStateOf(true) }
        var hasError by remember { mutableStateOf(false) }

        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true

                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView, url: String) {
                            isLoading = false
                        }

                        override fun onReceivedError(
                            view: WebView,
                            request: WebResourceRequest,
                            error: WebResourceError
                        ) {
                            hasError = true
                            isLoading = false
                        }
                    }
                    loadUrl("https://non-nom-save-client-wnnb.vercel.app/subscriptions/plans")
                }
            }
        )
    }
}