package com.example.wastesamaritanassignment1.viewmodel

import android.app.Application
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.wastesamaritanassignment1.model.Item
import com.example.wastesamaritanassignment1.model.ItemDatabase
import kotlinx.coroutines.Dispatchers
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

class ItemDetailsViewModel(application: Application, id: Int?): AndroidViewModel(application) {
    private lateinit var _database: ItemDatabase

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    var item: Item? = null
        private set

    val database: ItemDatabase
        get() {
            while (!this::_database.isInitialized);
            return _database
        }

    fun copyFileAsync(filesDir: String, sourceInputStream: InputStream, onComplete: (String)->Unit){
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val path = "${filesDir}${File.separator}$imageFileName.jpg"
        val image = File(path)
        viewModelScope.launch(Dispatchers.IO) {
            sourceInputStream.use {source ->
                FileOutputStream(image).use {destination ->
                    val byteArray = source.readBytes()
                    destination.write(byteArray)
                }
            }
            withContext(Dispatchers.Main) {
                onComplete(path)
            }
        }
    }

    fun save(images: List<String>, name: String, quantity: Int, ratings: Int, remarks: String?) {

    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _database = ItemDatabase.getDatabase(getApplication<Application>().applicationContext);_isLoading
            if (id != null) {
                item = database.itemDao().getItem(id)
            }
            _isLoading.emit(false)
        }
    }
}