package cc.taylorzhang.subtune.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cc.taylorzhang.subtune.R
import cc.taylorzhang.subtune.model.Settings
import cc.taylorzhang.subtune.player.LocalAudioPlayer
import cc.taylorzhang.subtune.ui.component.CustomAlertDialog
import cc.taylorzhang.subtune.ui.component.ProgressDialog
import cc.taylorzhang.subtune.ui.component.previewBackground
import cc.taylorzhang.subtune.ui.navigation.LocalNavController
import cc.taylorzhang.subtune.ui.navigation.Screen
import cc.taylorzhang.subtune.ui.theme.SubTuneTheme
import org.koin.androidx.compose.getViewModel

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = getViewModel()) {
    val navController = LocalNavController.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val audioPlayer = LocalAudioPlayer.current
    var showMaxBitrateWifiChoiceDialog by remember { mutableStateOf(false) }
    var showMaxBitrateMobileChoiceDialog by remember { mutableStateOf(false) }
    var showThemeChoiceDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState) {
        if (uiState.loggedOut) {
            navController.navigate(Screen.Login.route) {
                popUpTo(Screen.Main.route) { inclusive = true }
            }
        }
    }

    if (uiState.isProgress) {
        ProgressDialog()
    }

    BitrateChoiceDialog(
        visible = showMaxBitrateWifiChoiceDialog,
        title = stringResource(id = R.string.max_bitrate_wifi),
        currentBitrate = uiState.settings.maxBitrateWifi,
        onClosed = { showMaxBitrateWifiChoiceDialog = false },
        onChoice = {
            audioPlayer.onMaxBitrateWifiChanged(it)
            viewModel.updateMaxBitrateWifi(it)
        },
    )

    BitrateChoiceDialog(
        visible = showMaxBitrateMobileChoiceDialog,
        title = stringResource(id = R.string.max_bitrate_mobile),
        currentBitrate = uiState.settings.maxBitrateMobile,
        onClosed = { showMaxBitrateMobileChoiceDialog = false },
        onChoice = {
            audioPlayer.onMaxBitrateMobileChanged(it)
            viewModel.updateMaxBitrateMobile(it)
        },
    )

    ThemeChoiceDialog(
        visible = showThemeChoiceDialog,
        currentTheme = uiState.settings.preferredTheme,
        onClosed = { showThemeChoiceDialog = false },
        onChoice = { viewModel.updatePreferredTheme(it) },
    )

    CustomAlertDialog(
        visible = showLogoutDialog,
        title = stringResource(id = R.string.logout),
        text = stringResource(id = R.string.logout_message),
        onClosed = { showLogoutDialog = false },
        onSure = {
            audioPlayer.clearPlaybackList()
            viewModel.logout()
        },
    )

    SettingsContent(
        uiState = uiState,
        onLogoutClick = { showLogoutDialog = true },
        onMaxBitrateWifiClick = { showMaxBitrateWifiChoiceDialog = true },
        onMaxBitrateMobileClick = { showMaxBitrateMobileChoiceDialog = true },
        onPreferredThemeClick = { showThemeChoiceDialog = true },
        onDynamicColorCheckedChange = { viewModel.updateDynamicColor(it) },
        onAboutClick = { navController.navigate(Screen.About.route) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsContent(
    uiState: SettingsUiState,
    onMaxBitrateWifiClick: () -> Unit,
    onMaxBitrateMobileClick: () -> Unit,
    onPreferredThemeClick: () -> Unit,
    onDynamicColorCheckedChange: (Boolean) -> Unit,
    onAboutClick: () -> Unit,
    onLogoutClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .previewBackground(MaterialTheme.colorScheme.surface),
    ) {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
            ),
            title = { Text(stringResource(id = R.string.settings)) },
        )
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
        ) {
            SettingsServer(uiState = uiState)
            SettingsNetwork(
                uiState = uiState,
                onMaxBitrateWifiClick = onMaxBitrateWifiClick,
                onMaxBitrateMobileClick = onMaxBitrateMobileClick,
            )
            SettingsTheme(
                uiState = uiState,
                onPreferredThemeClick = onPreferredThemeClick,
                onDynamicColorCheckedChange = onDynamicColorCheckedChange,
            )
            SettingsOther(
                onAboutClick = onAboutClick,
            )
            Button(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
                onClick = onLogoutClick,
            ) {
                Text(
                    text = stringResource(id = R.string.logout),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }
    }
}

