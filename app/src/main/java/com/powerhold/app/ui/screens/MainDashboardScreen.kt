package com.powerhold.app.ui.screens

import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.powerhold.app.data.ActionType
import com.powerhold.app.data.RemapAction
import com.powerhold.app.ui.MainViewModel
import com.powerhold.app.ui.theme.AppIcons

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainDashboardScreen(
    viewModel: MainViewModel,
    onOpenAssistantSettings: () -> Unit = {}
) {
    val context = LocalContext.current
    val selectedAction by viewModel.selectedAction.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isAmoledMode by viewModel.isAmoledMode.collectAsState()

    val quickToggles by viewModel.filteredQuickToggles.collectAsState()
    val apps by viewModel.filteredInstalledApps.collectAsState()
    val activities by viewModel.filteredAppActivities.collectAsState()

    val isLoadingApps by viewModel.isLoadingApps.collectAsState()
    val isLoadingActivities by viewModel.isLoadingActivities.collectAsState()

    var showSearch by remember { mutableStateOf(false) }
    var showAdbDialog by remember { mutableStateOf(false) }

    // Track whether Powerhold is already set as the default assistant
    var isAssistantSet by remember { mutableStateOf(false) }
    // Track whether WRITE_SECURE_SETTINGS permission is granted via ADB
    var hasAdbPermission by remember { mutableStateOf(false) }

    fun checkPermissions() {
        isAssistantSet = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = context.getSystemService(RoleManager::class.java)
            roleManager?.isRoleHeld(RoleManager.ROLE_ASSISTANT) == true
        } else {
            false
        }

        hasAdbPermission = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.WRITE_SECURE_SETTINGS
        ) == PackageManager.PERMISSION_GRANTED
    }

    // Check on composition and re-check when app resumes
    LaunchedEffect(Unit) { checkPermissions() }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) checkPermissions()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

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
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = AppIcons.Power,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary,
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
                                text = "Default Assistant Shortcut Remapper",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    // AMOLED pure black toggle button (only visible when device is in dark mode)
                    if (isSystemInDarkTheme()) {
                        IconButton(onClick = {
                            val next = !isAmoledMode
                            viewModel.setAmoledMode(next)
                            val msg = if (next) "AMOLED Pure Black mode enabled" else "Standard Dark mode enabled"
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(
                                imageVector = AppIcons.Contrast,
                                contentDescription = "Toggle AMOLED Pure Black",
                                tint = if (isAmoledMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
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
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            // Material 3 Navigation Bar at the bottom
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 3.dp
            ) {
                val navItems = listOf(
                    Triple("Quick Toggles", AppIcons.Widgets, 0),
                    Triple("Apps", AppIcons.Apps, 1),
                    Triple("Activities", AppIcons.Code, 2)
                )
                navItems.forEach { (label, icon, index) ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { viewModel.setSelectedTab(index) },
                        icon = {
                            Icon(imageVector = icon, contentDescription = label)
                        },
                        label = {
                            Text(
                                text = label,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }
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
                context = context,
                action = selectedAction,
                isAssistantSet = isAssistantSet,
                hasAdbPermission = hasAdbPermission,
                onTest = { viewModel.testAction(context) },
                onSetDefault = onOpenAssistantSettings
            )

            Spacer(Modifier.height(8.dp))

            // Tab Content in weighted box so it scrolls properly above the bottom navigation bar
            Box(modifier = Modifier.weight(1f)) {
                when (selectedTab) {
                    0 -> QuickTogglesTab(
                        toggles = quickToggles,
                        selectedAction = selectedAction,
                        hasAdbPermission = hasAdbPermission,
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
}

@Composable
fun ActiveActionHeroCard(
    context: Context,
    action: RemapAction,
    isAssistantSet: Boolean,
    hasAdbPermission: Boolean = false,
    onTest: () -> Unit,
    onSetDefault: () -> Unit
) {
    // Resolve icon: app/activity icon drawable, or vector icon for quick toggles
    val appIconDrawable: Drawable? = remember(action.id) {
        if (action.type == ActionType.OPEN_APP || action.type == ActionType.LAUNCH_ACTIVITY) {
            try {
                action.packageName?.let { context.packageManager.getApplicationIcon(it) }
            } catch (e: Exception) {
                null
            }
        } else null
    }
    val toggleIcon: ImageVector? = remember(action.id) {
        if (action.type == ActionType.QUICK_TOGGLE) getToggleIcon(action.toggleKey)
        else null
    }

    val subtitleText = if (action.requiresAdb && hasAdbPermission) "" else action.subtitle

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon: app icon bitmap OR toggle vector icon
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        when {
                            appIconDrawable != null -> Image(
                                bitmap = appIconDrawable.toBitmap(96, 96).asImageBitmap(),
                                contentDescription = action.title,
                                modifier = Modifier.size(32.dp)
                            )
                            toggleIcon != null -> Icon(
                                imageVector = toggleIcon,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                            else -> Icon(
                                imageVector = AppIcons.Power,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "TRIGGER ACTION",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = action.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (subtitleText.isNotBlank()) {
                        Text(
                            text = subtitleText,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
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

                if (!isAssistantSet) {
                    FilledTonalButton(
                        onClick = onSetDefault,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Text("Set as Assistant")
                    }
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
