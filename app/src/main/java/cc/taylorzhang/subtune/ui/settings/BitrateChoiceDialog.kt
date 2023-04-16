package cc.taylorzhang.subtune.ui.settings

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import cc.taylorzhang.subtune.R
import cc.taylorzhang.subtune.ui.theme.SubTuneTheme
import com.maxkeppeker.sheets.core.models.base.Header
import com.maxkeppeker.sheets.core.models.base.rememberSheetState
import com.maxkeppeler.sheets.list.ListDialog
import com.maxkeppeler.sheets.list.models.ListOption
import com.maxkeppeler.sheets.list.models.ListSelection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BitrateChoiceDialog(
    visible: Boolean,
    title: String,
    currentBitrate: Int,
    bitrateChoices: Array<Int> = arrayOf(
        32, 40, 48, 56, 64, 80, 96, 112, 128, 160, 192, 224, 256, 320, 0,
    ),
    onClosed: () -> Unit,
    onChoice: (Int) -> Unit,
) {
    if (visible) {
        val options = bitrateChoices.map {
            ListOption(
                titleText = when (it) {
                    0 -> stringResource(id = R.string.unlimited_kbps)
                    else -> stringResource(id = R.string.kbps_value, it)
                },
                selected = currentBitrate == it,
            )
        }

        ListDialog(
            state = rememberSheetState(
                visible = true,
                onCloseRequest = { onClosed() },
            ),
            selection = ListSelection.Single(
                showRadioButtons = true,
                options = options,
                onSelectOption = { index, _ -> onChoice(bitrateChoices[index]) },
            ),
            header = Header.Default(title),
        )
    }
}

@Preview
@Composable
private fun BitrateChoiceDialogPreview() {
    SubTuneTheme {
        BitrateChoiceDialog(
            visible = true,
            title = stringResource(id = R.string.max_bitrate_wifi),
            currentBitrate = 0,
            onClosed = { },
            onChoice = { },
        )
    }
}