@Composable
private fun SettingsServer(uiState: SettingsUiState) {
    Text(
        text = stringResource(id = R.string.server),
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(horizontal = 16.dp)
    )
    ListItem(
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent,
        ),
        headlineText = {
            Text(text = uiState.url, overflow = TextOverflow.Ellipsis, maxLines = 1)
        },
        supportingText = {
            Text(text = uiState.username, overflow = TextOverflow.Ellipsis, maxLines = 1)
        }
    )
}

@Composable
private fun SettingsNetwork(
    uiState: SettingsUiState,
    onMaxBitrateWifiClick: () -> Unit,
    onMaxBitrateMobileClick: () -> Unit,
) {
    Text(
        text = stringResource(id = R.string.network),
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(horizontal = 16.dp),
    )
    ListItem(
        modifier = Modifier.clickable(onClick = onMaxBitrateWifiClick),
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent,
        ),
        headlineText = {
            Text(text = stringResource(id = R.string.max_bitrate_wifi))
        },
        supportingText = {
            val maxBitrateWifi = uiState.settings.maxBitrateWifi
            Text(
                text = when (maxBitrateWifi) {
                    0 -> stringResource(id = R.string.unlimited_kbps)
                    else -> stringResource(id = R.string.kbps_value, maxBitrateWifi)
                }
            )
        }
    )
    ListItem(
        modifier = Modifier.clickable(onClick = onMaxBitrateMobileClick),
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent,
        ),
        headlineText = {
            Text(text = stringResource(id = R.string.max_bitrate_mobile))
        },
        supportingText = {
            val maxBitrateMobile = uiState.settings.maxBitrateMobile
            Text(
                text = when (maxBitrateMobile) {
                    0 -> stringResource(id = R.string.unlimited_kbps)
                    else -> stringResource(id = R.string.kbps_value, maxBitrateMobile)
                }
            )
        }
    )
}

@Composable
private fun SettingsTheme(
    uiState: SettingsUiState,
    onPreferredThemeClick: () -> Unit,
    onDynamicColorCheckedChange: (Boolean) -> Unit,
) {
    Text(
        text = stringResource(id = R.string.theme),
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(horizontal = 16.dp),
    )
    ListItem(
        modifier = Modifier.clickable(onClick = onPreferredThemeClick),
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent,
        ),
        headlineText = {
            Text(text = stringResource(id = R.string.preferred_theme))
        },
        supportingText = {
            Text(text = stringResource(id = uiState.settings.preferredTheme.labelResId))
        }
    )
    ListItem(
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent,
        ),
        headlineText = {
            Text(text = stringResource(id = R.string.dynamic_color))
        },
        trailingContent = {
            Switch(
                checked = uiState.settings.dynamicColor,
                onCheckedChange = onDynamicColorCheckedChange,
            )
        }
    )
}

@Composable
private fun SettingsOther(
    onAboutClick: () -> Unit,
) {
    Text(
        text = stringResource(id = R.string.other),
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(horizontal = 16.dp),
    )
    ListItem(
        modifier = Modifier.clickable(onClick = onAboutClick),
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent,
        ),
        headlineText = {
            Text(text = stringResource(id = R.string.about_subtune))
        },
        trailingContent = {
            Icon(Icons.Filled.NavigateNext, null)
        }
    )
}

@Preview
@Composable
fun SettingsScreenPreview() {
    SubTuneTheme {
        SettingsContent(
            uiState = SettingsUiState(
                url = "demo.subsonic.org",
                username = "demo",
                settings = Settings(
                    maxBitrateWifi = 0,
                    maxBitrateMobile = 128,
                )
            ),
            onMaxBitrateWifiClick = { },
            onMaxBitrateMobileClick = { },
            onPreferredThemeClick = { },
            onDynamicColorCheckedChange = { },
            onAboutClick = { },
            onLogoutClick = { },
        )
    }
}