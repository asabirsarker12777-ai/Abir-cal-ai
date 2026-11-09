package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class User(
    @DocumentId val id: String = "",
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val photoUrl: String = "",
    val age: Int = 25,
    val height: Double = 170.0,
    val weight: Double = 65.0,
    val activityLevel: String = "moderately_active",
    val dietaryPreference: String = "standard",
    val allergies: String = "",
    val language: String = "en",
    val role: String = "user",
    val targetCalories: Int = 2000,
    val targetProtein: Int = 100,
    val targetCarbs: Int = 250,
    val targetFat: Int = 60,
    val targetWaterMl: Int = 2500,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)
