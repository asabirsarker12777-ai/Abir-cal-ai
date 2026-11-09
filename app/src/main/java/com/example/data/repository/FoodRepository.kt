package com.example.data.repository

import android.util.Log
import com.example.data.datasource.BangladeshiFoodDatabase
import com.example.data.model.FavoriteFood
import com.example.data.model.FoodEntry
import com.example.data.model.FoodItem
import com.example.data.model.WaterEntry
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FoodRepository(private val firestore: FirebaseFirestore) {

    companion object {
        private const val TAG = "FoodRepository"
    }

    private val auth = Firebase.auth

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in to access food logs")
    }

    fun getTodayDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    fun getCurrentTime(): String {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return sdf.format(Date())
    }

    // Observe food entries for a specific user and date
    fun observeFoodEntries(date: String): Flow<List<FoodEntry>> {
        val uid = requireUserId()
        return firestore.collection("foodEntries")
            .whereEqualTo("userId", uid)
            .whereEqualTo("mealDate", date)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(FoodEntry::class.java)
            }
            .catch { error ->
                Log.e(TAG, "Error observing food entries for date $date", error)
                emit(emptyList())
            }
    }

    // Add a food diary entry
    suspend fun addFoodEntry(entry: FoodEntry) {
        val uid = requireUserId()
        val docRef = firestore.collection("foodEntries").document()
        val payload = mapOf(
            "userId" to uid,
            "foodName" to entry.foodName,
            "foodNameBn" to entry.foodNameBn,
            "mealType" to entry.mealType,
            "servingUnit" to entry.servingUnit,
            "servingSize" to entry.servingSize,
            "calories" to entry.calories,
            "protein" to entry.protein,
            "carbs" to entry.carbs,
            "fat" to entry.fat,
            "mealDate" to if (entry.mealDate.isNotBlank()) entry.mealDate else getTodayDate(),
            "mealTime" to if (entry.mealTime.isNotBlank()) entry.mealTime else getCurrentTime(),
            "isAiEstimated" to entry.isAiEstimated,
            "createdAt" to FieldValue.serverTimestamp()
        )
        docRef.set(payload).await()
    }

    // Update food diary entry (e.g. change serving size or values)
    suspend fun updateFoodEntry(entry: FoodEntry) {
        val uid = requireUserId()
        require(entry.id.isNotBlank()) { "Entry ID cannot be blank" }
        val docRef = firestore.collection("foodEntries").document(entry.id)
        val payload = mapOf(
            "userId" to uid,
            "foodName" to entry.foodName,
            "foodNameBn" to entry.foodNameBn,
            "mealType" to entry.mealType,
            "servingUnit" to entry.servingUnit,
            "servingSize" to entry.servingSize,
            "calories" to entry.calories,
            "protein" to entry.protein,
            "carbs" to entry.carbs,
            "fat" to entry.fat,
            "mealDate" to entry.mealDate,
            "mealTime" to entry.mealTime,
            "isAiEstimated" to entry.isAiEstimated
        )
        docRef.update(payload).await()
    }

    // Delete a food diary entry
    suspend fun deleteFoodEntry(entryId: String) {
        require(entryId.isNotBlank()) { "Entry ID cannot be blank" }
        firestore.collection("foodEntries").document(entryId).delete().await()
    }

    // Observe water entries for date
    fun observeWaterEntries(date: String): Flow<List<WaterEntry>> {
        val uid = requireUserId()
        return firestore.collection("waterEntries")
            .whereEqualTo("userId", uid)
            .whereEqualTo("entryDate", date)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(WaterEntry::class.java)
            }
            .catch { error ->
                Log.e(TAG, "Error observing water entries for date $date", error)
                emit(emptyList())
            }
    }

    // Add water
    suspend fun addWater(amountMl: Int, date: String = getTodayDate()) {
        val uid = requireUserId()
        val docRef = firestore.collection("waterEntries").document()
        val payload = mapOf(
            "userId" to uid,
            "amountMl" to amountMl,
            "entryDate" to date,
            "entryTime" to getCurrentTime(),
            "createdAt" to FieldValue.serverTimestamp()
        )
        docRef.set(payload).await()
    }

    // Remove water entry
    suspend fun deleteWaterEntry(entryId: String) {
        firestore.collection("waterEntries").document(entryId).delete().await()
    }

    // Favorite foods
    fun observeFavoriteFoods(): Flow<List<FavoriteFood>> {
        val uid = requireUserId()
        return firestore.collection("favoriteFoods")
            .whereEqualTo("userId", uid)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(FavoriteFood::class.java)
            }
            .catch { error ->
                Log.e(TAG, "Error observing favorite foods", error)
                emit(emptyList())
            }
    }

    suspend fun addFavoriteFood(food: FoodItem) {
        val uid = requireUserId()
        val docRef = firestore.collection("favoriteFoods").document()
        val payload = mapOf(
            "userId" to uid,
            "foodId" to food.id,
            "foodName" to food.nameEn,
            "foodNameBn" to food.nameBn,
            "calories" to food.calories,
            "protein" to food.protein,
            "carbs" to food.carbs,
            "fat" to food.fat,
            "servingUnit" to food.servingUnit,
            "servingSize" to food.servingSize,
            "createdAt" to FieldValue.serverTimestamp()
        )
        docRef.set(payload).await()
    }

    suspend fun removeFavoriteFood(favId: String) {
        firestore.collection("favoriteFoods").document(favId).delete().await()
    }

    // Search Bangladeshi & International foods
    fun searchFoods(query: String, category: String? = null): List<FoodItem> {
        return BangladeshiFoodDatabase.search(query, category)
    }

    // Weekly summary for analytics
    suspend fun getPastDaysEntries(days: Int): List<FoodEntry> {
        val uid = requireUserId()
        return try {
            val snapshot = firestore.collection("foodEntries")
                .whereEqualTo("userId", uid)
                .get()
                .await()
            snapshot.toObjects(FoodEntry::class.java)
        } catch (e: Exception) {
            Log.e(TAG, "Failed fetching past days entries", e)
            emptyList()
        }
    }
}
