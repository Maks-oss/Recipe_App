package com.pi.recipeapp.authorization

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

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
                firebaseUser.updateProfile(
                    UserProfileChangeRequest.Builder().setPhotoUri(imageUri).build()
                ).addOnCompleteListener {
                    onSuccess(firebaseUser)
                }
            } else {
                onSuccess(firebaseUser)
            }
        } else {
            onFailure(task.exception)
        }
    }
}