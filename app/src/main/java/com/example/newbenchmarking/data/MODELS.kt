package com.example.newbenchmarking.data

import android.content.Context
import android.util.Log
import com.example.newbenchmarking.interfaces.Category
import com.example.newbenchmarking.interfaces.Model
import com.example.newbenchmarking.interfaces.Quantization
import com.opencsv.CSVReader
import org.tensorflow.lite.DataType
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.FileReader
import java.io.InputStream

fun getModels(context: Context): List<Model> {
    val yaml = Yaml()
    var inputStream: InputStream? = null
    return try {
        inputStream = context.assets.open("models.yaml")
        val data: Map<String, Any> = yaml.load(inputStream)
        val yamlList = data.values.elementAt(0) as List<Map<String, Any>>
        val modelsList = arrayListOf<Model>()
        for(element in yamlList) {
            try {
                val inputShape = element["inputShape"] as ArrayList<Int>?
                val outputShape = element["outputShape"] as ArrayList<Int>?
                val model = Model(
                    label = element["name"] as String,
                    description = element["type"] as String,
                    longDescription = element["description"] as String,
                    filename = element["file"] as String,
                    quantization = Quantization.valueOf(element["quantization"] as String),
                    inputShape = if(inputShape !== null) inputShape.toIntArray() else null,
                    outputShape = if(outputShape !== null) outputShape.toIntArray() else null,
                    category = Category.valueOf(element["category"] as String)
                )
                modelsList.add(model)
            }catch (e: Exception){
                Log.e("model_error", "Erro ao adicionar modelo ${element["name"]}: ${e.message}")
            }

        }
        modelsList
    } catch (e: Exception) {
        e.printStackTrace()
        emptyList()
    } finally {
        inputStream?.close()
    }
}

