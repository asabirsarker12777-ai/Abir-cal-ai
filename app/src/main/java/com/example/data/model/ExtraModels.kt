package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class MealPlan(
    @DocumentId val id: String = "",
    val userId: String = "system",
    val title: String = "",
    val titleBn: String = "",
    val mealCategory: String = "breakfast", // breakfast, lunch, dinner, snack
    val items: String = "",
    val calories: Double = 0.0,
    val protein: Double = 0.0,
    val carbs: Double = 0.0,
    val fat: Double = 0.0,
    val isCustom: Boolean = false,
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class FavoriteFood(
    @DocumentId val id: String = "",
    val userId: String = "",
    val foodId: String = "",
    val foodName: String = "",
    val foodNameBn: String = "",
    val calories: Double = 0.0,
    val protein: Double = 0.0,
    val carbs: Double = 0.0,
    val fat: Double = 0.0,
    val servingUnit: String = "serving",
    val servingSize: Double = 1.0,
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class Announcement(
    @DocumentId val id: String = "",
    val title: String = "",
    val content: String = "",
    val authorUid: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
)
