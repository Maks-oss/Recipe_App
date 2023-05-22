package com.pi.recipeapp.firebase.auth

import android.app.Activity.RESULT_OK
import android.content.Context
import android.util.Log
import androidx.activity.result.ActivityResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.pi.recipeapp.R

class GoogleAuth(private val context: Context) {
    companion object {
        private const val TAG = "GoogleAuth"
    }
    private val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.server_client_id))
        .requestEmail()
        .build()
    val googleSignInClient: GoogleSignInClient =
        GoogleSignIn.getClient(context, gso)
    private val auth = Firebase.auth

    fun googleAuthorize(result: ActivityResult, onSuccess: (FirebaseUser?) -> Unit, onFailure: (Exception?) -> Unit) {
        if (result.resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                signIn(account.idToken!!, onSuccess, onFailure)
            } catch (e: ApiException) {
                Log.e(TAG, "Google sign in failed $e ")
                onFailure(e)
            }
        }
    }

    private fun signIn(idToken: String, onSuccess: (FirebaseUser?) -> Unit, onFailure: (Exception?) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success ${auth.currentUser}")
                    onSuccess(auth.currentUser)
                } else {
                    Log.e(TAG, "signInWithCredential:failure", task.exception)
                    onFailure(task.exception)
                }
            }.addOnCanceledListener {
                Log.d(TAG, "signInCancelled: ")
            }.addOnFailureListener {
                Log.d(TAG, "signInFailure: $it")
            }
    }

}