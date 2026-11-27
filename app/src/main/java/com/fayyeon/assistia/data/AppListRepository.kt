package com.fayyeon.assistia.data

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object AppListRepository {

    suspend fun getInstalledApps(context: Context): List<RemapAction> = withContext(Dispatchers.IO) {
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val resolveInfos = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pm.queryIntentActivities(
                intent,
                PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_ALL.toLong())
            )
        } else {
            @Suppress("DEPRECATION")
            pm.queryIntentActivities(intent, PackageManager.MATCH_ALL)
        }

        val actions = resolveInfos.mapNotNull { resolveInfo ->
            val packageName = resolveInfo.activityInfo.packageName
            val label = resolveInfo.loadLabel(pm).toString()
            RemapAction(
                id = "app_$packageName",
                type = ActionType.OPEN_APP,
                title = label,
                subtitle = packageName,
                packageName = packageName,
                isNonRootDirect = true
            )
        }.distinctBy { it.packageName }
            .sortedBy { it.title.lowercase() }

        actions
    }

    suspend fun getAppActivities(context: Context): List<RemapAction> = withContext(Dispatchers.IO) {
        val pm = context.packageManager
        val flags = PackageManager.GET_ACTIVITIES

        val packages = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pm.getInstalledPackages(PackageManager.PackageInfoFlags.of(flags.toLong()))
        } else {
            @Suppress("DEPRECATION")
            pm.getInstalledPackages(flags)
        }

        val activityActions = mutableListOf<RemapAction>()

        for (pkg in packages) {
            val appLabel = try {
                val appInfo = pkg.applicationInfo
                if (appInfo != null) pm.getApplicationLabel(appInfo).toString() else pkg.packageName
            } catch (e: Exception) {
                pkg.packageName
            }

            val activities = pkg.activities ?: continue
            for (activity in activities) {
                val fullActivityName = activity.name
                val simpleName = fullActivityName.substringAfterLast('.')
                val activityLabel = try {
                    val lbl = activity.loadLabel(pm).toString()
                    if (lbl.isNotBlank() && lbl != fullActivityName) lbl else simpleName
                } catch (e: Exception) {
                    simpleName
                }

                activityActions.add(
                    RemapAction(
                        id = "act_${pkg.packageName}_$fullActivityName",
                        type = ActionType.LAUNCH_ACTIVITY,
                        title = activityLabel,
                        subtitle = appLabel,
                        packageName = pkg.packageName,
                        activityName = fullActivityName,
                        isNonRootDirect = activity.exported
                    )
                )
            }
        }

        // Sort all app activities strictly in alphabetical order by their display title
        activityActions.sortedBy { it.title.lowercase() }
    }
}
