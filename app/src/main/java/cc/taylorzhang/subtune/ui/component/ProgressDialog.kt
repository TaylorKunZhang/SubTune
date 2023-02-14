package cc.taylorzhang.subtune.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import cc.taylorzhang.subtune.ui.theme.SubTuneTheme

@Composable
fun ProgressDialog() {
    Dialog(
        onDismissRequest = {},
        DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(100.dp)
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = MaterialTheme.shapes.small,
                )
        ) {
            CircularProgressIndicator()
        }
    }
}

@Preview
@Composable
fun ProgressDialogPreview() {
    SubTuneTheme {
        ProgressDialog()
    }
}