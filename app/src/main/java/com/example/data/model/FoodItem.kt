package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class FoodItem(
    @DocumentId val id: String = "",
    val nameEn: String = "",
    val nameBn: String = "",
    val category: String = "", // Rice, Traditional, Protein, Vegetables, Snacks, Fruits/Dairy
    val servingUnit: String = "serving",
    val servingSize: Double = 1.0,
    val calories: Double = 0.0,
    val protein: Double = 0.0,
    val carbs: Double = 0.0,
    val fat: Double = 0.0,
    val cookingMethod: String = "",
    val description: String = "",
    val isVerified: Boolean = true,
    val createdBy: String = "system",
    @ServerTimestamp val createdAt: Timestamp? = null
)
