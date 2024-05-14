package com.example.newbenchmarking.data

import android.util.Log
import com.example.newbenchmarking.interfaces.Category
import com.example.newbenchmarking.interfaces.Model
import com.example.newbenchmarking.interfaces.Quantization
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

fun getModels(file: File, onError: ((e: Exception, elementId: Int?) -> Unit)? = null): List<Model> {
    val yaml = Yaml()
    var inputStream: InputStream? = null
    return try {
        inputStream = FileInputStream(file)
        val data: Map<String, Any> = yaml.load(inputStream)
        val yamlList = data.values.elementAt(0) as List<Map<String, Any>>
        val modelsList = arrayListOf<Model>()
        for(element in yamlList) {
            try {

                val inputShape = element["inputShape"] as ArrayList<Int>?
                val outputShape = element["outputShape"] as ArrayList<Int>?

                val model = Model(
                    id = element["id"] as? Int
                        ?: throw Exception("ID não definido"),
                    label = element["name"] as? String
                        ?: throw Exception("name não definido"),
                    description = element["type"] as? String
                        ?: throw Exception("type não definido"),
                    longDescription = element["description"] as? String
                        ?: throw Exception("longDescription não definida"),
                    filename = element["file"] as? String
                        ?: throw Exception("file não definido"),
                    quantization = Quantization.valueOf(element["quantization"] as? String
                        ?: throw Exception("quantization não definida")),
                    inputShape = if(inputShape !== null) inputShape.toIntArray() else null,
                    outputShape = if(outputShape !== null) outputShape.toIntArray() else null,
                    category = Category.valueOf(element["category"] as? String
                        ?: throw Exception("category não definida"))
                )
                modelsList.add(model)
            }catch (e: Exception){
                val id = element["id"] as? Int
                Log.e("model_error", "Erro ao adicionar modelo de ID $id: ${e.message}")
                if(onError !== null) onError(e, id)
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

