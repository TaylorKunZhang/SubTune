package cc.taylorzhang.subtune.ui.album

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringArrayResource
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
fun AlbumSortOrFilterChoiceDialog(
    visible: Boolean,
    currentSort: String,
    onClosed: () -> Unit,
    onChoice: (String) -> Unit,
) {
    val sorts = stringArrayResource(id = R.array.album_sort_or_filter)
    val sortValues = stringArrayResource(id = R.array.album_sort_or_filter_values)

    if (visible) {
        val options = sorts.mapIndexed { index, sort ->
            ListOption(
                titleText = sort,
                selected = currentSort == sortValues[index],
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
                onSelectOption = { index, _ -> onChoice(sortValues[index]) },
            ),
            header = Header.Default(stringResource(id = R.string.album_sort_or_filter)),
        )
    }
}

@Preview
@Composable
private fun AlbumSortOrFilterChoiceDialogPreview() {
    SubTuneTheme {
        AlbumSortOrFilterChoiceDialog(
            visible = true,
            currentSort = "alphabeticalByArtist",
            onClosed = { },
            onChoice = { },
        )
    }
}