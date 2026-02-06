package com.my.ganeshseats.Utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Paint.FILTER_BITMAP_FLAG
import android.graphics.Typeface
import android.media.ExifInterface
import android.net.Uri
import android.os.Environment
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class ImageUtils {

    // Average image size = 600-800 kb
    //private val maxHeight = 1280.0f  // 816, 400
    //private val maxWidth = 1280.0f   // 612, 300

    // Average image size = 40-60 kb
    //private val maxHeight = 400.0f  // 816, 400
    //private val maxWidth = 300.0f   // 612, 300

    // Average image size = 190 - 215 kb
    //private val maxHeight = 816.0f  // 816, 400
    //private val maxWidth = 612.0f   // 612, 300

    private val maxHeight = 1000.0f  // 816, 400
    private val maxWidth = 750.0f   // 612, 300

    companion object {
        val instance = ImageUtils()
    }

    /**
     * Reduces the size of an image without affecting its quality.
     *
     * @param imagePath -Path of an image
     * @return
     */

    public fun getBitmapFromUri(context: Context, uri: Uri): Bitmap {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()

        val exif = context.contentResolver.openInputStream(uri)?.let {
            ExifInterface(it)
        }

        val orientation = exif?.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        ) ?: ExifInterface.ORIENTATION_NORMAL

        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
        }

        return Bitmap.createBitmap(
            bitmap,
            0, 0,
            bitmap.width,
            bitmap.height,
            matrix,
            true
        )
