package ai.luxai.speedai.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File

fun createFolderIfNotExists(fileParent: File, folderName: String): File {
    val folder = File(fileParent, folderName)
    if (!folder.exists())
        if (!folder.mkdirs())
            throw Exception("Erro na criação da pasta $folderName em ${fileParent.absolutePath}")
    return folder
}

fun <T> Array<T>.takeWithRepeat(n: Int): List<T> {
    if (n <= size) return take(n)

    val result = mutableListOf<T>()
    var index = 0

    repeat(n) {
        result.add(this[index])
        index = (index + 1) % size  // Loop back to the start when reaching the end
    }

    return result
}

fun getBitmapsFromAssetsFolder(
    context: Context,
    folderName: String,
    numBitmaps: Int
): List<Bitmap> {
    return try {
        val assetManager = context.assets
        val filenames = assetManager.list(folderName)?.takeWithRepeat(numBitmaps) ?: emptyList()
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


