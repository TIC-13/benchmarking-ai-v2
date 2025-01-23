package com.example.newbenchmarking.data

import android.util.Log
import com.example.newbenchmarking.interfaces.Category
import com.example.newbenchmarking.interfaces.Model
import com.example.newbenchmarking.interfaces.Quantization
import org.jetbrains.annotations.Async.Execute
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.FileInputStream
import java.io.InputStream


fun getModelsFromYaml(folder: File, onError: ((e: Exception, elementId: Int?) -> Unit)? = null): List<Model> {

    if(!folder.exists())
        throw Exception("Pasta fornecida não existe")

    val yamlFile = File(folder, "models.yaml")

    val yaml = Yaml()
    var inputStream: InputStream? = null
    return try {
        inputStream = FileInputStream(yamlFile)
        val data: Map<String, Any> = yaml.load(inputStream)
        val yamlList = data.values.elementAt(0) as List<Map<String, Any>>
        val modelsList = arrayListOf<Model>()
        for(element in yamlList) {
            try {

                val inputShape = element["inputShape"] as ArrayList<Int>?
                val outputShape = element["outputShape"] as ArrayList<Int>?

                val filename = element["file"] as? String
                    ?: throw Exception("Nome do arquivo não definido em models.yaml")

                val modelFile = File(folder, filename)
                if(!modelFile.exists())
                    throw Exception("Arquivo de modelo definido em models.yaml não existe: $filename")

                val model = Model(
                    id = element["id"] as? Int
                        ?: throw Exception("ID não definido"),
                    label = element["name"] as? String
                        ?: throw Exception("name não definido"),
                    description = element["type"] as? String
                        ?: throw Exception("type não definido"),
                    longDescription = element["description"] as? String
                        ?: throw Exception("longDescription não definida"),
                    file = modelFile,
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
