package com.dogfight.magic.navigationwithcompose.presentation.items

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dogfight.magic.navigationwithcompose.data.LoadResult
import com.dogfight.magic.navigationwithcompose.domain.ItemsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ItemsViewModel @Inject constructor(val repository: ItemsRepository) :
    ViewModel() {
    val stateFlow: StateFlow<LoadResult<ScreenState>> =
        repository.getItems().map { LoadResult.Success(ScreenState(it)) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Lazily,
                initialValue = LoadResult.Loading,
            )

    data class ScreenState(val items: List<String>)
}