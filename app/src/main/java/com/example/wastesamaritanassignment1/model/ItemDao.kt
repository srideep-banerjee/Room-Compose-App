package com.example.wastesamaritanassignment1.model

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface ItemDao {

    @Upsert
    suspend fun upsert(item: Item)

    @Query("SELECT * FROM item WHERE id = :id")
    fun getItem(id: Int): Item

    @Query("SELECT id,name,quantity,rating,remarks FROM item WHERE id = :id")
    fun getItemShortened(id: Int): ItemDetailsShortened

    @Query("SELECT id,name,quantity,rating,remarks FROM item ORDER BY name COLLATE NOCASE")
    fun getItemsByNameAsc(): List<ItemDetailsShortened>

    @Query("SELECT id,name,quantity,rating,remarks FROM item ORDER BY name COLLATE NOCASE DESC")
    fun getItemsByNameDesc(): List<ItemDetailsShortened>

    @Query("SELECT id,name,quantity,rating,remarks FROM item ORDER BY rating COLLATE NOCASE")
    fun getItemsByRatingsAsc(): List<ItemDetailsShortened>

    @Query("SELECT id,name,quantity,rating,remarks FROM item ORDER BY rating COLLATE NOCASE DESC")
    fun getItemsByRatingsDesc(): List<ItemDetailsShortened>

}