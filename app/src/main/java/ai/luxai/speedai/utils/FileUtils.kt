package ai.luxai.speedai.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import ai.luxai.speedai.machineLearning.LanguageModelInput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.IOException

fun createFolderIfNotExists(fileParent: File, folderName: String): File {
    val folder = File(fileParent, folderName)
    if (!folder.exists())
        if (!folder.mkdirs())
            throw Exception("Erro na criação da pasta $folderName em ${fileParent.absolutePath}")
    return folder
}

fun getBitmapsFromAssetsFolder(
    context: Context,
    folderName: String,
    numBitmaps: Int
): List<Bitmap> {
    return try {
        val assetManager = context.assets
        val filenames = assetManager.list(folderName)?.take(numBitmaps) ?: emptyList()
        filenames.mapNotNull { filename ->
            val assetPath = "$folderName/$filename"
            loadBitmapFromAsset(context, assetPath)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        emptyList()
    }
}

fun loadBitmapFromAsset(context: Context, assetPath: String): Bitmap? {
    return try {
        val inputStream = context.assets.open(assetPath)
        BitmapFactory.decodeStream(inputStream).also {
            inputStream.close()
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

