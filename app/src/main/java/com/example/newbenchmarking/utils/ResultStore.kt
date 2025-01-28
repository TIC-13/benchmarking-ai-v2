package com.example.newbenchmarking.utils

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.newbenchmarking.interfaces.BenchmarkResult
import com.example.newbenchmarking.interfaces.InferenceParams
import com.example.newbenchmarking.interfaces.Model
import com.example.newbenchmarking.interfaces.toBenchmarkResult
import com.example.newbenchmarking.interfaces.toJson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.resultDataStore by preferencesDataStore("result_datastore")

suspend fun saveResultLocally(context: Context, result: BenchmarkResult) {
    val key = getResultKey(result.params)
    context.resultDataStore.edit { preferences ->
        preferences[key] = result.toJson()
    }
}

private fun getAllSavedResultsFlow(context: Context): Flow<List<BenchmarkResult>> {
    return context.resultDataStore.data.map { preferences ->
        preferences.asMap().values.mapNotNull { value ->
            (value as? String)?.toBenchmarkResult()
        }
    }
}

@Composable
fun getAllSavedResults(): List<BenchmarkResult> {

    val context = LocalContext.current
    val resultsFlow = remember { getAllSavedResultsFlow(context) }
    val results by resultsFlow.collectAsState(initial = emptyList())

    return results
}

fun getResultKey(params: InferenceParams) = stringPreferencesKey("result_${params.model}_${params.runMode}")
