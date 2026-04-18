package ua.nure.nomnomsave.ui.premium

import android.util.Log
import android.webkit.CookieManager
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import ua.nure.nomnomsave.ui.compose.NNSScreen

private val TAG by lazy { "PremiumScreen" }
@Composable
fun PremiumScreen(
    viewModel: PremiumViewModel,
    navController: NavController
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    NNSScreen() {
        var isLoading by remember { mutableStateOf(true) }
        var hasError by remember { mutableStateOf(false) }
        var url by remember {
            var url = "https://non-nom-save-client-wnnb.vercel.app/subscriptions/plans"
            mutableStateOf(url)
        }

        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true

                    val cookieManager = CookieManager.getInstance()
                    cookieManager.setAcceptCookie(true)
                    cookieManager.setAcceptThirdPartyCookies(this, true)
                    cookieManager.setCookie(
                        url,
                        "access_token=${state.token}; path=/"
                    )

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

                    val headers = mapOf(
                        "Authorization" to "Bearer ${state.token}"
                    )
                    loadUrl(url, headers)
                }
            }
        )
    }
}