package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class FoodEntry(
    @DocumentId val id: String = "",
    val userId: String = "",
    val foodName: String = "",
    val foodNameBn: String = "",
    val mealType: String = "breakfast", // breakfast, lunch, dinner, snack
    val servingUnit: String = "serving",
    val servingSize: Double = 1.0,
    val calories: Double = 0.0,
    val protein: Double = 0.0,
    val carbs: Double = 0.0,
    val fat: Double = 0.0,
    val mealDate: String = "", // YYYY-MM-DD
    val mealTime: String = "", // HH:mm
    val isAiEstimated: Boolean = false,
    @ServerTimestamp val createdAt: Timestamp? = null
)
