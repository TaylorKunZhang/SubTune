package cc.taylorzhang.subtune.ui.settings

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import cc.taylorzhang.subtune.R
import cc.taylorzhang.subtune.ui.component.InputDialog
import cc.taylorzhang.subtune.ui.component.ValidationResult
import cc.taylorzhang.subtune.ui.theme.SubTuneTheme

@Composable
fun RandomSongCountInputDialog(
    visible: Boolean,
    currentCount: Int,
    onClosed: () -> Unit,
    onSure: (Int) -> Unit,
) {
    val invalidTips = stringResource(id = R.string.random_song_count_invalid)
    InputDialog(
        visible = visible,
        title = stringResource(id = R.string.random_song_count),
        defaultValue = currentCount.toString(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
        ),
        validationListener = {
            val count = it?.toIntOrNull()
            if (count == null || count < 1 || count > 500) {
                ValidationResult.Invalid(invalidTips)
            } else {
                ValidationResult.Valid
            }
        },
        onClosed = onClosed,
        onSure = { onSure(it.toInt()) },
    )
}

@Preview
@Composable
fun RandomSongCountInputDialogPreview() {
    SubTuneTheme {
        RandomSongCountInputDialog(
            visible = true,
            currentCount = 100,
            onClosed = { },
            onSure = { },
        )
    }
}