package com.pi.recipeapp.firebase.authorization

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.pi.recipeapp.firebase.storage.CloudStorageUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class InAppAuth {
    companion object {
        private const val TAG = "InAppAuth"
    }

    private val auth = Firebase.auth

    fun signIn(
        email: String,
        password: String,
        onSuccess: (FirebaseUser?) -> Unit,
        onFailure: (Exception?) -> Unit
    ) = auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
        if (task.isSuccessful) {
            Log.d(TAG, "signInWithCredential:success ${auth.currentUser}")
            onSuccess(auth.currentUser)
        } else {
            Log.e(TAG, "signInWithCredential:failure", task.exception)
            onFailure(task.exception)
        }
    }

    fun signUp(
        email: String,
        password: String,
        imageUri: Uri? = null,
        onSuccess: (FirebaseUser?) -> Unit,
        onFailure: (Exception?) -> Unit
    ) = auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val firebaseUser = auth.currentUser!!
            if (imageUri != null) {
                CoroutineScope(Dispatchers.Default).launch {
                    CloudStorageUtil.uploadUserImage(
                        firebaseUser.uid,
                        imageUri,
                        onSuccess = { uri ->
                            updateUserProfile(firebaseUser, uri, onSuccess)
                        },
                        onFailure = { exc ->
                            onFailure(exc)
                        })
                }
            }
            onSuccess(firebaseUser)
        } else {
            onFailure(task.exception)
        }
    }

    private fun updateUserProfile(
        user: FirebaseUser,
        imageUri: Uri,
        onSuccess: (FirebaseUser?) -> Unit
    ) {
        user.updateProfile(
            UserProfileChangeRequest.Builder().setPhotoUri(imageUri).build()
        ).addOnCompleteListener {
            onSuccess(user)
        }
    }
}