//        return BitmapFactory.decodeStream(inputStream).also {
//            inputStream?.close()
//        }
    }


    public fun resizeBitmap(bitmap: Bitmap, maxSize: Int): Bitmap {
        var width = bitmap.width
        var height = bitmap.height

        val ratio = width.toFloat() / height.toFloat()

        if (ratio > 1) {
            width = maxSize
            height = (width / ratio).toInt()
        } else {
            height = maxSize
            width = (height * ratio).toInt()
        }

        return Bitmap.createScaledBitmap(bitmap, width, height, true)
    }


    public fun compressImageTo2MB(context: Context, uri: Uri): File {
        val originalBitmap = getBitmapFromUri(context, uri)

        // 👇 Resize to max 1920px (camera safe size)
        val resizedBitmap = resizeBitmap(originalBitmap, 1920)

        var quality = 100
        val maxSize = 2 * 1024 * 1024 // 2MB
        lateinit var file: File

        do {
            val baos = ByteArrayOutputStream()
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos)

            file = File(context.cacheDir, "img_${System.currentTimeMillis()}.jpg")
            FileOutputStream(file).use {
                it.write(baos.toByteArray())
            }

            quality -= 5
        } while (file.length() > maxSize && quality > 20)

        return file
    }



    public fun addFooterTextToBitmap(
        context: Context,
        originalBitmap: Bitmap,
        footerText: String
    ): Bitmap {

        val padding = 32
        val textSize = 45f

        val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            this.textSize = textSize
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        val maxTextWidth = originalBitmap.width - (padding * 2)

        val staticLayout = StaticLayout.Builder
            .obtain(footerText, 0, footerText.length, textPaint, maxTextWidth)
            .setAlignment(Layout.Alignment.ALIGN_CENTER)
            .setLineSpacing(1.0f, 1.0f)
            .setIncludePad(false)
            .build()

        val footerHeight = staticLayout.height + padding * 2

        val finalBitmap = Bitmap.createBitmap(
            originalBitmap.width,
            originalBitmap.height + footerHeight,
            Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(finalBitmap)

        // Draw original image
        canvas.drawBitmap(originalBitmap, 0f, 0f, null)

        // Footer background
        val bgPaint = Paint().apply {
            color = Color.BLACK
        }
        canvas.drawRect(
            0f,
            originalBitmap.height.toFloat(),
            finalBitmap.width.toFloat(),
            finalBitmap.height.toFloat(),
            bgPaint
        )

        // Draw wrapped text
        canvas.save()
        canvas.translate(
            padding.toFloat(),
            originalBitmap.height + padding.toFloat()
        )
        staticLayout.draw(canvas)
        canvas.restore()

        return finalBitmap
    }

     fun footerBitmapToFile(context: Context, bitmap: Bitmap): File {
        val file = File(context.cacheDir, "final_${System.currentTimeMillis()}.jpg")
        FileOutputStream(file).use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, it)
        }
        return file
    }

     fun compressBitmapTo2MB(context: Context, bitmap: Bitmap): File {
        val maxSize = 2 * 1024 * 1024 // 2MB
        var quality = 100
        lateinit var file: File

        do {
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos)

            file = File(context.cacheDir, "final_${System.currentTimeMillis()}.jpg")
            FileOutputStream(file).use {
                it.write(baos.toByteArray())
            }

            quality -= 5
        } while (file.length() > maxSize && quality > 20)

        return file
    }



    fun getCompressedImageFile(imagePath: String): File {
        var scaledBitmap: Bitmap? = null
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        var bmp: Bitmap? = BitmapFactory.decodeFile(imagePath, options)

        var actualHeight = options.outHeight
        var actualWidth = options.outWidth

        var imgRatio = actualWidth.toFloat() / actualHeight.toFloat()
        val maxRatio = maxWidth / maxHeight

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight
                actualWidth = (imgRatio * actualWidth).toInt()
                actualHeight = maxHeight.toInt()
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth
                actualHeight = (imgRatio * actualHeight).toInt()
                actualWidth = maxWidth.toInt()
            } else {
                actualHeight = maxHeight.toInt()
                actualWidth = maxWidth.toInt()
            }
        }
        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight)
        options.inJustDecodeBounds = false
        options.inDither = false
        options.inPurgeable = true
        options.inInputShareable = true
        options.inTempStorage = ByteArray(16 * 1024)
        try {
            bmp = BitmapFactory.decodeFile(imagePath, options)
        } catch (exception: OutOfMemoryError) {
            exception.printStackTrace()
        }

        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.RGB_565)
        } catch (exception: OutOfMemoryError) {
            exception.printStackTrace()
        }

        val ratioX = actualWidth / options.outWidth.toFloat()
        val ratioY = actualHeight / options.outHeight.toFloat()
        val middleX = actualWidth / 2.0f
        val middleY = actualHeight / 2.0f
        val scaleMatrix = Matrix()
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)
        val canvas = Canvas(scaledBitmap!!)
        canvas.setMatrix(scaleMatrix)
        canvas.drawBitmap(bmp!!, middleX - bmp.width / 2, middleY - bmp.height / 2, Paint(FILTER_BITMAP_FLAG))
        bmp.recycle()

        val exif: ExifInterface
        try {
            exif = ExifInterface(imagePath)
            val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0)
            val matrix = Matrix()
            if (orientation == 6) {
                matrix.postRotate(90.toFloat())
            } else if (orientation == 3) {
                matrix.postRotate(180.toFloat())
            } else if (orientation == 8) {
                matrix.postRotate(270.toFloat())
            }
            scaledBitmap =
                Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.width, scaledBitmap.height, matrix, true)
        } catch (e: IOException) {
            e.printStackTrace()
        }


        var fileOutputStream: FileOutputStream? = null
        val file = File(getFilename())
        try {
            fileOutputStream = FileOutputStream(file)
            //          write the compressed bitmap at the destination specified by filename.
            scaledBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return file
        //  return filename;
        // return scaledBitmap;
    }


    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
            val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())
            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
        }
        val totalPixels = (width * height).toFloat()
        val totalReqPixelsCap = (reqWidth * reqHeight * 2).toFloat()
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++
        }
        return inSampleSize
    }

    fun getFilename(): String {
        val mediaStorageDir = File(
            "${Environment.getExternalStorageDirectory()}/Android/data/" + "PayBy" + "/files_root"
        )
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdirs()
        }
        val mImageName = "IMG_" + (System.currentTimeMillis()).toString() + ".jpg"
        return (mediaStorageDir.absolutePath + "/" + mImageName)
    }

    fun bitmapToFile(context: Context, bitmap: Bitmap): File? {
        try {
            val file = File(context.cacheDir, "temp_image.png")
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            return file
        }catch (e: IOException) {
            e.printStackTrace()
            // Return false if there was an error writing the file
            false
        }

        return null
    }

}