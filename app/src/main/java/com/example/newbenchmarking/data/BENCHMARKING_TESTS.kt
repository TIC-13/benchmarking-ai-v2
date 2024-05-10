package com.example.newbenchmarking.data

import android.content.Context
import android.util.Log
import com.example.newbenchmarking.interfaces.Category
import com.example.newbenchmarking.interfaces.Dataset
import com.example.newbenchmarking.interfaces.InferenceParams
import com.example.newbenchmarking.interfaces.Model
import com.example.newbenchmarking.interfaces.Quantization
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

fun getBenchmarkingTests(models: List<Model>, datasets: List<Dataset>, file: File): List<InferenceParams> {
    val yaml = Yaml()
    var inputStream: InputStream? = null
    return try {
        inputStream = FileInputStream(file)
        val data: Map<String, Any> = yaml.load(inputStream)
        val yamlList = data.values.elementAt(0) as List<Map<String, Any>>
        val testsList = arrayListOf<InferenceParams>()
        for(element in yamlList) {
            try {
                val modelId = element["model_id"] as Int
                val datasetId = element["dataset_id"] as Int
                val selectedModel = models.find { it.id == modelId }
                if(selectedModel === null)
                    throw Exception("Modelo usado para teste não foi definido em models.yaml")
                val selectedDataset = datasets.find { it.id == datasetId }
                if(selectedDataset === null)
                    throw Exception("Dataset usado para teste não foi definido em datasets.yaml")
                val runMode = element["runMode"] as String

                val test = InferenceParams(
                    model = selectedModel,
                    useNNAPI = runMode == "NNAPI",
                    useGPU = runMode == "GPU",
                    numThreads = element["numThreads"] as Int,
                    numImages = element["numSamples"] as Int,
                    dataset = selectedDataset
                )
                testsList.add(test)
            }catch (e: Exception){
                Log.e("test_error", "Erro ao adicionar teste com modelo de id ${element["model_id"]}: ${e.message}")
            }
        }
        testsList
    } catch (e: Exception) {
        e.printStackTrace()
        emptyList()
    } finally {
        inputStream?.close()
    }
}

