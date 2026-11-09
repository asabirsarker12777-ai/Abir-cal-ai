package com.example.data.repository

import android.util.Log
import com.example.data.model.Announcement
import com.example.data.model.FoodItem
import com.example.data.model.User
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class AdminRepository(private val firestore: FirebaseFirestore) {

    companion object {
        const val SUPER_ADMIN_UID = "PtWxsVPDb3ZT8f38QhwjhDjnFF32"
        private const val TAG = "AdminRepository"
    }

    private val auth = Firebase.auth

    fun isSuperAdmin(): Boolean {
        return auth.currentUser?.uid == SUPER_ADMIN_UID
    }

    suspend fun isAdmin(): Boolean {
        if (isSuperAdmin()) return true
        val uid = auth.currentUser?.uid ?: return false
        return try {
            val doc = firestore.collection("users").document(uid).get().await()
            val role = doc.getString("role") ?: "user"
            role == "admin" || role == "super_admin"
        } catch (e: Exception) {
            false
        }
    }

    // Observe announcements
    fun observeAnnouncements(): Flow<List<Announcement>> {
        return firestore.collection("announcements")
            .snapshots()
            .map { it.toObjects(Announcement::class.java) }
            .catch { error ->
                Log.e(TAG, "Error observing announcements", error)
                emit(emptyList())
            }
    }

    // Post announcement
    suspend fun postAnnouncement(title: String, content: String) {
        val uid = auth.currentUser?.uid ?: throw IllegalStateException("Not authenticated")
        val docRef = firestore.collection("announcements").document()
        val payload = mapOf(
            "title" to title,
            "content" to content,
            "authorUid" to uid,
            "createdAt" to FieldValue.serverTimestamp()
        )
        docRef.set(payload).await()
    }

    // Add verified food to Firestore
    suspend fun addVerifiedFood(food: FoodItem) {
        val uid = auth.currentUser?.uid ?: throw IllegalStateException("Not authenticated")
        val docRef = firestore.collection("foods").document()
        val payload = mapOf(
            "nameEn" to food.nameEn,
            "nameBn" to food.nameBn,
            "category" to food.category,
            "servingUnit" to food.servingUnit,
            "servingSize" to food.servingSize,
            "calories" to food.calories,
            "protein" to food.protein,
            "carbs" to food.carbs,
            "fat" to food.fat,
            "cookingMethod" to food.cookingMethod,
            "description" to food.description,
            "isVerified" to true,
            "createdBy" to uid,
            "createdAt" to FieldValue.serverTimestamp()
        )
        docRef.set(payload).await()
    }

    // Super Admin: Update user role
    suspend fun setUserRole(targetUid: String, newRole: String) {
        if (!isSuperAdmin()) throw SecurityException("Only Super Admin can change user roles")
        firestore.collection("users").document(targetUid)
            .update("role", newRole, "updatedAt", FieldValue.serverTimestamp())
            .await()
    }

    // Get statistics
    suspend fun getAppStatistics(): Map<String, Int> {
        return try {
            val usersCount = firestore.collection("users").get().await().size()
            val foodEntriesCount = firestore.collection("foodEntries").get().await().size()
            val foodsCount = firestore.collection("foods").get().await().size()
            mapOf(
                "total_users" to usersCount,
                "total_food_logs" to foodEntriesCount,
                "custom_foods_count" to foodsCount
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed fetching admin statistics", e)
            mapOf("total_users" to 0, "total_food_logs" to 0, "custom_foods_count" to 0)
        }
    }
}
