package com.example.data.repository

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.example.R
import com.example.data.model.User
import com.example.util.NutritionCalculator
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AuthRepository(
    private val context: Context,
    private val firestore: FirebaseFirestore
) {
    companion object {
        const val SUPER_ADMIN_UID = "PtWxsVPDb3ZT8f38QhwjhDjnFF32"
        private const val TAG = "AuthRepository"
    }

    private val auth: FirebaseAuth = Firebase.auth
    private val credentialManager = CredentialManager.create(context)

    val currentUser: FirebaseUser?
        get() = auth.currentUser

    // Observe Firebase Auth state
    fun observeAuthState(): Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser)
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    // Observe User profile document in Firestore
    fun observeUserProfile(uid: String): Flow<User?> {
        return firestore.collection("users").document(uid)
            .snapshots()
            .map { snapshot ->
                if (snapshot.exists()) {
                    snapshot.toObject(User::class.java)
                } else {
                    null
                }
            }
            .catch { error ->
                Log.e(TAG, "Error observing user profile", error)
                emit(null)
            }
    }

    // Interactive Google Sign-In via Credential Manager
    fun signInWithGoogle(
        activity: Activity,
        scope: CoroutineScope,
        onSuccess: (FirebaseUser) -> Unit,
        onError: (String) -> Unit,
        onCancelled: () -> Unit = {}
    ) {
        val clientId = try {
            context.getString(R.string.default_web_client_id)
        } catch (e: Exception) {
            onError("Google Sign-In configuration missing: default_web_client_id")
            return
        }

        val signInOption = GetSignInWithGoogleOption.Builder(serverClientId = clientId).build()
        val request = GetCredentialRequest.Builder().addCredentialOption(signInOption).build()

        scope.launch {
            try {
                val result = credentialManager.getCredential(activity, request)
                val credential = result.credential
                if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
                    val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                    val authResult = auth.signInWithCredential(authCredential).await()
                    val user = authResult.user
                    if (user != null) {
                        checkAndCreateProfile(user)
                        onSuccess(user)
                    } else {
                        onError("Firebase user is null after sign in")
                    }
                } else {
                    onError("Unexpected credential format received")
                }
            } catch (e: GetCredentialCancellationException) {
                Log.w(TAG, "Google Sign-In dismissed: ${e.message}")
                onCancelled()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Google Sign-In failed", e)
                onError(e.localizedMessage ?: "Sign-in failed")
            }
        }
    }

    // Silent Auto-Sign-In on startup
    fun attemptAutoSignIn(
        scope: CoroutineScope,
        onSuccess: (FirebaseUser) -> Unit,
        onUnauthenticated: () -> Unit
    ) {
        val existing = auth.currentUser
        if (existing != null) {
            onSuccess(existing)
            return
        }

        val clientId = try {
            context.getString(R.string.default_web_client_id)
        } catch (e: Exception) {
            onUnauthenticated()
            return
        }

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(true)
            .setServerClientId(clientId)
            .setAutoSelectEnabled(true)
            .build()

        val request = GetCredentialRequest.Builder().addCredentialOption(googleIdOption).build()

        scope.launch {
            try {
                val result = credentialManager.getCredential(context, request)
                val credential = result.credential
                if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
                    val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                    val authResult = auth.signInWithCredential(authCredential).await()
                    val user = authResult.user
                    if (user != null) {
                        checkAndCreateProfile(user)
                        onSuccess(user)
                    } else {
                        onUnauthenticated()
                    }
                } else {
                    onUnauthenticated()
                }
            } catch (e: Exception) {
                onUnauthenticated()
            }
        }
    }

    // Check if profile exists; if not, create initial profile
    private suspend fun checkAndCreateProfile(user: FirebaseUser) {
        try {
            val docRef = firestore.collection("users").document(user.uid)
            val doc = docRef.get().await()
            if (!doc.exists()) {
                val initialRole = if (user.uid == SUPER_ADMIN_UID) "super_admin" else "user"
                val targets = NutritionCalculator.calculateTargets(25, 170.0, 65.0, "moderately_active")
                val newUserMap = mapOf(
                    "uid" to user.uid,
                    "name" to (user.displayName ?: "User"),
                    "email" to (user.email ?: ""),
                    "photoUrl" to (user.photoUrl?.toString() ?: ""),
                    "age" to 25,
                    "height" to 170.0,
                    "weight" to 65.0,
                    "activityLevel" to "moderately_active",
                    "dietaryPreference" to "standard",
                    "allergies" to "",
                    "language" to "en",
                    "role" to initialRole,
                    "targetCalories" to targets.calories,
                    "targetProtein" to targets.proteinGrams,
                    "targetCarbs" to targets.carbsGrams,
                    "targetFat" to targets.fatGrams,
                    "targetWaterMl" to targets.waterMl,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.set(newUserMap).await()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed creating user profile document", e)
        }
    }

    // Update user profile
    suspend fun updateUserProfile(user: User) {
        val currentUid = auth.currentUser?.uid ?: throw IllegalStateException("Not authenticated")
        val docRef = firestore.collection("users").document(currentUid)
        val targets = NutritionCalculator.calculateTargets(
            user.age,
            user.height,
            user.weight,
            user.activityLevel,
            user.dietaryPreference
        )

        val updates = mapOf(
            "name" to user.name,
            "age" to user.age,
            "height" to user.height,
            "weight" to user.weight,
            "activityLevel" to user.activityLevel,
            "dietaryPreference" to user.dietaryPreference,
            "allergies" to user.allergies,
            "language" to user.language,
            "targetCalories" to targets.calories,
            "targetProtein" to targets.proteinGrams,
            "targetCarbs" to targets.carbsGrams,
            "targetFat" to targets.fatGrams,
            "targetWaterMl" to targets.waterMl,
            "updatedAt" to FieldValue.serverTimestamp()
        )
        docRef.update(updates).await()
    }

    // Sign out
    fun signOut(scope: CoroutineScope, onComplete: () -> Unit) {
        scope.launch {
            withContext(NonCancellable) {
                try {
                    credentialManager.clearCredentialState(ClearCredentialStateRequest())
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to clear credential state: ${e.message}")
                } finally {
                    auth.signOut()
                    onComplete()
                }
            }
        }
    }
}
