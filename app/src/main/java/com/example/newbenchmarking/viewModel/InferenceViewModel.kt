package com.example.newbenchmarking.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.newbenchmarking.interfaces.InferenceParams
import com.example.newbenchmarking.interfaces.models
class InferenceViewModel : ViewModel() {
    // Initial values for InferenceParams
    private val _inferenceParamsList = MutableLiveData(arrayListOf(
        InferenceParams(
            model = models[0],
            numImages = 50,
            numThreads = 1,
            useGPU = false,
            useNNAPI = false
        )
    ))
    val inferenceParamsList: LiveData<ArrayList<InferenceParams>> = _inferenceParamsList

    fun updateInferenceParamsList(newList: ArrayList<InferenceParams>) {
        _inferenceParamsList.value = newList
    }
}
