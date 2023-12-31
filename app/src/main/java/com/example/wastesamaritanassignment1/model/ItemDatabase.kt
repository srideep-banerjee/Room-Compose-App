package com.example.wastesamaritanassignment1.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Item::class], version = 1, exportSchema = false)
@TypeConverters(ListStringConverter::class)
abstract class ItemDatabase: RoomDatabase() {

    abstract fun itemDao(): ItemDao

    companion object {
        @Volatile
        private var instance: ItemDatabase? = null

        fun getDatabase(context: Context): ItemDatabase {
            synchronized(this) {
                return instance ?:
                Room.databaseBuilder(context, ItemDatabase::class.java, "item_database")
                    .build()
                    .also { instance = it }
            }
        }
    }
}