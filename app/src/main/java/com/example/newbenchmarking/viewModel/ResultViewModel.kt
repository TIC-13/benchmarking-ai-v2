package com.example.newbenchmarking.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.newbenchmarking.interfaces.InferenceResult

class ResultViewModel : ViewModel() {
    private val _inferenceResultList = MutableLiveData(arrayListOf<InferenceResult>())
    val inferenceResultList: LiveData<ArrayList<InferenceResult>> = _inferenceResultList

    fun updateInferenceResultList(newList: ArrayList<InferenceResult>) {
        _inferenceResultList.value = newList
    }
}
