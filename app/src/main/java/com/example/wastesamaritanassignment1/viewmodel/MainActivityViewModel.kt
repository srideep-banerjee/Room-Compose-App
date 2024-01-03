package com.example.wastesamaritanassignment1.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.wastesamaritanassignment1.model.ItemDatabase
import com.example.wastesamaritanassignment1.model.ItemDetailsShortened
import com.example.wastesamaritanassignment1.model.ItemOrder
import com.example.wastesamaritanassignment1.model.ItemRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainActivityViewModel(application: Application): AndroidViewModel(application) {
    private lateinit var _database: ItemDatabase

    private lateinit var repository: ItemRepository

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _itemList = mutableStateListOf<ItemDetailsShortened>()
    val itemList = _itemList

    var sortOrder = ItemOrder.NAME_ASC
        private set

    val database: ItemDatabase get() {
        while (!this::_database.isInitialized);
        return _database
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _database = ItemDatabase.getDatabase(getApplication<Application>().applicationContext)

            repository = ItemRepository(database.itemDao())
            repository.loadItems(itemOrder = ItemOrder.NAME_ASC) {
                itemList.clear()
                itemList.addAll(it)

                viewModelScope.launch(Dispatchers.Main) {
                    _isLoading.emit(false)
                }
            }
        }
    }

    fun updateSortOrder(itemOrder: ItemOrder) {
        sortOrder = itemOrder
        if(_isLoading.value) return
        viewModelScope.launch(Dispatchers.Main) {
            _isLoading.emit(true)
        }
        repository.loadItems(itemOrder = itemOrder) {
            itemList.clear()
            itemList.addAll(it)
            viewModelScope.launch(Dispatchers.Main) {
                _isLoading.emit(false)
            }
        }
    }

}