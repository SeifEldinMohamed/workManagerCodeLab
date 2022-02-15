package com.example.background.workers

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.background.KEY_IMAGE_URI
import com.example.background.R

private const val TAG = "BlurWorker"
class BlurWorker(context: Context, params: WorkerParameters):Worker(context, params) {
    override fun doWork(): Result {
        // code for blur image

        makeStatusNotification("blurring image",applicationContext)
        sleep()
        return try {
//            val picture = BitmapFactory.decodeResource(
//                    applicationContext.resources,
//                    R.drawable.android_cupcake)
            val imageUri = inputData.getString(KEY_IMAGE_URI)
            if(imageUri!!.isEmpty()){
                Log.e(TAG, "Invalid input uri")
                throw IllegalArgumentException("Invalid input uri")
            }
            val resolver = applicationContext.contentResolver

            val picture = BitmapFactory.decodeStream(
                resolver.openInputStream(Uri.parse(imageUri)))

            val output = blurBitmap(picture, applicationContext)

            // Write bitmap to a temp file
            val outputUri = writeBitmapToFile(applicationContext, output)

            makeStatusNotification("Output is $outputUri", applicationContext)
            val outputData = workDataOf(KEY_IMAGE_URI to outputUri.toString())
            Result.success(outputData)
        } catch (throwable: Throwable) {
            Log.e(TAG, "Error applying blur")
            Result.failure()
        }
    }
}