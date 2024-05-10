package com.example.newbenchmarking.data

import android.content.Context
import android.util.Log
import com.example.newbenchmarking.interfaces.Dataset
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

fun loadDatasets(file: File): List<Dataset> {
    val yaml = Yaml()
    var inputStream: InputStream? = null
    return try {
        inputStream = FileInputStream(file)
        val data: Map<String, Any> = yaml.load(inputStream)
        val yamlList = data.values.elementAt(0) as List<Map<String, Any>>
        val datasetsList = arrayListOf<Dataset>()
        for(element in yamlList) {
            try {
                val test = Dataset(
                    id = element["id"] as Int,
                    name = element["name"] as String,
                    path = element["path"] as String,
                    size = element["size"] as Int
                )
                datasetsList.add(test)
            }catch (e: Exception){
                Log.e("dataset_error", "Erro ao adicionar dataset de id ${element["id"]}: ${e.message}")
            }
        }
        datasetsList
    } catch (e: Exception) {
        e.printStackTrace()
        emptyList()
    } finally {
        inputStream?.close()
    }
}