package com.example.wastesamaritanassignment1.model

import androidx.compose.runtime.Stable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Stable
@Entity
data class Item(
    @PrimaryKey(autoGenerate = true)
    val id: Int?,
    var name: String,
    var quantity: Int,
    var rating: Int,
    var remarks: String?,
    var images: List<String>
)