package ua.nure.nomnomsave.ui.auth.forgotpassword

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import ua.nure.nomnomsave.R
import ua.nure.nomnomsave.ui.auth.forgotpassword.compose.NewPasswordDialog
import ua.nure.nomnomsave.ui.auth.forgotpassword.compose.VerifyCodeDialog
import ua.nure.nomnomsave.ui.compose.NNSButton
import ua.nure.nomnomsave.ui.compose.NNSInputField
import ua.nure.nomnomsave.ui.compose.NNSScreen
import ua.nure.nomnomsave.ui.compose.NNSTitle
import ua.nure.nomnomsave.ui.theme.AppTheme

@Composable
fun ForgotPasswordScreen(
    viewModel: ForgotPasswordViewModel,
    navController: NavController,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        viewModel.event.collect {
            when (it) {
                ForgotPassword.Event.OnBack -> navController.navigateUp()
                is ForgotPassword.Event.OnNavigate -> navController.navigate(route = it.route)
            }
        }
    }

    ForgotPasswordScreenContent(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
private fun ForgotPasswordScreenContent(
    state: ForgotPassword.State,
    onAction: (ForgotPassword.Action) -> Unit
) {
    NNSScreen(
        modifier = Modifier.padding(horizontal = AppTheme.dimension.normal),
        verticalArrangement = Arrangement.Top
    ) {
        NNSTitle(title = stringResource(R.string.resettingPassword))

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = AppTheme.dimension.normal),
            text = stringResource(R.string.enterYourEmail),
            style = AppTheme.typography.large
        )
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = AppTheme.dimension.extraSmall),
            text = stringResource(R.string.forgotPasswordSubtitle),
            style = AppTheme.typography.small.copy(color = AppTheme.color.grey)
        )

        NNSInputField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = AppTheme.dimension.normal),
            label = stringResource(R.string.emailAddress),
            value = state.email,
            errorText = state.emailError,
        ) {
            onAction(ForgotPassword.Action.OnEmailChange(email = it))
        }

        Spacer(modifier = Modifier.weight(1F))

        NNSButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = AppTheme.dimension.normal),
            text = stringResource(R.string.submit),
            enabled = !state.inProgress,
        ) {
            onAction(ForgotPassword.Action.OnResetPassword)
        }
    }

    if (state.showVerificationDialog) {
        VerifyCodeDialog(
            email = state.email,
            codeError = state.codeError,
            onResend = { onAction(ForgotPassword.Action.OnResendCode) },
            onVerify = { code -> onAction(ForgotPassword.Action.OnVerifyCode(code = code)) },
            onDismiss = { onAction(ForgotPassword.Action.OnDismissCodeDialog) }
        )
    }

    if (state.showNewPasswordDialog) {
        NewPasswordDialog(
            onDismiss = { onAction(ForgotPassword.Action.OnDismissPasswordDialog) },
            onNewPassword = { onAction(ForgotPassword.Action.OnNewPassword(password = it)) }
        )
    }
}

@Preview
@Composable
private fun ForgotPasswordScreenContentPreview() {
    AppTheme {
        Box(modifier = Modifier.background(AppTheme.color.background)) {
            ForgotPasswordScreenContent(state = ForgotPassword.State()) {}
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ForgotPasswordScreenContentDarkPreview() {
    AppTheme {
        Box(modifier = Modifier.background(AppTheme.color.background)) {
            ForgotPasswordScreenContent(state = ForgotPassword.State()) {}
        }
    }
}