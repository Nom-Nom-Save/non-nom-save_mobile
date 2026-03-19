package ua.nure.nomnomsave.ui.auth.register

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import ua.nure.nomnomsave.App
import ua.nure.nomnomsave.R
import ua.nure.nomnomsave.navigation.Screen
import ua.nure.nomnomsave.ui.compose.AccountVerificationDialog
import ua.nure.nomnomsave.ui.compose.NNSButton
import ua.nure.nomnomsave.ui.compose.NNSInputField
import ua.nure.nomnomsave.ui.compose.NNSScreen
import ua.nure.nomnomsave.ui.compose.NNSTitle
import ua.nure.nomnomsave.ui.theme.AppTheme

@Composable
fun RegistrationScreen(
    viewModel: RegisterViewModel,
    navController: NavController,
) {

    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        viewModel.event.collect {
            when (it) {
                Register.Event.OnBack -> navController.navigateUp()
                is Register.Event.OnNavigate -> navController.navigate(route = it.route)
            }
        }
    }
    RegistrationScreenContent(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
private fun RegistrationScreenContent(
    state: Register.State,
    onAction: (Register.Action) -> Unit
) {
    NNSScreen(
        modifier = Modifier.padding(horizontal = AppTheme.dimension.normal),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        NNSTitle(title = stringResource(R.string.registrationTitle))

        Text(
            modifier = Modifier
                .fillMaxWidth(),
            text = stringResource(R.string.joinUs),
            style = AppTheme.typography.large
        )
        Text(
            modifier = Modifier
                .fillMaxWidth(),
            text = stringResource(R.string.registrationMessage),
            style = AppTheme.typography.small.copy(
                color = AppTheme.color.grey
            )
        )
        Column {
            NNSInputField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = AppTheme.dimension.normal),
                label = stringResource(R.string.name),
                value = state.name,
                errorText = state.nameError
            ) {
                onAction(Register.Action.OnNameChange(name = it))
            }
            NNSInputField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = AppTheme.dimension.normal),
                label = stringResource(R.string.email),
                value = state.email,
                errorText = state.emailError
            ) {
                onAction(Register.Action.OnEmailChange(email = it))
            }
            NNSInputField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = AppTheme.dimension.normal),
                label = stringResource(R.string.password),
                value = state.password,
                isPassword = true,
                errorText = state.passwordError
            ) {
                onAction(Register.Action.OnPasswordChange(password = it))
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = AppTheme.dimension.small)
                    .clickable{onAction(Register.Action.OnPrivacyPolicyAgreementChange(isAgreed = !state.isPrivacyPolicyAgreed))},
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(
                    checked = state.isPrivacyPolicyAgreed,
                    onCheckedChange = { isChecked ->
                        onAction(Register.Action.OnPrivacyPolicyAgreementChange(isAgreed = isChecked))
                    },
                    colors = CheckboxDefaults.colors(
                        checkedColor = AppTheme.color.active,
                        uncheckedColor = AppTheme.color.active
                    ),
                    modifier = Modifier.padding(end = AppTheme.dimension.extraSmall)
                )
                Text(
                    text = stringResource(R.string.privacyPolicyAgreement),
                    style = AppTheme.typography.small.copy(
                        textAlign = TextAlign.Start,
                        color = AppTheme.color.grey
                    )
                )
            }
        }

        NNSButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppTheme.dimension.normal),
            text = stringResource(R.string.signUp),
            enabled = state.isPrivacyPolicyAgreed
        ) {
            onAction(Register.Action.OnRegister)
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
            onAction(Register.Action.OnGoogle)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = AppTheme.dimension.small, bottom = AppTheme.dimension.large)
                .clickable {
                    onAction(Register.Action.OnNavigate(Screen.Auth.SignIn))
                },
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                modifier = Modifier.padding(end = AppTheme.dimension.small),
                text = stringResource(R.string.haveAnAcc),
                style = AppTheme.typography.regular.copy(
                    color = AppTheme.color.grey
                )
            )
            Text(
                text = stringResource(R.string.login),
                style = AppTheme.typography.regular.copy(
                    color = AppTheme.color.active
                ),
            )
        }

        if (state.showVerificationDialog) {
            AccountVerificationDialog(
                email = state.email,
                onVerify = { code ->
                    onAction(
                        Register.Action.OnVerificationEmailCode(code = code)
                    )
                },
                onDismiss = {
                    onAction(
                        Register.Action.OnShowVerificationDialog(false)
                    )
                }
            )
        }
    }
}

@Preview
@Composable
private fun RegistrationScreenContentPreview(modifier: Modifier = Modifier) {
    AppTheme {
        Box(
            modifier = modifier.background(color = AppTheme.color.background)
        ) {
            RegistrationScreenContent(
                state = Register.State(
                    isPrivacyPolicyAgreed = true
                )
            ) { }
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun RegistrationScreenContentDarkPreview(modifier: Modifier = Modifier) {
    AppTheme {
        Box(
            modifier = modifier.background(color = AppTheme.color.background)
        ) {
            RegistrationScreenContent(
                state = Register.State(
                    isPrivacyPolicyAgreed = true
                )
            ) { }
        }
    }
}