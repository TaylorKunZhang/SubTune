package cc.taylorzhang.subtune.ui.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import cc.taylorzhang.subtune.R
import cc.taylorzhang.subtune.ui.navigation.LocalNavController
import cc.taylorzhang.subtune.ui.theme.SubTuneTheme

@Composable
fun AboutScreen() {
    val navController = LocalNavController.current
    val context = LocalContext.current
    val versionName = context.packageManager.getPackageInfo(context.packageName, 0).versionName

    AboutContent(
        versionName = versionName,
        onBackClick = { navController.popBackStack() },
        onProjectHomepageClick = {
            openUrl(context, "https://github.com/TaylorKunZhang/SubTune")
        },
    )
}

private fun openUrl(context: Context, url: String) {
    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AboutContent(
    versionName: String,
    onBackClick: () -> Unit,
    onProjectHomepageClick: () -> Unit,
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                ),
                title = { Text(stringResource(id = R.string.about_subtune)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Filled.ArrowBack, null)
                    }
                },
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(30.dp))
            ResourcesCompat.getDrawable(
                context.resources, R.mipmap.ic_launcher, context.theme
            )?.let { drawable ->
                Image(
                    modifier = Modifier
                        .requiredSize(96.dp)
                        .clip(MaterialTheme.shapes.small),
                    bitmap = drawable.toBitmap().asImageBitmap(),
                    contentDescription = null,
                )
            }
            Spacer(modifier = Modifier.height(30.dp))
            Text(
                text = stringResource(id = R.string.version_name, versionName),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
            )
            Spacer(modifier = Modifier.height(30.dp))
            AboutButton(
                text = stringResource(id = R.string.project_homepage),
                onClick = onProjectHomepageClick,
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = stringResource(id = R.string.developer),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodySmall,
            )
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun AboutButton(text: String, onClick: () -> Unit) {
    Button(
        modifier = Modifier
            .padding(start = 16.dp, top = 4.dp, end = 16.dp)
            .fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
        onClick = onClick,
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        )
    }
}

@Preview
@Composable
fun AboutScreenPreview() {
    SubTuneTheme {
        AboutContent(
            versionName = "1.0.0",
            onBackClick = { },
            onProjectHomepageClick = { },
        )
    }
}