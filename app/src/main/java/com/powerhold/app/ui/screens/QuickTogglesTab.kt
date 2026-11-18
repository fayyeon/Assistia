package com.powerhold.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.powerhold.app.data.RemapAction
import com.powerhold.app.ui.theme.AppIcons

@Composable
fun QuickTogglesTab(
    toggles: List<RemapAction>,
    selectedAction: RemapAction,
    onSelectAction: (RemapAction) -> Unit,
    onTestAction: (RemapAction) -> Unit
) {
    if (toggles.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No quick toggles match search",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(toggles, key = { it.id }) { action ->
            val isSelected = action.id == selectedAction.id

            QuickToggleItemCard(
                action = action,
                isSelected = isSelected,
                onClick = { onSelectAction(action) },
                onTest = { onTestAction(action) }
            )
        }
    }
}

@Composable
fun QuickToggleItemCard(
    action: RemapAction,
    isSelected: Boolean,
    onClick: () -> Unit,
    onTest: () -> Unit
) {
    val icon = getToggleIcon(action.toggleKey)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
            else
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(46.dp),
                shape = CircleShape,
                color = if (isSelected)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.surfaceVariant
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isSelected)
                            MaterialTheme.colorScheme.onPrimary
                        else
                            MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = action.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = action.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (action.isNonRootDirect) {
                        BadgePill("Non-Root Direct", MaterialTheme.colorScheme.primaryContainer)
                    } else if (action.requiresAdb) {
                        BadgePill("ADB / Panel", MaterialTheme.colorScheme.tertiaryContainer)
                    } else {
                        BadgePill("Quick Panel", MaterialTheme.colorScheme.secondaryContainer)
                    }
                }
            }

            Spacer(Modifier.width(8.dp))

            FilledIconButton(
                onClick = onTest,
                modifier = Modifier.size(36.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Icon(
                    imageVector = AppIcons.PlayArrow,
                    contentDescription = "Test toggle",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(Modifier.width(4.dp))

            RadioButton(
                selected = isSelected,
                onClick = onClick
            )
        }
    }
}

@Composable
fun BadgePill(text: String, containerColor: androidx.compose.ui.graphics.Color) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = containerColor
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

fun getToggleIcon(toggleKey: String?): ImageVector {
    return when (toggleKey) {
        "flashlight" -> AppIcons.Flashlight
        "auto_rotate" -> AppIcons.ScreenRotation
        "dnd" -> AppIcons.Bedtime
        "sound_mode" -> AppIcons.VolumeUp
        "battery_saver" -> AppIcons.BatterySaver
        "color_inversion" -> AppIcons.InvertColors
        "extra_dim" -> AppIcons.Nightlight
        "eye_protection" -> AppIcons.Nightlight
        "bluetooth" -> AppIcons.Bluetooth
        "wifi" -> AppIcons.Wifi
        "mobile_data" -> AppIcons.SignalCellular
        "airplane_mode" -> AppIcons.AirplaneMode
        "data_saver" -> AppIcons.DataSaver
        "location" -> AppIcons.Location
        "camera_access" -> AppIcons.Videocam
        "microphone_access" -> AppIcons.Mic
        else -> AppIcons.Widgets
    }
}
