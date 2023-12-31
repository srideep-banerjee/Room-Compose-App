package com.example.wastesamaritanassignment1.viewmodel

import android.app.Application
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.wastesamaritanassignment1.model.Item
import com.example.wastesamaritanassignment1.model.ItemDatabase
import com.example.wastesamaritanassignment1.model.ItemRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date

class ItemDetailsViewModel(application: Application, val id: Int?): AndroidViewModel(application) {
    private lateinit var _database: ItemDatabase

    private lateinit var repository: ItemRepository

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    var item: Item? = null
        private set

    val database: ItemDatabase
        get() {
            while (!this::_database.isInitialized);
            return _database
        }

    fun save(images: List<String>, name: String, quantity: Int, ratings: Int, remarks: String?, onComplete: ()->Unit) {
        repository.upsertItem(Item(id, name, quantity, ratings, remarks, images)){
            onComplete()
        }
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _database = ItemDatabase.getDatabase(getApplication<Application>().applicationContext)
            repository = ItemRepository(database.itemDao())

            if (id != null) {
                repository.loadItem(id) {
                    item = it
                    viewModelScope.launch { _isLoading.emit(false) }
                }
            } else {
                viewModelScope.launch { _isLoading.emit(false) }
            }
        }
    }
}