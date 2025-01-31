package ai.luxai.speedai.data

import android.content.Context
import android.util.Log
import android.widget.Toast
import ai.luxai.speedai.interfaces.Category
import ai.luxai.speedai.interfaces.Dataset
import ai.luxai.speedai.interfaces.InferenceParams
import ai.luxai.speedai.interfaces.Model
import ai.luxai.speedai.interfaces.Quantization
import ai.luxai.speedai.interfaces.RunMode
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
fun getBenchmarkingTestsFromAssets(
    context: Context,
    models: List<Model>,
    datasets: List<Dataset>,
    assetFilename: String = "tests.yaml",
    onError: ((e: Exception, elementId: Int?) -> Unit)? = null
): List<InferenceParams> {
    val yaml = Yaml()
    var inputStream: InputStream? = null
    return try {
        inputStream = context.assets.open(assetFilename)
        val data: Map<String, Any> = yaml.load(inputStream)
        val yamlList = data.values.elementAt(0) as List<Map<String, Any>>
        val testsList = arrayListOf<InferenceParams>()
        for (element in yamlList) {
            try {
                val modelId = element["model_id"] as Int
                val datasetId = element["dataset_id"] as Int

                val selectedModel = models.find { it.id == modelId }
                    ?: throw Exception("Modelo usado não definido em models.yaml")

                val selectedDataset = datasets.find { it.id == datasetId }
                    ?: throw Exception("Dataset usado não definido em datasets.yaml")

                val runMode = element["runMode"] as? String
                    ?: throw Exception("runMode não definido")

                val test = InferenceParams(
                    model = selectedModel,
                    runMode =
                    if (runMode == "NNAPI")
                        RunMode.NNAPI
                    else if (runMode == "GPU")
                        RunMode.GPU
                    else
                        RunMode.CPU,
                    numThreads = element["numThreads"] as? Int
                        ?: throw Exception("numThreads não definido"),
                    numImages = element["numSamples"] as? Int
                        ?: throw Exception("numSamples não definido"),
                    dataset = selectedDataset
                )
                testsList.add(test)
            } catch (e: Exception) {
                val id = element["model_id"] as? Int
                Log.e("test_error", "Erro ao carregar teste de id ${id}: ${e.message}")
                if (onError !== null) onError(e, id)
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


