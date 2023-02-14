package cc.taylorzhang.subtune.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cc.taylorzhang.subtune.ui.theme.SubTuneTheme

@Composable
fun LoadingLayout(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .previewBackground(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(modifier = Modifier.size(50.dp))
    }
}

@Preview
@Composable
fun LoadingLayoutPreview() {
    SubTuneTheme {
        LoadingLayout()
    }
}