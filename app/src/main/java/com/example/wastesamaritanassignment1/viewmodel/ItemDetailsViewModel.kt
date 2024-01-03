package com.example.wastesamaritanassignment1.viewmodel

import android.app.Application
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.wastesamaritanassignment1.model.FileTaskType
import com.example.wastesamaritanassignment1.model.FileWorker
import com.example.wastesamaritanassignment1.model.Item
import com.example.wastesamaritanassignment1.model.ItemDatabase
import com.example.wastesamaritanassignment1.model.ItemRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ItemDetailsViewModel(application: Application,private val id: Int?): AndroidViewModel(application) {
    private lateinit var _database: ItemDatabase

    private lateinit var repository: ItemRepository

    private lateinit var workManager: WorkManager

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    var item: MutableState<Item?> = mutableStateOf(null)
        private set

    val database: ItemDatabase
        get() {
            while (!this::_database.isInitialized);
            return _database
        }

    val imagesState = mutableStateListOf<String>()
    val newImagesState = mutableStateMapOf<String,Boolean>()

    fun save(images: List<String>, name: String, quantity: Int, ratings: Int, remarks: String?, onComplete: ()->Unit) {
        newImagesState.clear()
        val newItem = Item(id, name, quantity, ratings, remarks, images)
        repository.upsertItem(newItem){
            item.value = newItem
            onComplete()
        }
    }

    fun deleteFile(path: String) {
        val deleteFileRequest = OneTimeWorkRequestBuilder<FileWorker>()
            .setInputData(
                workDataOf(
                    FileWorker.FILE_TASK_KEY to FileTaskType.DELETE.name,
                    FileWorker.FILE_PATH_KEY to path
                )
            )
            .setConstraints(Constraints.NONE)
            .build()
        workManager.enqueue(deleteFileRequest)
    }

    fun deleteAddedFiles(paths: List<String>) {
        if(paths.size == 1) {
            deleteFile(paths[0])
            return
        }
        if (paths.isNotEmpty()) {
            val deleteFileRequest = OneTimeWorkRequestBuilder<FileWorker>()
                .setInputData(
                    workDataOf(
                        FileWorker.FILE_TASK_KEY to FileTaskType.DELETE_LIST.name,
                        FileWorker.FILE_PATH_LIST_KEY to paths.toTypedArray()
                    )
                )
                .setConstraints(Constraints.NONE)
                .build()
            workManager.enqueue(deleteFileRequest)
        }
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _database = ItemDatabase.getDatabase(getApplication<Application>().applicationContext)
            repository = ItemRepository(database.itemDao())
            workManager = WorkManager.getInstance(getApplication<Application>().applicationContext)

            if (id != null) {
                repository.loadItem(id) {
                    item.value = it
                    imagesState.addAll(it.images)
                    viewModelScope.launch { _isLoading.emit(false) }
                }
            } else {
                viewModelScope.launch { _isLoading.emit(false) }
            }
        }
    }
}