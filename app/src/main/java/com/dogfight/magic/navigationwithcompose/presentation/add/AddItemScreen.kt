package com.dogfight.magic.navigationwithcompose.presentation.add

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.dogfight.magic.R
import com.dogfight.magic.navigationwithcompose.presentation.actions.ActionScreen
import com.dogfight.magic.navigationwithcompose.presentation.components.ItemDetails
import com.dogfight.magic.navigationwithcompose.presentation.components.ItemDetailsState

@Composable
fun AddItemScreen(modifier: Modifier = Modifier) {
    val viewModel: AddItemViewModel = hiltViewModel()
    ActionScreen(delegate = viewModel, content = { (screenState, onExecuteAction) ->
        AddItemContent(screenState, onExecuteAction)
    })
}

@Composable
fun AddItemContent(
    state: AddItemViewModel.ScreenState,
    onAddItemClicked: (String) -> Unit
) {
    ItemDetails(
        state = ItemDetailsState(
            loadedItem = "",
            texFieldPlaceholder = stringResource(id = R.string.app_name),
            actionButtonText = stringResource(R.string.app_name),
            isActionInProgress = state.isProgressVisible,
        ),
        onActionButtonClicked = onAddItemClicked,
        modifier = Modifier.fillMaxSize()
    )

}

@Composable
@Preview(showSystemUi = true)
fun ItemsScreenPreview() {
    AddItemContent(state = AddItemViewModel.ScreenState(), {})
}
