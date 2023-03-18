package cc.taylorzhang.subtune.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cc.taylorzhang.subtune.R
import cc.taylorzhang.subtune.ui.component.CustomAlertDialog
import cc.taylorzhang.subtune.ui.component.ProgressDialog
import cc.taylorzhang.subtune.ui.navigation.LocalNavController
import cc.taylorzhang.subtune.ui.navigation.Screen
import cc.taylorzhang.subtune.ui.theme.SubTuneTheme
import cc.taylorzhang.subtune.ui.theme.isLight
import org.koin.androidx.compose.getViewModel

@Composable
fun LoginScreen(viewModel: LoginViewModel = getViewModel()) {
    val navController = LocalNavController.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showForcePlaintextPasswordDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState) {
        if (uiState.loggedIn) {
            navController.navigate(Screen.Main.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        }
    }

    if (uiState.isLoading) {
        ProgressDialog()
    }

    CustomAlertDialog(
        visible = showForcePlaintextPasswordDialog,
        title = stringResource(id = R.string.force_plaintext_password),
        text = stringResource(id = R.string.force_plaintext_password_message),
        onClosed = { showForcePlaintextPasswordDialog = false },
        onSure = { viewModel.toggleForcePlaintextPassword() },
    )

    LoginContent(
        uiState = uiState,
        onUrlChange = { viewModel.updateUrl(it) },
        onUsernameChange = { viewModel.updateUsername(it) },
        onPasswordChange = { viewModel.updatePassword(it) },
        onPasswordVisibleClick = { viewModel.togglePasswordVisible() },
        onHttpsEnabledClick = { viewModel.toggleHttpsEnabled() },
        onForcePlaintextPasswordClick = {
            if (uiState.forcePlaintextPassword) {
                viewModel.toggleForcePlaintextPassword()
            } else {
                showForcePlaintextPasswordDialog = true
            }
        },
        onLogin = { viewModel.login() },
    )
}

@Composable
private fun LoginContent(
    uiState: LoginUiState,
    onUrlChange: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onPasswordVisibleClick: () -> Unit,
    onHttpsEnabledClick: () -> Unit,
    onForcePlaintextPasswordClick: () -> Unit,
    onLogin: () -> Unit,
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(if (MaterialTheme.isLight) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
            .statusBarsPadding()
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(id = R.string.app_name),
            color = if (MaterialTheme.isLight) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
        )

        Spacer(modifier = Modifier.height(50.dp))
        LoginTextField(
            value = uiState.url,
            onValueChange = onUrlChange,
            label = stringResource(id = R.string.server_url),
        )
        LoginTextField(
            value = uiState.username,
            onValueChange = onUsernameChange,
            label = stringResource(id = R.string.username),
        )
        LoginTextField(
            value = uiState.password,
            onValueChange = onPasswordChange,
            label = stringResource(id = R.string.password),
            trailingIcon = {
                IconButton(onClick = onPasswordVisibleClick) {
                    Icon(
                        imageVector = if (uiState.passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        null,
                    )
                }
            },
            visualTransformation = if (uiState.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardType = KeyboardType.Password,
        )
        LoginTextField(
            value = stringResource(id = R.string.https),
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = onHttpsEnabledClick) {
                    Icon(
                        imageVector = if (uiState.httpsEnabled) Icons.Filled.CheckBox else Icons.Filled.CheckBoxOutlineBlank,
                        null,
                    )
                }
            },
        )
        LoginTextField(
            value = stringResource(id = R.string.force_plaintext_password),
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = onForcePlaintextPasswordClick) {
                    Icon(
                        imageVector = if (uiState.forcePlaintextPassword) Icons.Filled.CheckBox else Icons.Filled.CheckBoxOutlineBlank,
                        null,
                    )
                }
            },
        )
        Button(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
            ),
            onClick = onLogin,
        ) {
            Text(
                text = stringResource(id = R.string.login),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
        Spacer(modifier = Modifier.height(200.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoginTextField(
    value: String,
    onValueChange: (String) -> Unit,
    readOnly: Boolean = false,
    label: String? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = label?.let { { Text(text = it) } },
        trailingIcon = trailingIcon,
        readOnly = readOnly,
        singleLine = true,
        visualTransformation = visualTransformation,
        shape = RectangleShape,
        colors = TextFieldDefaults.textFieldColors(
            containerColor = if (MaterialTheme.isLight) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
        )
    )
}

@Preview
@Composable
private fun LoginScreenPreview() {
    SubTuneTheme {
        LoginContent(
            uiState = LoginUiState(),
            onUrlChange = { },
            onUsernameChange = { },
            onPasswordChange = { },
            onPasswordVisibleClick = { },
            onHttpsEnabledClick = { },
            onForcePlaintextPasswordClick = { },
            onLogin = { },
        )
    }
}
