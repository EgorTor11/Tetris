package com.dogfight.magic.navigationwithcompose.presentation.add

import androidx.lifecycle.ViewModel
import com.dogfight.magic.navigationwithcompose.domain.ItemsRepository
import com.dogfight.magic.navigationwithcompose.presentation.actions.ActionsViewModel.Delegate
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddItemViewModel @Inject constructor(private val repository: ItemsRepository) : ViewModel(),
    Delegate<AddItemViewModel.ScreenState, String> {


    data class ScreenState(val isProgressVisible: Boolean = false)

    override suspend fun loadState(): ScreenState {
        return ScreenState()
    }

    override suspend fun execute(action: String) {
        repository.addItem(action)
    }

    override fun showProgress(input: ScreenState): ScreenState {
        return input.copy(isProgressVisible = true)
    }

}