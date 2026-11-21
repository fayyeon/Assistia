package com.powerhold.app.ui.screens

import android.app.role.RoleManager
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.powerhold.app.data.ActionType
import com.powerhold.app.ui.MainViewModel
import com.powerhold.app.ui.theme.AppIcons
import com.powerhold.app.ui.theme.PowerholdSerifFontFamily
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
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

    val isSystemDark = isSystemInDarkTheme()
    val isDarkActive = themeMode == "DARK" || (themeMode == "SYSTEM" && isSystemDark)

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

    // Handle back button to close search mode smoothly
    BackHandler(enabled = showSearch) {
        showSearch = false
        viewModel.setSearchQuery("")
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
            isSystemDark = isSystemDark,
            onThemeSelect = { viewModel.setThemeMode(it) },
            onAmoledToggle = { viewModel.setAmoledMode(it) },
            onDismiss = { showThemeDialog = false }
        )
    }

    // Samsung One UI upper 50% screen viewing area dimensions
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val maxHeaderHeight = (screenHeight * 0.48f).coerceAtLeast(320.dp)
    val maxHeaderHeightPx = with(LocalDensity.current) { maxHeaderHeight.toPx() }
    var headerOffsetPx by remember { mutableFloatStateOf(0f) }

    val nestedScrollConnection = remember(maxHeaderHeightPx) {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                // When scrolling DOWN (finger drags up, delta < 0), collapse the upper 50% header
                if (delta < 0 && headerOffsetPx > -maxHeaderHeightPx) {
                    val prev = headerOffsetPx
                    headerOffsetPx = (headerOffsetPx + delta).coerceIn(-maxHeaderHeightPx, 0f)
                    val consumed = headerOffsetPx - prev
                    return Offset(0f, consumed)
                }
                return Offset.Zero
            }

            override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                // When scrolling UP (finger drags down, delta > 0) and content reached top, expand header
                if (delta > 0 && headerOffsetPx < 0f) {
                    val prev = headerOffsetPx
                    headerOffsetPx = (headerOffsetPx + delta).coerceIn(-maxHeaderHeightPx, 0f)
                    val consumed = headerOffsetPx - prev
                    return Offset(0f, consumed)
                }
                return Offset.Zero
            }
        }
    }

    // Root Surface guarantees solid, correct theme background across light, dark, and AMOLED modes
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(nestedScrollConnection)
        ) {
            // 1. Content: HorizontalPager with content placed below the 50% line when at top
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val tabPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = if (showSearch) 12.dp else (maxHeaderHeight + 8.dp),
                    bottom = 142.dp
                )
                when (page) {
                    0 -> QuickTogglesTab(
                        toggles = quickToggles,
                        selectedAction = selectedAction,
                        hasAdbPermission = hasAdbPermission,
                        contentPadding = tabPadding,
                        onSelectAction = { viewModel.selectAction(it) },
                        onTestAction = { viewModel.testAction(context, it) }
                    )
                    1 -> InstalledAppsTab(
                        apps = apps,
                        isLoading = isLoadingApps,
                        selectedAction = selectedAction,
                        contentPadding = tabPadding,
                        onSelectAction = { viewModel.selectAction(it) },
                        onTestAction = { viewModel.testAction(context, it) }
                    )
                    2 -> AppActivitiesTab(
                        activities = activities,
                        isLoading = isLoadingActivities,
                        selectedAction = selectedAction,
                        contentPadding = tabPadding,
                        onSelectAction = { viewModel.selectAction(it) },
                        onTestAction = { viewModel.testAction(context, it) }
                    )
                }
            }

            // 2. Samsung One UI 50% Upper Viewing Area:
            // "Powerhold" title at top-left corner (large 42sp serif font),
            // spacious viewing area, and theme paintbrush button at bottom-right of this upper area.
            // Smoothly collapses on scroll down; completely hidden when search is active.
            if (!showSearch) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(maxHeaderHeight)
                        .offset { IntOffset(0, headerOffsetPx.roundToInt()) }
                        .padding(horizontal = 24.dp)
                ) {
                    // "Powerhold" title in serif font at top-left corner (large 42sp, clicking opens About dialog)
                    Text(
                        text = "Powerhold",
                        fontFamily = PowerholdSerifFontFamily,
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Normal,
                        color = if (isDarkActive) Color.White else Color.Black,
                        modifier = Modifier
                            .padding(top = 40.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                showAboutDialog = true
                            }
                    )

                    // Spacious upper 50% area
                    Spacer(Modifier.weight(1f))

                    // Theme button located on the bottom-right of the upper 50% area (just above content)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(
                            onClick = { showThemeDialog = true },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = AppIcons.Paintbrush,
                                contentDescription = "Theme Settings",
                                tint = if (isDarkActive) Color.White else Color.Black,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }
                }
            }

            // 3. Docked Bottom Bar: Dynamic Test Trigger button + NavigationBar
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 3.dp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
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

            // 4. Floating Action Buttons & Search Bar Layer
            // Automatically moves with user's keyboard height (imePadding).
            // When search is active, the search bar appears above the keyboard,
            // and the Search/X button + ADB button float directly above the search bar.
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .fillMaxWidth()
                    .imePadding()
                    .padding(bottom = if (showSearch) 8.dp else 142.dp),
                horizontalAlignment = Alignment.End
            ) {
                // Floating Action Buttons (ADB setup & Search / Close)
                Column(
                    modifier = Modifier.padding(end = 16.dp, bottom = if (showSearch) 8.dp else 0.dp),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Secondary ADB Setup FAB directly above Search FAB, hidden once permission granted
                    AnimatedVisibility(
                        visible = !hasAdbPermission,
                        enter = fadeIn(),
                        exit = fadeOut()
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

                    // Search Floating Action Button (switches to Close 'X' when search is open)
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

                // Search Bar (slides in right above keyboard/bottom, with autofocus)
                AnimatedVisibility(
                    visible = showSearch,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(20.dp),
                        tonalElevation = 6.dp,
                        shadowElevation = 8.dp,
                        color = MaterialTheme.colorScheme.surface
                    ) {
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

/**
 * Visual Theme Selection Dialog designed to match Concept Pic 2:
 * - Top-right "X" close button
 * - Horizontal layout with 3 visual options:
 *   1. Device Theme (B/W split circle + RadioButton + "Device Theme")
 *   2. Light (White circle + RadioButton + "Light")
 *   3. Dark (Grey/black circle as specified + RadioButton + "Dark")
 * - Centered "Pure Black" checkbox at bottom (pure white text in dark mode, pure black in light mode)
 */
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
    val pureTextColor = if (isDarkActive) Color.White else Color.Black

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = if (isAmoled && isDarkActive) Color.Black else if (isDarkActive) Color(0xFF1E1E1E) else Color.White,
            border = BorderStroke(
                1.dp,
                if (isDarkActive) Color.White.copy(alpha = 0.15f) else Color.Black.copy(alpha = 0.1f)
            ),
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top-right close "X" button (Pic 2)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = pureTextColor
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Three horizontal theme options (Pic 2)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Top
                ) {
                    // Option 1: Device Theme (B/W split circle)
                    ThemeOptionItem(
                        title = "Device\nTheme",
                        isSelected = currentTheme == "SYSTEM",
                        textColor = pureTextColor,
                        onClick = { onThemeSelect("SYSTEM") }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .border(1.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), CircleShape)
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                // Left half: Light
                                drawArc(
                                    color = Color.White,
                                    startAngle = 90f,
                                    sweepAngle = 180f,
                                    useCenter = true
                                )
                                // Right half: Dark
                                drawArc(
                                    color = Color(0xFF262626),
                                    startAngle = 270f,
                                    sweepAngle = 180f,
                                    useCenter = true
                                )
                            }
                        }
                    }

                    // Option 2: Light (Solid white circle)
                    ThemeOptionItem(
                        title = "Light\n",
                        isSelected = currentTheme == "LIGHT",
                        textColor = pureTextColor,
                        onClick = { onThemeSelect("LIGHT") }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .border(1.5.dp, Color(0xFFCCCCCC), CircleShape)
                        )
                    }

                    // Option 3: Dark (Grey if set to pure black, black if not set to pure black, Pic 2)
                    ThemeOptionItem(
                        title = "Dark\n",
                        isSelected = currentTheme == "DARK",
                        textColor = pureTextColor,
                        onClick = { onThemeSelect("DARK") }
                    ) {
                        val circleColor = if (isAmoled) Color(0xFF6E6E6E) else Color(0xFF1A1A1A)
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(circleColor)
                                .border(1.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), CircleShape)
                        )
                    }
                }

                Spacer(Modifier.height(28.dp))

                // Pure Black Checkbox (Pic 2)
                // Text is purely white on dark mode and purely black on light mode
                val isCheckboxEnabled = isDarkActive
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(enabled = isCheckboxEnabled) { onAmoledToggle(!isAmoled) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Checkbox(
                        checked = isAmoled && isCheckboxEnabled,
                        onCheckedChange = { if (isCheckboxEnabled) onAmoledToggle(it) },
                        enabled = isCheckboxEnabled,
                        colors = CheckboxDefaults.colors(
                            checkedColor = pureTextColor,
                            checkmarkColor = if (isDarkActive) Color.Black else Color.White,
                            uncheckedColor = pureTextColor.copy(alpha = 0.6f)
                        )
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Pure Black",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = if (isCheckboxEnabled) pureTextColor else pureTextColor.copy(alpha = 0.35f)
                    )
                }

                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun ThemeOptionItem(
    title: String,
    isSelected: Boolean,
    textColor: Color,
    onClick: () -> Unit,
    icon: @Composable () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        icon()
        Spacer(Modifier.height(10.dp))
        RadioButton(
            selected = isSelected,
            onClick = onClick
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            textAlign = TextAlign.Center,
            color = textColor
        )
    }
}
