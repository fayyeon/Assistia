package com.powerhold.app.ui.screens

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.powerhold.app.data.RemapAction
import com.powerhold.app.ui.MainViewModel
import com.powerhold.app.ui.theme.AppIcons

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainDashboardScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val selectedAction by viewModel.selectedAction.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    val quickToggles by viewModel.filteredQuickToggles.collectAsState()
    val apps by viewModel.filteredInstalledApps.collectAsState()
    val activities by viewModel.filteredAppActivities.collectAsState()

    val isLoadingApps by viewModel.isLoadingApps.collectAsState()
    val isLoadingActivities by viewModel.isLoadingActivities.collectAsState()

    var showSearch by remember { mutableStateOf(false) }
    var showAdbDialog by remember { mutableStateOf(false) }

    // Lazy load apps and activities on tab selection
    LaunchedEffect(selectedTab) {
        if (selectedTab == 1) {
            viewModel.loadApps(context)
        } else if (selectedTab == 2) {
            viewModel.loadActivities(context)
        }
    }

    if (showAdbDialog) {
        AdbHelperDialog(onDismiss = { showAdbDialog = false })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = AppIcons.Power,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Powerhold",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Power Button & Gesture Remapper",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = {
                        showSearch = !showSearch
                        if (!showSearch) viewModel.setSearchQuery("")
                    }) {
                        Icon(
                            imageVector = if (showSearch) Icons.Default.Close else Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    }
                    IconButton(onClick = { showAdbDialog = true }) {
                        Icon(
                            imageVector = AppIcons.Terminal,
                            contentDescription = "ADB Guide",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = { openAssistantSettings(context) }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Set Default Assistant",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Bar (collapsible)
            AnimatedVisibility(
                visible = showSearch,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        placeholder = { Text("Filter actions, apps, or activities...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                        )
                    )
                }
            }

            // Current Active Action Hero Card
            ActiveActionHeroCard(
                action = selectedAction,
                onTest = { viewModel.testAction(context) },
                onSetDefault = { openAssistantSettings(context) }
            )

            Spacer(Modifier.height(8.dp))

            // Expressive PrimaryTabRow
            val tabs = listOf("Quick Toggles", "Apps", "Activities")
            PrimaryTabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { viewModel.setSelectedTab(index) },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            // Tab Content
            when (selectedTab) {
                0 -> QuickTogglesTab(
                    toggles = quickToggles,
                    selectedAction = selectedAction,
                    onSelectAction = { viewModel.selectAction(it) },
                    onTestAction = { viewModel.testAction(context, it) }
                )
                1 -> InstalledAppsTab(
                    apps = apps,
                    isLoading = isLoadingApps,
                    selectedAction = selectedAction,
                    onSelectAction = { viewModel.selectAction(it) },
                    onTestAction = { viewModel.testAction(context, it) }
                )
                2 -> AppActivitiesTab(
                    activities = activities,
                    isLoading = isLoadingActivities,
                    selectedAction = selectedAction,
                    onSelectAction = { viewModel.selectAction(it) },
                    onTestAction = { viewModel.testAction(context, it) }
                )
            }
        }
    }
}

@Composable
fun ActiveActionHeroCard(
    action: RemapAction,
    onTest: () -> Unit,
    onSetDefault: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = AppIcons.Power,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "TRIGGER ACTION (POWER / GESTURE)",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = action.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = action.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onTest,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(AppIcons.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Test Trigger")
                }

                FilledTonalButton(
                    onClick = onSetDefault,
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Set as Assistant")
                }
            }
        }
    }
}

private fun openAssistantSettings(context: Context) {
    try {
        val intent = Intent(Settings.ACTION_VOICE_INPUT_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        try {
            val intent = Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e2: Exception) {
            Toast.makeText(context, "Open Settings -> Apps -> Default apps -> Digital assistant", Toast.LENGTH_LONG).show()
        }
    }
}
