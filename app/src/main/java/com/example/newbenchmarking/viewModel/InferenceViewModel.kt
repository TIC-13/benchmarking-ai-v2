package com.example.newbenchmarking.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.newbenchmarking.data.DEFAULT_PARAMS
import com.example.newbenchmarking.interfaces.InferenceParams
class InferenceViewModel : ViewModel() {
    // Initial values for InferenceParams
    private val _inferenceParamsList = MutableLiveData(arrayListOf(
        DEFAULT_PARAMS.params
    ))
    val inferenceParamsList: LiveData<ArrayList<InferenceParams>> = _inferenceParamsList

    fun updateInferenceParamsList(newList: ArrayList<InferenceParams>) {
        _inferenceParamsList.value = newList
    }
}
