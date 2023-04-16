package cc.taylorzhang.subtune.ui.component

import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import cc.taylorzhang.subtune.R
import cc.taylorzhang.subtune.ui.theme.ElevationTokens

@Composable
fun BaseDialog(
    title: String? = null,
    onSureEnabled: Boolean = true,
    onClosed: () -> Unit,
    onSure: () -> Unit,
    content: @Composable (() -> Unit)? = null,
) {
    AlertDialog(
        onDismissRequest = onClosed,
        modifier = Modifier.wrapContentHeight(),
        title = title?.let { { Text(text = it) } },
        text = content,
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
            }, enabled = onSureEnabled) {
                Text(text = stringResource(id = R.string.sure))
            }
        },
    )
}