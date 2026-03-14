package com.dogfight.magic.navigationwithcompose.presentation.edid

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.dogfight.magic.R
import com.dogfight.magic.navigationwithcompose.presentation.actions.ActionScreen
import com.dogfight.magic.navigationwithcompose.presentation.components.ItemDetails
import com.dogfight.magic.navigationwithcompose.presentation.components.ItemDetailsState

@Composable
fun EditItemScreen(id: Int) {
    val viewModel =
        hiltViewModel<EditItemViewModel, EditItemViewModel.Factory> { factory -> factory.create(id) }
    ActionScreen(
        delegate = viewModel,
        content = { (screenState, onExecuteAction) ->
            EditItemContent(
                screenState,
                onExecuteAction
            )
        })
}

@Composable
private fun EditItemContent(
    state: EditItemViewModel.ScreenState,
    onEditButtonClicked: (String) -> Unit
) {

    ItemDetails(
        state = ItemDetailsState(
            loadedItem = state.loadedItem,
            texFieldPlaceholder = stringResource(id = R.string.app_name),
            actionButtonText = stringResource(R.string.app_name),
            isActionInProgress = state.isEditInProcess,
        ),
        onActionButtonClicked = onEditButtonClicked,
        modifier = Modifier.fillMaxSize()
    )
}