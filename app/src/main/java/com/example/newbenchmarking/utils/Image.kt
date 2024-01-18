import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import androidx.core.content.ContextCompat

fun getImage(context: Context, id: Int): Bitmap {
    val drawable = ContextCompat.getDrawable(context, id)
    val bitmap = (drawable as BitmapDrawable).bitmap
    return Bitmap.createScaledBitmap(bitmap, 257, 257, true)
}

fun getImagesIdList(context: Context, numImages: Int): List<Int> {
    val imagesIdList = mutableListOf<Int>()
    val resources = context.resources
    val packageName = context.packageName

    for (i in 0 until numImages) {
        val imageName = "i$i"
        val resId = resources.getIdentifier(imageName, "drawable", packageName)
        if (resId != 0)
            imagesIdList.add(resId)
    }

    return imagesIdList
}

fun getBitmapImages(context: Context, numImages: Int): List<Bitmap> {
    val imagesIdList = getImagesIdList(context, numImages)
    return imagesIdList.map { getImage(context, it) }
}