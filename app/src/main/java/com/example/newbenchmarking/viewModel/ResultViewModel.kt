package com.example.newbenchmarking.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.newbenchmarking.interfaces.InferenceParams
import com.example.newbenchmarking.interfaces.InferenceResult
import com.example.newbenchmarking.interfaces.models
class ResultViewModel : ViewModel() {
    private val _inferenceResult = MutableLiveData(
        InferenceResult(
            cpuAverage = 0F,
            gpuAverage =  0,
            ramConsumedAverage =  0F,
            inferenceTimeAverage = 0L,
            loadTime = 0L,
    ))
    val inferenceResult: LiveData<InferenceResult> = _inferenceResult

    fun updateInferenceResult(newParams: InferenceResult) {
        _inferenceResult.value = newParams
    }
}
