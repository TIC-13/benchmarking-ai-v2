package com.example.newbenchmarking.utils

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource

@Composable
fun getImage(id: Int): Bitmap {
    val image = ImageBitmap.imageResource(id = id).asAndroidBitmap()
    return Bitmap.createScaledBitmap(image, 257, 257, true)
}

@Composable
fun getImagesIdList(numImages: Int): List<Int> {

    val imagesIdList = mutableListOf<Int>()

    val context = LocalContext.current
    val resources = context.resources
    val packageName = context.packageName

    for(i in 0 until numImages) {
        val imageName = "i$i"
        val resId = resources.getIdentifier(imageName, "drawable", packageName)
        if(resId != 0)
            imagesIdList.add(resId)
    }

    return imagesIdList
}

@Composable
fun getBitmapImages(numImages: Int): List<Bitmap> {
    val imagesIdList = getImagesIdList(numImages)
    return imagesIdList.map { getImage(id = it) }
}