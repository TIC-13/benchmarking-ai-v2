package ai.luxai.speedai.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ai.luxai.speedai.interfaces.InferenceParams
import java.io.File

class InferenceViewModel : ViewModel() {

    // Initial values for InferenceParams
    private val _inferenceParamsList = MutableLiveData(listOf<InferenceParams>())
    val inferenceParamsList: LiveData<List<InferenceParams>> = _inferenceParamsList

    private val _afterRun = MutableLiveData<(() -> Unit)?>(null)
    val afterRun: LiveData<(() -> Unit)?> = _afterRun

    fun updateInferenceParamsList(newList: List<InferenceParams>) {
        _inferenceParamsList.value = newList
    }

    fun updateAfterRun(newFun: (() -> Unit)?) {
        _afterRun.value = newFun
    }
}
