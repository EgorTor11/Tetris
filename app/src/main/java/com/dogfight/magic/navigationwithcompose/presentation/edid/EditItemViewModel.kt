package com.dogfight.magic.navigationwithcompose.presentation.edid

import androidx.lifecycle.ViewModel
import com.dogfight.magic.navigationwithcompose.domain.ItemsRepository
import com.dogfight.magic.navigationwithcompose.presentation.actions.ActionsViewModel.Delegate
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel(assistedFactory = EditItemViewModel.Factory::class)
class EditItemViewModel @AssistedInject constructor(
    @Assisted private val id: Int,
    private val itemsRepository: ItemsRepository,
) : ViewModel(), Delegate<EditItemViewModel.ScreenState, String> {


    data class ScreenState(
        val loadedItem: String,
        val isEditInProcess: Boolean = false
    )

    @AssistedFactory
    interface Factory {
        fun create(id: Int): EditItemViewModel
    }

    override suspend fun loadState(): ScreenState {
        return ScreenState(itemsRepository.getItemById(id))
    }

    override suspend fun execute(action: String) {
        itemsRepository.updateItem(id, action)
    }

    override fun showProgress(input: ScreenState): ScreenState {
        return input.copy(isEditInProcess = true)
    }
}