package cc.taylorzhang.subtune.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cc.taylorzhang.subtune.R
import cc.taylorzhang.subtune.ui.theme.SubTuneTheme

@Composable
fun ErrorLayout(
    modifier: Modifier = Modifier,
    icon: Painter = rememberVectorPainter(image = Icons.Outlined.Error),
    message: String = stringResource(id = R.string.error_content),
    buttonText: String = stringResource(id = R.string.error_retry_button_content),
    onRetryClick: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .previewBackground(MaterialTheme.colorScheme.surface),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = icon,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
        )
        Text(
            text = message,
            modifier = Modifier.padding(vertical = 30.dp),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium,
        )
        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
            ),
            onClick = { onRetryClick?.invoke() },
        ) {
            Text(
                text = buttonText,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}

@Preview
@Composable
fun ErrorLayoutPreview() {
    SubTuneTheme {
        ErrorLayout()
    }
}