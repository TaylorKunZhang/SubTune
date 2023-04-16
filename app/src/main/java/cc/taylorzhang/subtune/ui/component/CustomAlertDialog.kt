package cc.taylorzhang.subtune.ui.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import cc.taylorzhang.subtune.ui.theme.SubTuneTheme

@Composable
fun CustomAlertDialog(
    visible: Boolean,
    title: String? = null,
    text: String? = null,
    onClosed: () -> Unit,
    onSure: () -> Unit,
) {
    if (!visible) return
    BaseDialog(
        title = title,
        onClosed = onClosed,
        onSure = onSure,
        content = text?.let { { Text(text = it) } },
    )
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