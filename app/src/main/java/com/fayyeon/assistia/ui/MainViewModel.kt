package com.fayyeon.assistia.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fayyeon.assistia.AssistiaApplication
import com.fayyeon.assistia.assistant.ActionExecutor
import com.fayyeon.assistia.data.ActionType
import com.fayyeon.assistia.data.AppListRepository
import com.fayyeon.assistia.data.PreferencesManager
import com.fayyeon.assistia.data.QuickToggleRepository
import com.fayyeon.assistia.data.RemapAction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val prefsManager: PreferencesManager = AssistiaApplication.instance.preferencesManager

    val selectedAction: StateFlow<RemapAction> = prefsManager.selectedActionFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), RemapAction.DEFAULT)

    val isAmoledMode: StateFlow<Boolean> = prefsManager.amoledModeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val themeMode: StateFlow<String> = prefsManager.themeModeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "SYSTEM")

    val savedWebsiteUrl: StateFlow<String> = prefsManager.savedWebsiteUrlFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _installedApps = MutableStateFlow<List<RemapAction>>(emptyList())
    val isLoadingApps = MutableStateFlow(false)

    private val _appActivities = MutableStateFlow<List<RemapAction>>(emptyList())
    val isLoadingActivities = MutableStateFlow(false)

    // Filtered lists combining search query and sorting
    val filteredQuickToggles: StateFlow<List<RemapAction>> = combine(
        _searchQuery
    ) { (query) ->
        val list = QuickToggleRepository.allToggles
        if (query.isBlank()) list
        else list.filter {
            it.title.contains(query, ignoreCase = true) ||
            it.subtitle.contains(query, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), QuickToggleRepository.allToggles)

    val filteredInstalledApps: StateFlow<List<RemapAction>> = combine(
        _installedApps, _searchQuery
    ) { apps, query ->
        if (query.isBlank()) apps
        else apps.filter {
            it.title.contains(query, ignoreCase = true) ||
            (it.packageName ?: "").contains(query, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredAppActivities: StateFlow<List<RemapAction>> = combine(
        _appActivities, _searchQuery
    ) { activities, query ->
        if (query.isBlank()) activities
        else activities.filter {
            it.title.contains(query, ignoreCase = true) ||
            (it.activityName ?: "").contains(query, ignoreCase = true) ||
            (it.packageName ?: "").contains(query, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSelectedTab(tab: Int) {
        _selectedTab.value = tab
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setAmoledMode(enabled: Boolean) {
        viewModelScope.launch {
            prefsManager.setAmoledMode(enabled)
        }
    }

    fun setThemeMode(mode: String) {
        viewModelScope.launch {
            prefsManager.setThemeMode(mode)
        }
    }

    fun selectAction(action: RemapAction) {
        viewModelScope.launch {
            prefsManager.setSelectedAction(action)
        }
    }

    fun selectWebsite(rawUrl: String) {
        val trimmed = rawUrl.trim()
        if (trimmed.isBlank()) return
        val normalized = if (trimmed.startsWith("http://", ignoreCase = true) ||
            trimmed.startsWith("https://", ignoreCase = true)
        ) {
            trimmed
        } else {
            "https://$trimmed"
        }
        val hostLabel = try {
            android.net.Uri.parse(normalized).host?.removePrefix("www.")?.ifBlank { normalized } ?: normalized
        } catch (_: Exception) {
            normalized
        }
        val websiteAction = RemapAction(
            id = "open_website",
            type = ActionType.OPEN_WEBSITE,
            title = hostLabel,
            subtitle = normalized,
            url = normalized,
            isNonRootDirect = true
        )
        viewModelScope.launch {
            prefsManager.setSavedWebsiteUrl(normalized)
            prefsManager.setSelectedAction(websiteAction)
        }
    }

    fun testAction(context: Context, action: RemapAction? = null) {
        viewModelScope.launch {
            if (action != null) {
                // Temporarily test specific action
                prefsManager.setSelectedAction(action)
            }
            ActionExecutor.execute(context)
        }
    }

    fun loadApps(context: Context) {
        if (_installedApps.value.isNotEmpty() || isLoadingApps.value) return
        viewModelScope.launch {
            isLoadingApps.value = true
            _installedApps.value = AppListRepository.getInstalledApps(context)
            isLoadingApps.value = false
        }
    }

    fun loadActivities(context: Context) {
        if (_appActivities.value.isNotEmpty() || isLoadingActivities.value) return
        viewModelScope.launch {
            isLoadingActivities.value = true
            _appActivities.value = AppListRepository.getAppActivities(context)
            isLoadingActivities.value = false
        }
    }
}
