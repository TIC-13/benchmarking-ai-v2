package com.example.newbenchmarking.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.newbenchmarking.machineLearning.LanguageModelInput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
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


fun getBitmapsFromFolder(folder: File, numBitmaps: Int): List<Bitmap> {
    val filenames = getFilesFromFolder(folder).subList(0, numBitmaps)
    return filenames.mapNotNull { filename ->
        val file = File(folder, filename)
        loadBitmapFromFile(file)
    }
}

fun loadBitmapFromFile(file: File): Bitmap? {
    return try {
        FileInputStream(file).use { inputStream ->
            BitmapFactory.decodeStream(inputStream)
        }
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}

fun getFilesFromFolder(folder: File): List<String> {
    return try {
        folder.listFiles()?.map { it.name } ?: emptyList()
    } catch (e: Exception) {
        throw Exception("Erro ao listar arquivos da pasta ${e.message}")
    }
}

fun parseLanguageDataset(file: File): List<LanguageModelInput> {
    return try {
        val fileContent = FileInputStream(file).bufferedReader().use { it.readText() }
        val contextQuestionPair = fileContent.split("\r\n\r\n")
        contextQuestionPair.map {
            val separatedPair = it.split("\r\n")
            LanguageModelInput(
                context = separatedPair[0],
                question = separatedPair[1]
            )
        }
    } catch (e: IOException) {
        e.printStackTrace()
        println("Error loading file: ${e.message}")
        throw e
    }
}

fun clearFolderContents(folder: File) {
    // Check if the directory exists and is indeed a directory
    if (folder.exists() && folder.isDirectory) {
        // List all contents and loop through them to delete each one
        folder.listFiles()?.forEach { file ->
            deleteRecursively(file)
        }
    } else {
        println("The specified path is not a directory or does not exist.")
    }
}

fun deleteRecursively(file: File) {
    if (file.isDirectory) {
        // If it's a directory, delete its contents recursively
        file.listFiles()?.forEach { subFile ->
            deleteRecursively(subFile)
        }
    }
    // Delete the file or directory (now empty if it was a directory)
    file.delete()
}