package com.example.newbenchmarking.data

import android.content.Context
import android.util.Log
import com.example.newbenchmarking.interfaces.Category
import com.example.newbenchmarking.interfaces.Model
import com.example.newbenchmarking.interfaces.Quantization
import org.jetbrains.annotations.Async.Execute
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

fun getModelsFromAssets(context: Context, onError: ((e: Exception, elementId: Int?) -> Unit)? = null): List<Model> {

    val yamlFileName = "models.yaml"
    val yaml = Yaml()
    var inputStream: InputStream? = null
    return try {
        // Open the YAML file from the assets folder
        inputStream = context.assets.open(yamlFileName)
        val data: Map<String, Any> = yaml.load(inputStream)
        val yamlList = data.values.elementAt(0) as List<Map<String, Any>>
        val modelsList = arrayListOf<Model>()

        for (element in yamlList) {
            try {

                val inputShape = element["inputShape"] as ArrayList<Int>?
                val outputShape = element["outputShape"] as ArrayList<Int>?

                val filename = element["file"] as? String
                    ?: throw Exception("Nome do arquivo não definido em models.yaml")

                // Check if the file exists in the assets folder
                context.assets.open(filename).use {
                    // File exists in assets, no exception means success
                }

                val model = Model(
                    id = element["id"] as? Int
                        ?: throw Exception("ID não definido"),
                    label = element["name"] as? String
                        ?: throw Exception("name não definido"),
                    description = element["type"] as? String
                        ?: throw Exception("type não definido"),
                    longDescription = element["description"] as? String
                        ?: throw Exception("longDescription não definida"),
                    filename = filename, // Store only the file name since it's in assets
                    quantization = Quantization.valueOf(element["quantization"] as? String
                        ?: throw Exception("quantization não definida")),
                    inputShape = inputShape?.toIntArray(),
                    outputShape = outputShape?.toIntArray(),
                    category = Category.valueOf(element["category"] as? String
                        ?: throw Exception("category não definida"))
                )
                modelsList.add(model)
            } catch (e: Exception) {
                val id = element["id"] as? Int
                Log.e("model_error", "Erro ao adicionar modelo de ID $id: ${e.message}")
                onError?.invoke(e, id)
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

fun getModelFromFile(file: File): Model? {

    if(!file.exists())
        throw Exception("Arquivo não existe")

    if(file.isDirectory)
        throw Exception("Arquivo fornecido é diretório")

    val (filename, extension) = file.name.split(".")

    if(extension != "tflite") {
        return null
    }

    return Model(
        id = 1,
        label = filename,
        file = file
    )
}
