package cc.taylorzhang.subtune.ui.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputDialog(
    visible: Boolean,
    title: String? = null,
    defaultValue: String? = null,
    validationListener: ((String?) -> ValidationResult)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    maxLines: Int = Int.MAX_VALUE,
    onClosed: () -> Unit,
    onSure: (String) -> Unit,
) {
    if (!visible) return
    var value by remember { mutableStateOf(defaultValue ?: "") }
    var validationResult by remember { mutableStateOf<ValidationResult?>(null) }

    LaunchedEffect(value) {
        validationResult = validationListener?.invoke(value)
    }

    BaseDialog(
        title = title,
        onSureEnabled = validationResult?.valid ?: true,
        onClosed = onClosed,
        onSure = { onSure(value) },
    ) {
        Column(modifier = Modifier.animateContentSize()) {
            TextField(
                value = value,
                onValueChange = { value = it },
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                maxLines = maxLines,
            )
            Text(
                text = validationResult?.errorMessage ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
            )
        }

    }
}

sealed class ValidationResult(
    internal val valid: Boolean = true,
    internal open val errorMessage: String? = null
) {
    /**
     * Input is valid.
     */
    object Valid : ValidationResult()

    /**
     * Input is not valid.
     * @param errorMessage The reason why the input is not valid.
     */
    class Invalid(override val errorMessage: String) : ValidationResult(false)
}