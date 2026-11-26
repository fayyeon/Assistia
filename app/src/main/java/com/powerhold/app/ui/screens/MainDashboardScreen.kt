package com.powerhold.app.ui.screens

import android.app.role.RoleManager
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
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
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.powerhold.app.data.ActionType
import com.powerhold.app.ui.MainViewModel
import com.powerhold.app.ui.theme.AppIcons
import com.powerhold.app.ui.theme.PowerholdSerifFontFamily
import kotlinx.coroutines.launch

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
    val savedWebsiteUrl by viewModel.savedWebsiteUrl.collectAsState()

    val quickToggles by viewModel.filteredQuickToggles.collectAsState()
    val apps by viewModel.filteredInstalledApps.collectAsState()
    val activities by viewModel.filteredAppActivities.collectAsState()

    val isLoadingApps by viewModel.isLoadingApps.collectAsState()
    val isLoadingActivities by viewModel.isLoadingActivities.collectAsState()

    var showSearch by remember { mutableStateOf(false) }
    var showAdbDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }

    val isSystemDark = isSystemInDarkTheme()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    var isAssistantSet by remember { mutableStateOf(false) }
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

    // 5-page HorizontalPager: 0=Actions, 1=Website, 2=Apps, 3=Activities, 4=About
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 5 })
    val isSearchablePage = pagerState.currentPage in listOf(0, 2, 3)

    LaunchedEffect(pagerState.currentPage) {
        viewModel.setSelectedTab(pagerState.currentPage)
        if (!isSearchablePage && showSearch) {
            showSearch = false
            viewModel.setSearchQuery("")
            keyboardController?.hide()
        }
        if (pagerState.currentPage == 2) {
            viewModel.loadApps(context)
        } else if (pagerState.currentPage == 3) {
            viewModel.loadActivities(context)
        }
    }

    val pageTitle = when (pagerState.currentPage) {
        0 -> "Actions"
        1 -> "Website"
        2 -> "Apps"
        3 -> "Activities"
        4 -> "About"
        else -> "Actions"
    }

    val searchPlaceholder = when (pagerState.currentPage) {
        0 -> "Search actions..."
        2 -> "Search apps..."
        3 -> "Search activities..."
        else -> "Search..."
    }

    val testButtonLabel = remember(selectedAction) {
        when (selectedAction.type) {
            ActionType.OPEN_APP -> "Test Trigger ${selectedAction.title}"
            ActionType.LAUNCH_ACTIVITY -> {
                val appName = selectedAction.subtitle.ifBlank { selectedAction.title }
                "Test Trigger $appName Activity"
            }
            ActionType.QUICK_TOGGLE -> "Test Trigger ${selectedAction.title}"
            ActionType.OPEN_WEBSITE -> "Test Trigger ${selectedAction.title}"
        }
    }

    LaunchedEffect(showSearch) {
        if (showSearch) {
            focusRequester.requestFocus()
            keyboardController?.show()
        } else {
            keyboardController?.hide()
        }
    }

    BackHandler(enabled = showSearch) {
        showSearch = false
        viewModel.setSearchQuery("")
    }

    if (showAdbDialog) {
        AdbHelperDialog(onDismiss = { showAdbDialog = false })
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

    val navTabs = remember {
        listOf(
            NavTabItem("Actions", AppIcons.Widgets, AppIcons.WidgetsOutlined, 0),
            NavTabItem("Website", AppIcons.Language, AppIcons.LanguageOutlined, 1),
            NavTabItem("Apps", AppIcons.Apps, AppIcons.AppsOutlined, 2),
            NavTabItem("Activities", AppIcons.Code, AppIcons.CodeOutlined, 3),
            NavTabItem("About", AppIcons.Info, AppIcons.InfoOutlined, 4)
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (isLandscape) {
            // ================= LANDSCAPE LAYOUT =================
            Row(modifier = Modifier.fillMaxSize()) {
                // Main Content + Floating Overlay
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Fixed compact top bar: title stays small on every page, never scales on scroll
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .statusBarsPadding()
                                .height(52.dp)
                                .padding(horizontal = 24.dp)
                        ) {
                            Text(
                                text = pageTitle,
                                fontFamily = PowerholdSerifFontFamily,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Normal,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.align(Alignment.CenterStart)
                            )

                            if (pagerState.currentPage != 1 && pagerState.currentPage != 4) {
                                IconButton(
                                    onClick = { showThemeDialog = true },
                                    modifier = Modifier
                                        .align(Alignment.CenterEnd)
                                        .size(48.dp)
                                ) {
                                    Icon(
                                        imageVector = AppIcons.Paintbrush,
                                        contentDescription = "Theme Settings",
                                        tint = MaterialTheme.colorScheme.onBackground,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }

                        DashboardPager(
                            pagerState = pagerState,
                            tabPadding = PaddingValues(
                                start = 16.dp,
                                end = 16.dp,
                                top = 8.dp,
                                bottom = 92.dp
                            ),
                            quickToggles = quickToggles,
                            apps = apps,
                            activities = activities,
                            selectedAction = selectedAction,
                            hasAdbPermission = hasAdbPermission,
                            savedWebsiteUrl = savedWebsiteUrl,
                            isLoadingApps = isLoadingApps,
                            isLoadingActivities = isLoadingActivities,
                            onSelectAction = { viewModel.selectAction(it) },
                            onTestAction = { viewModel.testAction(context, it) },
                            onOpenThemeDialog = { showThemeDialog = true },
                            onSelectWebsite = { viewModel.selectWebsite(it) },
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        )
                    }

                    // Floating ADB, Search, and Test Trigger buttons (landscape)
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .fillMaxWidth()
                            .windowInsetsPadding(WindowInsets.ime.union(WindowInsets.navigationBars))
                            .padding(bottom = 12.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        SearchAndAdbControls(
                            showSearch = showSearch,
                            isSearchablePage = isSearchablePage,
                            showAdbFab = !hasAdbPermission && pagerState.currentPage == 0,
                            searchQuery = searchQuery,
                            searchPlaceholder = searchPlaceholder,
                            focusRequester = focusRequester,
                            onQueryChange = { viewModel.setSearchQuery(it) },
                            onOpenSearch = { showSearch = true },
                            onCloseSearch = { showSearch = false },
                            onOpenAdb = { showAdbDialog = true },
                            onHideKeyboard = { keyboardController?.hide() }
                        )

                        // Floating Test Trigger button placed below the search button (hidden while typing in search)
                        if (!showSearch) {
                            Spacer(Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.padding(end = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (!isAssistantSet) {
                                    Surface(
                                        onClick = onOpenAssistantSettings,
                                        shape = RoundedCornerShape(16.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        shadowElevation = 4.dp,
                                        modifier = Modifier.height(56.dp)
                                    ) {
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier.padding(horizontal = 16.dp)
                                        ) {
                                            Text(
                                                text = "Set as Assistant",
                                                fontWeight = FontWeight.SemiBold,
                                                style = MaterialTheme.typography.labelLarge
                                            )
                                        }
                                    }
                                }

                                Surface(
                                    onClick = { viewModel.testAction(context) },
                                    shape = RoundedCornerShape(16.dp),
                                    color = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary,
                                    shadowElevation = 6.dp,
                                    modifier = Modifier
                                        .height(56.dp)
                                        .widthIn(min = 56.dp, max = 340.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    ) {
                                        Icon(
                                            imageVector = AppIcons.PlayArrow,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Text(
                                            text = testButtonLabel,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            fontWeight = FontWeight.SemiBold,
                                            style = MaterialTheme.typography.labelLarge
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Right-side vertical navigation bar
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 3.dp,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Row(modifier = Modifier.fillMaxHeight()) {
                        VerticalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
                            thickness = 1.dp
                        )
                        Column(
                            modifier = Modifier
                                .fillMaxHeight()
                                .navigationBarsPadding()
                                .displayCutoutPadding()
                                .width(64.dp),
                            verticalArrangement = Arrangement.SpaceEvenly,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            navTabs.forEach { tab ->
                                NavBarIconItem(
                                    tab = tab,
                                    isSelected = pagerState.currentPage == tab.index,
                                    isVerticalBar = true,
                                    onClick = {
                                        coroutineScope.launch {
                                            pagerState.animateScrollToPage(tab.index)
                                        }
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // ================= PORTRAIT LAYOUT =================
            val screenHeight = configuration.screenHeightDp.dp
            val expandedHeaderHeight = (screenHeight * 0.40f).coerceAtLeast(250.dp)
            val collapsedHeaderHeight = 52.dp
            val collapseRangePx = with(LocalDensity.current) {
                (expandedHeaderHeight - collapsedHeaderHeight).toPx().coerceAtLeast(1f)
            }
            var headerOffsetPx by remember { mutableFloatStateOf(0f) }

            val scrollCollapseFraction = (-headerOffsetPx / collapseRangePx).coerceIn(0f, 1f)
            val isNonScrollHeader = showSearch || pagerState.currentPage == 1 || pagerState.currentPage == 4
            val targetCollapseFraction = when {
                showSearch -> 1f
                pagerState.currentPage == 1 || pagerState.currentPage == 4 -> 0f
                else -> scrollCollapseFraction
            }
            val effectiveCollapseFraction by animateFloatAsState(
                targetValue = targetCollapseFraction,
                animationSpec = if (isNonScrollHeader) {
                    tween(durationMillis = 220, easing = FastOutSlowInEasing)
                } else {
                    snap()
                },
                label = "collapseFraction"
            )
            val currentHeaderHeight by animateDpAsState(
                targetValue = when {
                    showSearch -> collapsedHeaderHeight
                    pagerState.currentPage == 4 -> 72.dp
                    else -> lerp(expandedHeaderHeight, collapsedHeaderHeight, effectiveCollapseFraction)
                },
                animationSpec = if (isNonScrollHeader) {
                    tween(durationMillis = 220, easing = FastOutSlowInEasing)
                } else {
                    snap()
                },
                label = "headerHeight"
            )

            val nestedScrollConnection = remember(collapseRangePx) {
                object : NestedScrollConnection {
                    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                        val delta = available.y
                        if (delta < 0 && headerOffsetPx > -collapseRangePx) {
                            val prev = headerOffsetPx
                            headerOffsetPx = (headerOffsetPx + delta).coerceIn(-collapseRangePx, 0f)
                            return Offset(0f, headerOffsetPx - prev)
                        }
                        return Offset.Zero
                    }

                    override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
                        val delta = available.y
                        if (delta > 0 && headerOffsetPx < 0f) {
                            val prev = headerOffsetPx
                            headerOffsetPx = (headerOffsetPx + delta).coerceIn(-collapseRangePx, 0f)
                            return Offset(0f, headerOffsetPx - prev)
                        }
                        return Offset.Zero
                    }
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .nestedScroll(nestedScrollConnection)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .height(currentHeaderHeight)
                            .padding(horizontal = 24.dp)
                    ) {
                        Text(
                            text = pageTitle,
                            fontFamily = PowerholdSerifFontFamily,
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(top = lerp(16.dp, 8.dp, effectiveCollapseFraction))
                                .graphicsLayer {
                                    val titleScale = lerp(1f, 0.55f, effectiveCollapseFraction)
                                    scaleX = titleScale
                                    scaleY = titleScale
                                    transformOrigin = TransformOrigin(0f, 0.5f)
                                }
                        )

                        if (pagerState.currentPage != 1 && pagerState.currentPage != 4) {
                            IconButton(
                                onClick = { showThemeDialog = true },
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(bottom = lerp(8.dp, 2.dp, effectiveCollapseFraction))
                                    .size(48.dp)
                            ) {
                                Icon(
                                    imageVector = AppIcons.Paintbrush,
                                    contentDescription = "Theme Settings",
                                    tint = MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }
                    }

                    DashboardPager(
                        pagerState = pagerState,
                        tabPadding = PaddingValues(
                            start = 16.dp,
                            end = 16.dp,
                            top = 8.dp,
                            bottom = 160.dp
                        ),
                        quickToggles = quickToggles,
                        apps = apps,
                        activities = activities,
                        selectedAction = selectedAction,
                        hasAdbPermission = hasAdbPermission,
                        savedWebsiteUrl = savedWebsiteUrl,
                        isLoadingApps = isLoadingApps,
                        isLoadingActivities = isLoadingActivities,
                        onSelectAction = { viewModel.selectAction(it) },
                        onTestAction = { viewModel.testAction(context, it) },
                        onOpenThemeDialog = { showThemeDialog = true },
                        onSelectWebsite = { viewModel.selectWebsite(it) },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    )
                }

                // Docked Bottom Bar: FIXED at bottom (no imePadding, covered by keyboard when active)
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

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = { viewModel.testAction(context) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Icon(AppIcons.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = testButtonLabel,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontWeight = FontWeight.SemiBold,
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }

                            if (!isAssistantSet) {
                                FilledTonalButton(
                                    onClick = onOpenAssistantSettings,
                                    modifier = Modifier.height(38.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.filledTonalButtonColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                ) {
                                    Text(
                                        text = "Set as Assistant",
                                        fontWeight = FontWeight.SemiBold,
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .navigationBarsPadding()
                                .height(52.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            navTabs.forEach { tab ->
                                NavBarIconItem(
                                    tab = tab,
                                    isSelected = pagerState.currentPage == tab.index,
                                    isVerticalBar = false,
                                    onClick = {
                                        coroutineScope.launch {
                                            pagerState.animateScrollToPage(tab.index)
                                        }
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                )
                            }
                        }
                    }
                }

                // Floating ADB & Morphing Search layer (only moves with keyboard)
                val isImeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.ime.union(WindowInsets.navigationBars))
                        .padding(bottom = if (isImeVisible) 8.dp else 112.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    SearchAndAdbControls(
                        showSearch = showSearch,
                        isSearchablePage = isSearchablePage,
                        showAdbFab = !hasAdbPermission && pagerState.currentPage == 0,
                        searchQuery = searchQuery,
                        searchPlaceholder = searchPlaceholder,
                        focusRequester = focusRequester,
                        onQueryChange = { viewModel.setSearchQuery(it) },
                        onOpenSearch = { showSearch = true },
                        onCloseSearch = { showSearch = false },
                        onOpenAdb = { showAdbDialog = true },
                        onHideKeyboard = { keyboardController?.hide() }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DashboardPager(
    pagerState: PagerState,
    tabPadding: PaddingValues,
    quickToggles: List<com.powerhold.app.data.RemapAction>,
    apps: List<com.powerhold.app.data.RemapAction>,
    activities: List<com.powerhold.app.data.RemapAction>,
    selectedAction: com.powerhold.app.data.RemapAction,
    hasAdbPermission: Boolean,
    savedWebsiteUrl: String,
    isLoadingApps: Boolean,
    isLoadingActivities: Boolean,
    onSelectAction: (com.powerhold.app.data.RemapAction) -> Unit,
    onTestAction: (com.powerhold.app.data.RemapAction) -> Unit,
    onOpenThemeDialog: () -> Unit,
    onSelectWebsite: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    HorizontalPager(
        state = pagerState,
        modifier = modifier
    ) { page ->
        when (page) {
            0 -> QuickTogglesTab(
                toggles = quickToggles,
                selectedAction = selectedAction,
                hasAdbPermission = hasAdbPermission,
                contentPadding = tabPadding,
                onSelectAction = onSelectAction,
                onTestAction = onTestAction
            )
            1 -> WebsiteTab(
                savedUrl = savedWebsiteUrl,
                selectedAction = selectedAction,
                onOpenThemeDialog = onOpenThemeDialog,
                onSelectWebsite = onSelectWebsite
            )
            2 -> InstalledAppsTab(
                apps = apps,
                isLoading = isLoadingApps,
                selectedAction = selectedAction,
                contentPadding = tabPadding,
                onSelectAction = onSelectAction,
                onTestAction = onTestAction
            )
            3 -> AppActivitiesTab(
                activities = activities,
                isLoading = isLoadingActivities,
                selectedAction = selectedAction,
                contentPadding = tabPadding,
                onSelectAction = onSelectAction,
                onTestAction = onTestAction
            )
            4 -> AboutTab()
        }
    }
}

@Composable
private fun SearchAndAdbControls(
    showSearch: Boolean,
    isSearchablePage: Boolean,
    showAdbFab: Boolean,
    searchQuery: String,
    searchPlaceholder: String,
    focusRequester: FocusRequester,
    onQueryChange: (String) -> Unit,
    onOpenSearch: () -> Unit,
    onCloseSearch: () -> Unit,
    onOpenAdb: () -> Unit,
    onHideKeyboard: () -> Unit
) {
    AnimatedVisibility(
        visible = showAdbFab,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        FloatingActionButton(
            onClick = onOpenAdb,
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .padding(end = 16.dp, bottom = 10.dp)
                .size(48.dp)
        ) {
            Icon(
                imageVector = AppIcons.Terminal,
                contentDescription = "ADB Setup",
                modifier = Modifier.size(22.dp)
            )
        }
    }

    if (isSearchablePage || showSearch) {
        AnimatedContent(
            targetState = showSearch,
            transitionSpec = {
                (fadeIn(tween(200)) + expandHorizontally(
                    expandFrom = Alignment.End,
                    animationSpec = tween(250)
                )) togetherWith (fadeOut(tween(150)) + shrinkHorizontally(
                    shrinkTowards = Alignment.End,
                    animationSpec = tween(200)
                )) using SizeTransform(clip = false)
            },
            contentAlignment = Alignment.CenterEnd,
            label = "searchMorph"
        ) { isSearching ->
            if (isSearching) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(20.dp),
                    tonalElevation = 6.dp,
                    shadowElevation = 8.dp,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onQueryChange,
                        placeholder = { Text(searchPlaceholder) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    if (searchQuery.isNotEmpty()) {
                                        onQueryChange("")
                                    } else {
                                        onCloseSearch()
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Close search")
                            }
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { onHideKeyboard() }),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                        )
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 16.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    FloatingActionButton(
                        onClick = onOpenSearch,
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NavBarIconItem(
    tab: NavTabItem,
    isSelected: Boolean,
    isVerticalBar: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val iconScale by animateFloatAsState(
        targetValue = if (isSelected) 1.12f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "iconScale"
    )
    val pillSize by animateDpAsState(
        targetValue = if (isSelected) (if (isVerticalBar) 44.dp else 54.dp) else 0.dp,
        animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing),
        label = "pillSize"
    )

    Box(
        modifier = modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick
        ),
        contentAlignment = Alignment.Center
    ) {
        if (pillSize > 0.dp) {
            Box(
                modifier = Modifier
                    .width(pillSize)
                    .height(if (isVerticalBar) 36.dp else 30.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer)
            )
        }
        Icon(
            imageVector = if (isSelected) tab.filledIcon else tab.outlinedIcon,
            contentDescription = tab.label,
            tint = if (isSelected)
                MaterialTheme.colorScheme.onSecondaryContainer
            else
                MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .size(22.dp)
                .scale(iconScale)
        )
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

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Top
                ) {
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
                                drawArc(
                                    color = Color.White,
                                    startAngle = 90f,
                                    sweepAngle = 180f,
                                    useCenter = true
                                )
                                drawArc(
                                    color = Color(0xFF262626),
                                    startAngle = 270f,
                                    sweepAngle = 180f,
                                    useCenter = true
                                )
                            }
                        }
                    }

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
