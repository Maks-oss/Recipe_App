package com.pi.recipeapp.firebase.storage

import android.net.Uri
import android.util.Log
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object CloudStorageUtil {
    private const val TAG = "CloudStorage"
    lateinit var storageReference: StorageReference

    fun uploadUserImage(
        userId: String,
        image: Uri,
        onSuccess: (Uri) -> Unit,
        onFailure: (Exception?) -> Unit
    ) {
        val ref = storageReference.child("images/$userId.jpg")
        ref.putFile(image).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "uploadUserImageSuccess: ${task.result.metadata}")
                ref.downloadUrl.addOnCompleteListener { res ->
                    Log.d(TAG, "downloadUserImageSuccess: ${res.result}")
                    onSuccess(res.result)
                }.addOnFailureListener { exc ->
                    Log.e(TAG, "downloadUserImageFailure: $exc")
                    onFailure(exc)
                }
            } else {
                Log.e(TAG, "uploadUserImageFailure: ${task.exception}")
                onFailure(task.exception)
            }
        }
    }
}