package com.example.wastesamaritanassignment1.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Item(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val quantity: Int,
    val rating: Int,
    val remarks: String?,
    val images: List<String>
)