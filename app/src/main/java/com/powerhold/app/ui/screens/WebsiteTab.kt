package com.powerhold.app.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.powerhold.app.data.ActionType
import com.powerhold.app.data.RemapAction
import com.powerhold.app.ui.theme.AppIcons

@Composable
fun WebsiteTab(
    savedUrl: String,
    selectedAction: RemapAction,
    onOpenThemeDialog: () -> Unit,
    onSelectWebsite: (String) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    var urlInput by remember { mutableStateOf(savedUrl) }

    LaunchedEffect(savedUrl) {
        if (urlInput.isBlank() && savedUrl.isNotBlank()) {
            urlInput = savedUrl
        }
    }

    val isWebsiteSelected = selectedAction.type == ActionType.OPEN_WEBSITE
    val isAlreadySelected = isWebsiteSelected && selectedAction.url == normalizeDisplayUrl(urlInput)
    val isImeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0

    val bottomPadding = when {
        isImeVisible -> 8.dp
        isLandscape -> 80.dp // Leaves room for floating Test Trigger button below in landscape
        else -> 112.dp
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .then(
                if (isLandscape) Modifier.verticalScroll(rememberScrollState()) else Modifier
            )
            .windowInsetsPadding(WindowInsets.ime.union(WindowInsets.navigationBars))
            .padding(
                start = 16.dp,
                end = 16.dp,
                bottom = bottomPadding
            ),
        verticalArrangement = Arrangement.Bottom
    ) {
        // Theme icon placed above-right of the website box
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(
                onClick = onOpenThemeDialog,
                modifier = Modifier.size(44.dp)
            ) {
                Icon(
                    imageVector = AppIcons.Paintbrush,
                    contentDescription = "Theme Settings",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isWebsiteSelected)
                    MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.surfaceVariant
            ),
            border = if (!isWebsiteSelected) {
                BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            } else null
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(if (isLandscape) 14.dp else 18.dp),
                verticalArrangement = Arrangement.spacedBy(if (isLandscape) 10.dp else 14.dp)
            ) {
                OutlinedTextField(
                    value = urlInput,
                    onValueChange = { urlInput = it },
                    placeholder = { Text("https://example.com") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Uri,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (urlInput.isNotBlank() && !isAlreadySelected) {
                                onSelectWebsite(urlInput)
                                keyboardController?.hide()
                            }
                        }
                    ),
                    trailingIcon = {
                        if (urlInput.isNotEmpty()) {
                            IconButton(onClick = { urlInput = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                    )
                )

                Button(
                    onClick = {
                        onSelectWebsite(urlInput)
                        keyboardController?.hide()
                    },
                    enabled = urlInput.isNotBlank() && !isAlreadySelected,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        disabledContainerColor = if (isAlreadySelected)
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.65f)
                        else
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                        disabledContentColor = if (isAlreadySelected)
                            MaterialTheme.colorScheme.onPrimary
                        else
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    )
                ) {
                    Icon(
                        imageVector = AppIcons.Check,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = if (isAlreadySelected)
                            "Selected as Trigger Action"
                        else
                            "Set as Trigger Action",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

private fun normalizeDisplayUrl(raw: String): String {
    val trimmed = raw.trim()
    if (trimmed.isBlank()) return ""
    return if (trimmed.startsWith("http://", ignoreCase = true) ||
        trimmed.startsWith("https://", ignoreCase = true)
    ) {
        trimmed
    } else {
        "https://$trimmed"
    }
}
