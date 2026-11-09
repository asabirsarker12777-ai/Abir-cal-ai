package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class WaterEntry(
    @DocumentId val id: String = "",
    val userId: String = "",
    val amountMl: Int = 250,
    val entryDate: String = "", // YYYY-MM-DD
    val entryTime: String = "", // HH:mm
    @ServerTimestamp val createdAt: Timestamp? = null
)
