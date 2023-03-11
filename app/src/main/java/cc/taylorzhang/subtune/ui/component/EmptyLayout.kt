package cc.taylorzhang.subtune.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import cc.taylorzhang.subtune.R
import cc.taylorzhang.subtune.ui.theme.SubTuneTheme

@Composable
fun EmptyLayout(
    modifier: Modifier = Modifier,
    contentText: String = stringResource(id = R.string.empty_content),
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .previewBackground(MaterialTheme.colorScheme.surface),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = contentText,
            color = contentColor,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Preview
@Composable
fun EmptyLayoutPreview() {
    SubTuneTheme {
        EmptyLayout()
    }
}