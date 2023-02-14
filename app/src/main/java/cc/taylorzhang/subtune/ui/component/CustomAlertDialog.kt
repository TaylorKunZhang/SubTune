package cc.taylorzhang.subtune.ui.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import cc.taylorzhang.subtune.R
import cc.taylorzhang.subtune.ui.theme.ElevationTokens
import cc.taylorzhang.subtune.ui.theme.SubTuneTheme

@Composable
fun CustomAlertDialog(
    visible: Boolean,
    title: String? = null,
    text: String? = null,
    onClosed: () -> Unit,
    onSure: () -> Unit,
) {
    if (visible) {
        AlertDialog(
            onDismissRequest = onClosed,
            title = title?.let { { Text(text = it) } },
            text = text?.let { { Text(text = it) } },
            tonalElevation = ElevationTokens.Level0,
            dismissButton = {
                TextButton(onClick = onClosed) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    onClosed()
                    onSure()
                }) {
                    Text(text = stringResource(id = R.string.sure))
                }
            },
        )
    }
}

@Preview
@Composable
private fun CustomAlertDialogPreview() {
    SubTuneTheme {
        CustomAlertDialog(
            visible = true,
            title = "Preview Title",
            text = "Preview Message",
            onClosed = { },
            onSure = { },
        )
    }
}