package com.example.newbenchmarking.data

import android.content.Context
import android.util.Log
import com.example.newbenchmarking.interfaces.Dataset
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

fun loadDatasets(file: File, onError: ((e: Exception, elementId: Int?) -> Unit)? = null): List<Dataset> {
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
                    id = element["id"] as? Int
                        ?: throw Exception("id não definido"),
                    name = element["name"] as? String
                        ?: throw Exception("name não definido"),
                    path = element["path"] as? String
                        ?: throw Exception("path não definido"),
                    size = element["size"] as? Int
                        ?: throw Exception("size não definido")
                )
                datasetsList.add(test)
            }catch (e: Exception){
                val id = element["id"] as? Int
                Log.e("dataset_error", "Erro ao adicionar dataset de id ${element["id"]}: ${e.message}")
                if(onError !== null) onError(e, id)
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