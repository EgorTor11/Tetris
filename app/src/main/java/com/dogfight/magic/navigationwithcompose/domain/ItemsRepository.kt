package com.dogfight.magic.navigationwithcompose.domain

import kotlinx.coroutines.flow.Flow


interface ItemsRepository {
    suspend fun addItem(title: String)
    fun getItems(): Flow<List<String>>
    suspend fun updateItem(id: Int, newTitle: String)
    suspend fun getItemById(id: Int): String
}