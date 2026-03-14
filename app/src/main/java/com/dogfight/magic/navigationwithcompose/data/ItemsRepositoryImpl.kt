package com.dogfight.magic.navigationwithcompose.data

import com.dogfight.magic.navigationwithcompose.domain.ItemsRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ItemsRepositoryImpl @Inject constructor() : ItemsRepository {

    private val itemsFlow = MutableStateFlow(List(size = 5) { "Item ${it + 1}" })
    override suspend fun addItem(title: String) {
        delay(2000)
        itemsFlow.update { it + title }
    }

    override fun getItems(): Flow<List<String>> {
        return itemsFlow.onStart { delay(3000) }
    }


    override suspend fun updateItem(id: Int, newTitle: String) {
        delay(2000)
        itemsFlow.update { oldList ->
            oldList.toMutableList()
                .apply { runCatching { set(id, newTitle) }.getOrNull() ?: add(newTitle) }
        }
    }

    override suspend fun getItemById(id: Int): String {
        delay(1000)
        return runCatching { itemsFlow.value.get(id) }.getOrNull()
            ?: "Такого элемента нету!!!"
    }
}