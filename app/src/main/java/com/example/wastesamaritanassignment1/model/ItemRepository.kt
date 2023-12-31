package com.example.wastesamaritanassignment1.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ItemRepository(private val itemDao: ItemDao) {
    private val _items: MutableLiveData<List<ItemDetailsShortened>> = MutableLiveData(listOf())
    val items: LiveData<List<ItemDetailsShortened>> = _items

    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private var loadingJob: Job? = null

    fun loadItems(itemOrder: ItemOrder, onComplete: (List<ItemDetailsShortened>)->Unit) {
        loadingJob?.cancel()
        loadingJob = coroutineScope.launch(Dispatchers.IO) {
            val loadedItems = when(itemOrder) {
                ItemOrder.NAME_ASC -> itemDao.getItemsByNameAsc()
                ItemOrder.NAME_DESC -> itemDao.getItemsByNameDesc()
                ItemOrder.RATINGS_ASC -> itemDao.getItemsByRatingsAsc()
                ItemOrder.RATINGS_DESC -> itemDao.getItemsByRatingsDesc()
            }
            try {
                ensureActive()
                withContext(Dispatchers.Main) {
                    _items.value = loadedItems
                    onComplete(loadedItems)
                }
            } catch (_: CancellationException){}

        }
    }

    fun upsertItem(item: Item, onComplete: () -> Unit) {
        coroutineScope.launch(Dispatchers.IO) {
            itemDao.upsert(item)
            withContext(Dispatchers.Main) {
                onComplete()
            }
        }
    }

    fun loadItem(id: Int, onComplete: (Item) -> Unit) {
        coroutineScope.launch(Dispatchers.IO) {
            onComplete(itemDao.getItem(id))
        }
        itemDao.getItem(id)
    }
}