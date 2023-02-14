package cc.taylorzhang.subtune.ui.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Album
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

enum class MainTab(
    val iconImageVector: ImageVector,
    val selectedImageVector: ImageVector,
) {
    ALBUM(Icons.Outlined.Album, Icons.Filled.Album),
    PLAYLIST(Icons.Outlined.LibraryMusic, Icons.Filled.LibraryMusic),
    SETTINGS(Icons.Outlined.Settings, Icons.Filled.Settings),
}