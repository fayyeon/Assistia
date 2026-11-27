package com.powerhold.app.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
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
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.powerhold.app.data.RemapAction
import com.powerhold.app.ui.theme.AppIcons

@Composable
fun QuickTogglesTab(
    toggles: List<RemapAction>,
    selectedAction: RemapAction,
    hasAdbPermission: Boolean = false,
    isAdbBannerDismissed: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
    onDismissAdbBanner: () -> Unit = {},
    onMoreInfoAdb: () -> Unit = {},
    onSelectAction: (RemapAction) -> Unit,
    onTestAction: (RemapAction) -> Unit
) {
    if (toggles.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No actions found",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    val showBanner = !hasAdbPermission && !isAdbBannerDismissed
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    if (isLandscape) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 220.dp),
            modifier = Modifier.fillMaxSize(),
            contentPadding = contentPadding,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (showBanner) {
                item(span = { GridItemSpan(maxLineSpan) }, key = "adb_banner") {
                    AdbWarningBanner(
                        onDismiss = onDismissAdbBanner,
                        onMoreInfo = onMoreInfoAdb
                    )
                }
            }
            gridItems(toggles, key = { it.id }) { action ->
                val isSelected = action.id == selectedAction.id

                QuickToggleItemCard(
                    action = action,
                    isSelected = isSelected,
                    hasAdbPermission = hasAdbPermission,
                    onClick = { onSelectAction(action) },
                    onTest = { onTestAction(action) }
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = contentPadding,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (showBanner) {
                item(key = "adb_banner") {
                    AdbWarningBanner(
                        onDismiss = onDismissAdbBanner,
                        onMoreInfo = onMoreInfoAdb
                    )
                }
            }
            items(toggles, key = { it.id }) { action ->
                val isSelected = action.id == selectedAction.id

                QuickToggleItemCard(
                    action = action,
                    isSelected = isSelected,
                    hasAdbPermission = hasAdbPermission,
                    onClick = { onSelectAction(action) },
                    onTest = { onTestAction(action) }
                )
            }
        }
    }
}

@Composable
private fun AdbWarningBanner(
    onDismiss: () -> Unit,
    onMoreInfo: () -> Unit
) {
    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    val containerColor = if (isDark) Color(0xFF332800) else Color(0xFFFFF3CD)
    val accentColor = if (isDark) Color(0xFFFFD54F) else Color(0xFF856404)
    val textColor = if (isDark) Color(0xFFFFECB3) else Color(0xFF5C4600)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = containerColor,
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 16.dp, top = 18.dp, bottom = 10.dp)
        ) {
            Icon(
                imageVector = AppIcons.InfoOutlined,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(24.dp)
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Some actions requires adb to work.",
                style = MaterialTheme.typography.bodyLarge,
                color = textColor
            )

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onDismiss) {
                    Text(
                        text = "Dismiss",
                        color = accentColor,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(Modifier.width(4.dp))
                TextButton(onClick = onMoreInfo) {
                    Text(
                        text = "More Info",
                        color = accentColor,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun QuickToggleItemCard(
    action: RemapAction,
    isSelected: Boolean,
    hasAdbPermission: Boolean = false,
    onClick: () -> Unit,
    onTest: () -> Unit
) {
    val icon = getToggleIcon(action.toggleKey)
    val isDisabled = action.requiresAdb && !hasAdbPermission

    val containerColor = when {
        isDisabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
        isSelected -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    val titleColor = when {
        isDisabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        isSelected -> MaterialTheme.colorScheme.onPrimaryContainer
        else -> MaterialTheme.colorScheme.onSurface
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(enabled = !isDisabled, onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(20.dp),
        border = when {
            isDisabled -> BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f))
            !isSelected -> BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            else -> null
        }
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
                color = when {
                    isDisabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)
                    isSelected -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
                }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = when {
                            isDisabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                            isSelected -> MaterialTheme.colorScheme.onPrimary
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        },
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
                    fontWeight = if (isSelected && !isDisabled) FontWeight.Bold else FontWeight.SemiBold,
                    color = titleColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (action.subtitle.isNotBlank() && !action.requiresAdb) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = action.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isSelected && !isDisabled)
                            MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(Modifier.width(8.dp))

            RadioButton(
                selected = isSelected && !isDisabled,
                enabled = !isDisabled,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledUnselectedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f)
                )
            )

            if (isSelected && !isDisabled) {
                Spacer(Modifier.width(4.dp))
                FilledIconButton(
                    onClick = onTest,
                    modifier = Modifier.size(36.dp),
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(
                        imageVector = AppIcons.PlayArrow,
                        contentDescription = "Test Action",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
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
