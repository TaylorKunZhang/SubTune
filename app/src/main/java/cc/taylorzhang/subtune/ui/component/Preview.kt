package cc.taylorzhang.subtune.ui.component

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalInspectionMode

@ReadOnlyComposable
@Composable
fun isPreview() = LocalInspectionMode.current

fun Modifier.previewBackground(
    color: Color,
    shape: Shape = RectangleShape,
) = composed { if (isPreview()) background(color, shape) else this }