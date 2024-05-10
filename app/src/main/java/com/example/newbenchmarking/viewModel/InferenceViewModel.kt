package com.example.newbenchmarking.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.newbenchmarking.interfaces.InferenceParams
import java.io.File

class InferenceViewModel : ViewModel() {

    // Initial values for InferenceParams
    private val _inferenceParamsList = MutableLiveData(listOf<InferenceParams>())
    val inferenceParamsList: LiveData<List<InferenceParams>> = _inferenceParamsList

    private val _folder = MutableLiveData<File?>(null)
    val folder: LiveData<File?> = _folder

    private val _afterRun = MutableLiveData<(() -> Unit)?>(null)
    val afterRun: LiveData<(() -> Unit)?> = _afterRun

    fun updateInferenceParamsList(newList: List<InferenceParams>) {
        _inferenceParamsList.value = newList
    }

    fun updateFolder(newFolder: File?) {
        _folder.value = newFolder
    }

    fun updateAfterRun(newFun: (() -> Unit)?) {
        _afterRun.value = newFun
    }
}
