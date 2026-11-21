package com.powerhold.app.ui.screens

import android.app.role.RoleManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.powerhold.app.data.ActionType
import com.powerhold.app.data.RemapAction
import com.powerhold.app.ui.MainViewModel
import com.powerhold.app.ui.theme.AppIcons
import com.powerhold.app.ui.theme.PowerholdSerifFontFamily
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainDashboardScreen(
    viewModel: MainViewModel,
    onOpenAssistantSettings: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    val selectedAction by viewModel.selectedAction.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isAmoledMode by viewModel.isAmoledMode.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()

    val quickToggles by viewModel.filteredQuickToggles.collectAsState()
    val apps by viewModel.filteredInstalledApps.collectAsState()
    val activities by viewModel.filteredAppActivities.collectAsState()

    val isLoadingApps by viewModel.isLoadingApps.collectAsState()
    val isLoadingActivities by viewModel.isLoadingActivities.collectAsState()

    var showSearch by remember { mutableStateOf(false) }
    var showAdbDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }

    // Track whether Powerhold is already set as default assistant
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

    LaunchedEffect(Unit) { checkPermissions() }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) checkPermissions()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // Horizontal pager for swiping between tabs (Task 9)
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 3 })

    LaunchedEffect(pagerState.currentPage) {
        viewModel.setSelectedTab(pagerState.currentPage)
        if (pagerState.currentPage == 1) {
            viewModel.loadApps(context)
        } else if (pagerState.currentPage == 2) {
            viewModel.loadActivities(context)
        }
    }

    // Dynamic search placeholder based on current tab (Task 3)
    val searchPlaceholder = when (pagerState.currentPage) {
        0 -> "Search actions..."
        1 -> "Search apps..."
        2 -> "Search activities..."
        else -> "Search..."
    }

    // Dynamic Test Trigger button text (Task 17)
    val testButtonLabel = remember(selectedAction) {
        when (selectedAction.type) {
            ActionType.OPEN_APP -> "Test Trigger ${selectedAction.title}"
            ActionType.LAUNCH_ACTIVITY -> {
                val appName = selectedAction.subtitle.ifBlank { selectedAction.title }
                "Test Trigger $appName Activity"
            }
            ActionType.QUICK_TOGGLE -> "Test Trigger ${selectedAction.title}"
        }
    }

    // Focus & keyboard auto-open on search toggle (Task 4)
    LaunchedEffect(showSearch) {
        if (showSearch) {
            focusRequester.requestFocus()
            keyboardController?.show()
        }
    }

    if (showAdbDialog) {
        AdbHelperDialog(onDismiss = { showAdbDialog = false })
    }

    if (showAboutDialog) {
        AboutAppDialog(onDismiss = { showAboutDialog = false })
    }

    if (showThemeDialog) {
        ThemeChooserDialog(
            currentTheme = themeMode,
            isAmoled = isAmoledMode,
            isSystemDark = isSystemInDarkTheme(),
            onThemeSelect = { viewModel.setThemeMode(it) },
            onAmoledToggle = { viewModel.setAmoledMode(it) },
            onDismiss = { showThemeDialog = false }
        )
    }

    // TopAppBar collapses on scroll down (Task 17)
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                scrollBehavior = scrollBehavior,
                title = {
                    // Powerhold text moved to top-left corner, larger in serif font, opens About dialog (Task 17)
                    Text(
                        text = "Powerhold",
                        fontFamily = PowerholdSerifFontFamily,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            showAboutDialog = true
                        }
                    )
                },
                actions = {
                    // Theme selector button on top-right (Task 17)
                    IconButton(onClick = { showThemeDialog = true }) {
                        Icon(
                            imageVector = AppIcons.Palette,
                            contentDescription = "Theme Settings",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.imePadding()
            ) {
                // Secondary ADB Setup FAB directly above Search FAB, hidden once permission granted (Task 10)
                AnimatedVisibility(
                    visible = !hasAdbPermission,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    FloatingActionButton(
                        onClick = { showAdbDialog = true },
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = AppIcons.Terminal,
                            contentDescription = "ADB Setup",
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                // Search Floating Action Button (Tasks 4, 10)
                FloatingActionButton(
                    onClick = {
                        showSearch = !showSearch
                        if (!showSearch) viewModel.setSearchQuery("")
                    },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        imageVector = if (showSearch) Icons.Default.Close else Icons.Default.Search,
                        contentDescription = if (showSearch) "Close search" else "Search"
                    )
                }
            }
        },
        bottomBar = {
            // Docked Test Trigger button directly above Actions, Apps, Activities navigation bar (Task 17)
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 3.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
                        thickness = 1.dp
                    )

                    // Test Trigger button bar (Task 17)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { viewModel.testAction(context) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(AppIcons.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = testButtonLabel,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        if (!isAssistantSet) {
                            FilledTonalButton(
                                onClick = onOpenAssistantSettings,
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            ) {
                                Text("Set as Assistant", fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    // Bottom navigation bar with animated outline -> filled icons & expanding pills (Tasks 2, 8, Pic 3)
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 0.dp
                    ) {
                        val navTabs = listOf(
                            NavTabItem("Actions", AppIcons.Widgets, AppIcons.WidgetsOutlined, 0),
                            NavTabItem("Apps", AppIcons.Apps, AppIcons.AppsOutlined, 1),
                            NavTabItem("Activities", AppIcons.Code, AppIcons.CodeOutlined, 2)
                        )

                        navTabs.forEach { tab ->
                            val isSelected = pagerState.currentPage == tab.index
                            val iconScale by animateFloatAsState(
                                targetValue = if (isSelected) 1.15f else 1.0f,
                                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                                label = "iconScale"
                            )

                            NavigationBarItem(
                                selected = isSelected,
                                onClick = {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(tab.index)
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = if (isSelected) tab.filledIcon else tab.outlinedIcon,
                                        contentDescription = tab.label,
                                        modifier = Modifier.scale(iconScale)
                                    )
                                },
                                label = {
                                    Text(
                                        text = tab.label,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Animated Search Bar with smooth slide-down animation (Tasks 3, 4)
            AnimatedVisibility(
                visible = showSearch,
                enter = slideInVertically(initialOffsetY = { -it / 2 }) + expandVertically() + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { -it / 2 }) + shrinkVertically() + fadeOut()
            ) {
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        placeholder = { Text(searchPlaceholder) },
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                        )
                    )
                }
            }

            // Swipeable HorizontalPager between Actions, Apps, and Activities (Task 9, Pic 4)
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
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

private data class NavTabItem(
    val label: String,
    val filledIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val outlinedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val index: Int
)

@Composable
private fun ThemeChooserDialog(
    currentTheme: String,
    isAmoled: Boolean,
    isSystemDark: Boolean,
    onThemeSelect: (String) -> Unit,
    onAmoledToggle: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    val isDarkActive = currentTheme == "DARK" || (currentTheme == "SYSTEM" && isSystemDark)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Theme",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                val themeOptions = listOf(
                    "SYSTEM" to "Device theme",
                    "LIGHT" to "Light",
                    "DARK" to "Dark"
                )

                themeOptions.forEach { (mode, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onThemeSelect(mode) }
                            .padding(horizontal = 8.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = currentTheme == mode,
                            onClick = { onThemeSelect(mode) }
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = if (currentTheme == mode) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }

                // When user chooses dark or device theme is dark mode, provide pure black toggle (Task 17)
                if (isDarkActive) {
                    Spacer(Modifier.height(4.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                    Spacer(Modifier.height(4.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onAmoledToggle(!isAmoled) }
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Pure black (AMOLED)",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "True black (#000000) for OLED screens",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = isAmoled,
                            onCheckedChange = { onAmoledToggle(it) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Done")
            }
        }
    )
}
