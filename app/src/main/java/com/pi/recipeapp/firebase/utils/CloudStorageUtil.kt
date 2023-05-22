package com.pi.recipeapp.firebase.utils

import android.net.Uri
import android.util.Log
import com.google.firebase.storage.StorageReference

class CloudStorageUtil(private val storageReference: StorageReference) {
    companion object {
        private const val TAG = "CloudStorage"
    }
    fun uploadImageToCloud(
        imagePath: String,
        image: Uri,
        onSuccess: (Uri) -> Unit,
        onFailure: (Exception?) -> Unit
    ) {
        val ref = storageReference.child(imagePath)
        ref.putFile(image).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "uploadImageSuccess: ${task.result.metadata}")
                ref.downloadUrl.addOnCompleteListener { res ->
                    Log.d(TAG, "downloadImageSuccess: ${res.result}")
                    onSuccess(res.result)
                }.addOnFailureListener { exc ->
                    Log.e(TAG, "downloadImageFailure: $exc")
                    onFailure(exc)
                }
            } else {
                Log.e(TAG, "uploadImageFailure: ${task.exception}")
                onFailure(task.exception)
            }
        }
    }
}