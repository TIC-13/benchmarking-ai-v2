package ai.luxai.speedai.interfaces

data class InferenceParams(
    var model: Model,
    var runMode: RunMode,
    var numThreads: Int,
    var numImages: Int,
    var dataset: Dataset,
    var type: Type = Type.Benchmarking,
    )

enum class RunMode {
    CPU, GPU, NNAPI
}

enum class Type {
    Benchmarking, Custom
}