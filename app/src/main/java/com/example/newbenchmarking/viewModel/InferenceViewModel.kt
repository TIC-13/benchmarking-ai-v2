package com.example.newbenchmarking.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.newbenchmarking.interfaces.InferenceParams
import com.example.newbenchmarking.interfaces.models
class InferenceViewModel : ViewModel() {
    // Initial values for InferenceParams
    private val _inferenceParams = MutableLiveData(InferenceParams(
        model = models[0],
        numImages = 50,
        numThreads = 1,
        useGPU = false,
        useNNAPI = false
    ))
    val inferenceParams: LiveData<InferenceParams> = _inferenceParams

    fun updateInferenceParams(newParams: InferenceParams) {
        _inferenceParams.value = newParams
    }
}
