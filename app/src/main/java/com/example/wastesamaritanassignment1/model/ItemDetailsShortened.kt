package com.example.wastesamaritanassignment1.model

import androidx.compose.runtime.Immutable

@Immutable
data class ItemDetailsShortened(
    val id: Int,
    val name: String,
    val quantity: Int,
    val rating: Int,
    val remarks: String?
)