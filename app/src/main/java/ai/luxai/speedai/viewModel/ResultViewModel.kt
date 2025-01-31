package ai.luxai.speedai.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ai.luxai.speedai.interfaces.BenchmarkResult

class ResultViewModel : ViewModel() {
    private val _benchmarkResultList = MutableLiveData(arrayListOf<BenchmarkResult>())
    val benchmarkResultList: LiveData<ArrayList<BenchmarkResult>> = _benchmarkResultList

    fun updateInferenceResultList(newList: ArrayList<BenchmarkResult>) {
        _benchmarkResultList.value = newList
    }
}
