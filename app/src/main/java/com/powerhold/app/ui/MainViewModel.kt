package com.powerhold.app.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.powerhold.app.PowerholdApplication
import com.powerhold.app.assistant.ActionExecutor
import com.powerhold.app.data.AppListRepository
import com.powerhold.app.data.PreferencesManager
import com.powerhold.app.data.QuickToggleRepository
import com.powerhold.app.data.RemapAction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val prefsManager: PreferencesManager = PowerholdApplication.instance.preferencesManager

    val selectedAction: StateFlow<RemapAction> = prefsManager.selectedActionFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), RemapAction.DEFAULT)

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

    fun selectAction(action: RemapAction) {
        viewModelScope.launch {
            prefsManager.setSelectedAction(action)
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
