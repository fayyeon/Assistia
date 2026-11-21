package com.powerhold.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "powerhold_prefs")

class PreferencesManager(private val context: Context) {

    private object Keys {
        val ACTION_ID = stringPreferencesKey("action_id")
        val ACTION_TYPE = stringPreferencesKey("action_type")
        val ACTION_TITLE = stringPreferencesKey("action_title")
        val ACTION_SUBTITLE = stringPreferencesKey("action_subtitle")
        val PACKAGE_NAME = stringPreferencesKey("package_name")
        val ACTIVITY_NAME = stringPreferencesKey("activity_name")
        val TOGGLE_KEY = stringPreferencesKey("toggle_key")
        val REQUIRES_ADB = booleanPreferencesKey("requires_adb")
        val REQUIRES_PERMISSION = stringPreferencesKey("requires_permission")
        val IS_NON_ROOT_DIRECT = booleanPreferencesKey("is_non_root_direct")
        val TORCH_STATE = booleanPreferencesKey("torch_state")
        val AMOLED_MODE = booleanPreferencesKey("amoled_mode")
        val THEME_MODE = stringPreferencesKey("theme_mode")
    }

    val selectedActionFlow: Flow<RemapAction> = context.dataStore.data.map { prefs ->
        val id = prefs[Keys.ACTION_ID] ?: RemapAction.DEFAULT.id
        val typeStr = prefs[Keys.ACTION_TYPE] ?: RemapAction.DEFAULT.type.name
        val type = try {
            ActionType.valueOf(typeStr)
        } catch (e: Exception) {
            RemapAction.DEFAULT.type
        }

        RemapAction(
            id = id,
            type = type,
            title = prefs[Keys.ACTION_TITLE] ?: RemapAction.DEFAULT.title,
            subtitle = prefs[Keys.ACTION_SUBTITLE] ?: RemapAction.DEFAULT.subtitle,
            packageName = prefs[Keys.PACKAGE_NAME],
            activityName = prefs[Keys.ACTIVITY_NAME],
            toggleKey = prefs[Keys.TOGGLE_KEY] ?: RemapAction.DEFAULT.toggleKey,
            requiresAdb = prefs[Keys.REQUIRES_ADB] ?: false,
            requiresPermission = prefs[Keys.REQUIRES_PERMISSION],
            isNonRootDirect = prefs[Keys.IS_NON_ROOT_DIRECT] ?: true
        )
    }

    suspend fun getSelectedAction(): RemapAction {
        return selectedActionFlow.first()
    }

    suspend fun setSelectedAction(action: RemapAction) {
        context.dataStore.edit { prefs ->
            prefs[Keys.ACTION_ID] = action.id
            prefs[Keys.ACTION_TYPE] = action.type.name
            prefs[Keys.ACTION_TITLE] = action.title
            prefs[Keys.ACTION_SUBTITLE] = action.subtitle
            action.packageName?.let { prefs[Keys.PACKAGE_NAME] = it } ?: prefs.remove(Keys.PACKAGE_NAME)
            action.activityName?.let { prefs[Keys.ACTIVITY_NAME] = it } ?: prefs.remove(Keys.ACTIVITY_NAME)
            action.toggleKey?.let { prefs[Keys.TOGGLE_KEY] = it } ?: prefs.remove(Keys.TOGGLE_KEY)
            prefs[Keys.REQUIRES_ADB] = action.requiresAdb
            action.requiresPermission?.let { prefs[Keys.REQUIRES_PERMISSION] = it } ?: prefs.remove(Keys.REQUIRES_PERMISSION)
            prefs[Keys.IS_NON_ROOT_DIRECT] = action.isNonRootDirect
        }
    }

    suspend fun getTorchState(): Boolean {
        return context.dataStore.data.first()[Keys.TORCH_STATE] ?: false
    }

    suspend fun setTorchState(isOn: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.TORCH_STATE] = isOn
        }
    }

    val amoledModeFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.AMOLED_MODE] ?: false
    }

    suspend fun setAmoledMode(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.AMOLED_MODE] = enabled
        }
    }

    val themeModeFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[Keys.THEME_MODE] ?: "SYSTEM"
    }

    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.THEME_MODE] = mode
        }
    }
}
