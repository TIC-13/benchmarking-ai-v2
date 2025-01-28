package com.example.newbenchmarking.utils

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.askStoragePermissionDataStore by preferencesDataStore(name = "askStoragePermission")

val BOOLEAN_KEY = booleanPreferencesKey("permission")

suspend fun setAskStoragePermission(context: Context, value: Boolean) {
    context.askStoragePermissionDataStore.edit { preferences ->
        preferences[BOOLEAN_KEY] = value
    }
}

fun getAskStoragePermissionFlow(context: Context): Flow<Boolean> {
    return context.askStoragePermissionDataStore.data
        .map { preferences ->
            preferences[BOOLEAN_KEY] ?: true
        }
}

@Composable
fun useAskStoragePermission(): Boolean? {
    val context = LocalContext.current
    val permissionFlow = remember { getAskStoragePermissionFlow(context) }
    val permission by permissionFlow.collectAsState(initial = null)

    return permission
}