package com.example.newbenchmarking.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.newbenchmarking.interfaces.BenchmarkResult

class ResultViewModel : ViewModel() {
    private val _benchmarkResultList = MutableLiveData(arrayListOf<BenchmarkResult>())
    val benchmarkResultList: LiveData<ArrayList<BenchmarkResult>> = _benchmarkResultList

    fun updateInferenceResultList(newList: ArrayList<BenchmarkResult>) {
        _benchmarkResultList.value = newList
    }
}
