package cc.taylorzhang.subtune.ui.settings

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import cc.taylorzhang.subtune.R
import cc.taylorzhang.subtune.model.AppTheme
import com.maxkeppeker.sheets.core.models.base.Header
import com.maxkeppeker.sheets.core.models.base.rememberSheetState
import com.maxkeppeler.sheets.list.ListDialog
import com.maxkeppeler.sheets.list.models.ListOption
import com.maxkeppeler.sheets.list.models.ListSelection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeChoiceDialog(
    visible: Boolean,
    currentTheme: AppTheme,
    themes: Array<AppTheme> = AppTheme.values(),
    onClosed: () -> Unit,
    onChoice: (AppTheme) -> Unit,
) {
    if (visible) {
        val options = themes.map {
            ListOption(
                titleText = stringResource(id = it.labelResId),
                selected = currentTheme == it,
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
                onSelectOption = { index, _ -> onChoice(themes[index]) },
            ),
            header = Header.Default(stringResource(id = R.string.preferred_theme)),
        )
    }
}

@Preview
@Composable
private fun ThemeChoiceDialogPreview() {
    MaterialTheme {
        ThemeChoiceDialog(
            visible = true,
            currentTheme = AppTheme.LIGHT,
            onClosed = { },
            onChoice = { },
        )
    }
}