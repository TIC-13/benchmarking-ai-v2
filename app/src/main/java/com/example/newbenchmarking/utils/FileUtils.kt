package com.example.newbenchmarking.utils

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

suspend fun pasteAssets(context: Context, folderPath: String = "", destinationPath: String = context.filesDir.absolutePath): Unit = withContext(
    Dispatchers.IO) {
    val assetManager = context.assets
    val files = assetManager.list(folderPath) ?: return@withContext  // List all assets at this path

    // Process each item in the current asset folder
    for (filename in files) {
        val path = if (folderPath.isEmpty()) filename else "$folderPath/$filename"
        val outFile = File(destinationPath, filename)

        try {
            // Check if the current path is a file or directory by trying to list its contents
            if (assetManager.list(path)!!.isEmpty()) {
                // It's a file (or an empty directory, which typically won't happen in assets)
                assetManager.open(path).use { input ->
                    outFile.parentFile?.mkdirs()  // Ensure directory structure exists
                    outFile.outputStream().use { output ->
                        input.copyTo(output, 1024)
                    }
                }

            } else {
                // It's a directory, call the function recursively
                if (!outFile.exists()) {
                    outFile.mkdirs()  // Create directory if it doesn't exist
                }
                pasteAssets(context, path, outFile.absolutePath)
            }
        } catch (e: IOException) {
            e.printStackTrace()  // Log or handle the exception as needed
        }
    }
}


fun fileExists(fileParent: File, filename: String): Boolean {
    return File(fileParent, filename).exists()
}

fun createFolderIfNotExists(fileParent: File, folderName: String): File {
    val folder = File(fileParent, folderName)
    if (!folder.exists())
        if (!folder.mkdirs())
            throw Exception("Erro na criação da pasta $folderName em ${fileParent.absolutePath}")
    return folder
}