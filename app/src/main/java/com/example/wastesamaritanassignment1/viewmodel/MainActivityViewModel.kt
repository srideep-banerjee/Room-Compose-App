package com.example.wastesamaritanassignment1.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.wastesamaritanassignment1.model.ItemDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivityViewModel(application: Application): AndroidViewModel(application) {
    private lateinit var _database: ItemDatabase

    val database: ItemDatabase get() {
        while (!this::_database.isInitialized);
        return _database
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _database = ItemDatabase.getDatabase(getApplication<Application>().applicationContext)
        }
    }

}