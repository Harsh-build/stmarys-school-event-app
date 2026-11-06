package com.example.data.auth

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.example.R
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

object GoogleAuthHelper {
    private const val TAG = "GoogleAuthHelper"
    private const val FALLBACK_WEB_CLIENT_ID = "571130772397-nm804vbi7831dh6usta5iua1blruo34h.apps.googleusercontent.com"

    suspend fun signInWithGoogle(context: Context): Result<FirebaseUser> {
        val activity = context as? Activity
            ?: return Result.failure(IllegalStateException("Context must be an Activity"))

        val clientId = try {
            val resId = context.resources.getIdentifier("default_web_client_id", "string", context.packageName)
            if (resId != 0) context.getString(resId) else FALLBACK_WEB_CLIENT_ID
        } catch (e: Exception) {
            FALLBACK_WEB_CLIENT_ID
        }

        val credentialManager = CredentialManager.create(context)
        val signInOption = GetSignInWithGoogleOption.Builder(serverClientId = clientId).build()
        val request = GetCredentialRequest.Builder().addCredentialOption(signInOption).build()

        return try {
            val result = credentialManager.getCredential(activity, request)
            val credential = result.credential
            if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
                val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                val authResult = FirebaseAuth.getInstance().signInWithCredential(authCredential).await()
                val user = authResult.user
                if (user != null) {
                    Result.success(user)
                } else {
                    Result.failure(Exception("Firebase user is null after Google sign in."))
                }
            } else {
                Result.failure(Exception("Unexpected credential format received."))
            }
        } catch (e: GetCredentialCancellationException) {
            Log.w(TAG, "User cancelled or Google Sign-In reauth failed: ${e.message}")
            val msg = e.message ?: ""
            if (msg.contains("16") || msg.contains("reauth", ignoreCase = true) || msg.contains("DEVELOPER_ERROR", ignoreCase = true)) {
                Result.failure(Exception("Google Sign-In requires your app's SHA-1 fingerprint registered in Firebase Console (Project Settings > Android app). Alternatively, sign in using your school email & password above."))
            } else {
                Result.failure(Exception("Google Sign-In was cancelled."))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Google Sign-In failed", e)
            val msg = e.message ?: ""
            if (msg.contains("16") || msg.contains("reauth", ignoreCase = true) || msg.contains("DEVELOPER_ERROR", ignoreCase = true)) {
                Result.failure(Exception("Google Sign-In requires your app's SHA-1 fingerprint registered in Firebase Console (Project Settings > Android app). Alternatively, sign in using your school email & password above."))
            } else {
                Result.failure(e)
            }
        }
    }
}
