package ua.nure.nomnomsave.ui.auth.login

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import ua.nure.nomnomsave.App
import ua.nure.nomnomsave.R
import ua.nure.nomnomsave.navigation.Screen
import ua.nure.nomnomsave.ui.compose.NNSButton
import ua.nure.nomnomsave.ui.compose.NNSInputField
import ua.nure.nomnomsave.ui.compose.NNSScreen
import ua.nure.nomnomsave.ui.compose.NNSTitle
import ua.nure.nomnomsave.ui.theme.AppTheme

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    navController: NavController,
) {

    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        viewModel.event.collect {
            when (it) {
                Login.Event.OnBack -> navController.navigateUp()
                is Login.Event.OnNavigate -> navController.navigate(route = it.route)
            }
        }
    }
    LoginScreenContent(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
private fun LoginScreenContent(
    state: Login.State,
    onAction: (Login.Action) -> Unit
) {
    NNSScreen(
        modifier = Modifier.padding(horizontal = AppTheme.dimension.normal),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        NNSTitle(title = stringResource(R.string.loginTitle))

        Image(
            modifier = Modifier.size(150.dp),
            painter = painterResource(if (isSystemInDarkTheme()) R.drawable.logo_dark else R.drawable.logo_light),
            contentDescription = null
        )

        Text(
            modifier = Modifier
                .fillMaxWidth(),
            text = stringResource(R.string.welcomeBack),
            style = AppTheme.typography.large
        )
        Text(
            modifier = Modifier
                .fillMaxWidth(),
            text = stringResource(R.string.loginSubtitle),
            style = AppTheme.typography.small.copy(
                color = AppTheme.color.grey
            )
        )
        Column {
            NNSInputField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = AppTheme.dimension.normal),
                label = stringResource(R.string.email),
                value = state.email,
                errorText = if (state.loginError != null) "" else null
            ) {
                onAction(Login.Action.OnEmailChange(email = it))
            }
            NNSInputField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = AppTheme.dimension.normal),
                label = stringResource(R.string.password),
                value = state.password,
                isPassword = true,
                errorText = state.loginError,
            ) {
                onAction(Login.Action.OnPasswordChange(password = it))
            }
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppTheme.dimension.normal)
                    .padding(top = AppTheme.dimension.normal)
                    .clickable {
                        onAction(Login.Action.OnNavigate(route = Screen.Auth.ForgotPassword))
                    },
                text = stringResource(R.string.forgotPassword),
                style = AppTheme.typography.regular.copy(
                    color = AppTheme.color.active,
                    textAlign = TextAlign.End
                ),
            )
        }

        NNSButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppTheme.dimension.normal),
            text = stringResource(R.string.login),
        ) {
            onAction(Login.Action.OnLogIn)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .height(1.dp)
                    .background(AppTheme.color.grey)
                    .weight(1F)
            )
            Text(
                modifier = Modifier.padding(horizontal = AppTheme.dimension.normal),
                text = stringResource(R.string.orContinueWith),
                style = AppTheme.typography.small.copy(
                    color = AppTheme.color.grey
                )
            )
            Box(
                modifier = Modifier
                    .height(1.dp)
                    .background(AppTheme.color.grey)
                    .weight(1F)
            )
        }

        NNSButton(
            modifier = Modifier
                .padding(horizontal = AppTheme.dimension.large),
            text = stringResource(R.string.google),
            textColor = Color.Black,
            icon = R.drawable.google,
            color = Color.White
        ) {
            //onAction(Login.Action.OnGoogleLogIn())
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = AppTheme.dimension.small, bottom = AppTheme.dimension.large)
                .clickable {
                    onAction(Login.Action.OnNavigate(Screen.Auth.Registration))
                },
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                modifier = Modifier.padding(end = AppTheme.dimension.small),
                text = stringResource(R.string.dontHaveAnAcc),
                style = AppTheme.typography.regular.copy(
                    color = AppTheme.color.grey
                )
            )
            Text(
                text = stringResource(R.string.registrationTitle),
                style = AppTheme.typography.regular.copy(
                    color = AppTheme.color.active
                ),
            )
        }
    }
}

@Preview
@Composable
private fun LoginScreenContentPreview(modifier: Modifier = Modifier) {
    AppTheme {
        Box(
            modifier = modifier.background(color = AppTheme.color.background)
        ) {
            LoginScreenContent(
                state = Login.State(
                )
            ) { }
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun LoginScreenContentDarkPreview(modifier: Modifier = Modifier) {
    AppTheme {
        Box(
            modifier = modifier.background(color = AppTheme.color.background)
        ) {
            LoginScreenContent(
                state = Login.State(
                )
            ) { }
        }
    }
}