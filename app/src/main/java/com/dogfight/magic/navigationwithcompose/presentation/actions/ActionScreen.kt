package com.dogfight.magic.navigationwithcompose.presentation.actions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dogfight.magic.navigationwithcompose.EventConsumer
import com.dogfight.magic.navigationwithcompose.presentation.actions.ActionsViewModel.Delegate
import com.dogfight.magic.navigationwithcompose.presentation.components.LoadResultContent
import com.dogfight.magic.navigationwithcompose.presentation.composition_local.LocalNavController
import com.dogfight.magic.navigationwithcompose.presentation.routes.routeClass

data class ActionContentState<State, Action>(
    val state: State,
    val onExecuteAction: (Action) -> Unit
)

@Composable
fun <State, Action> ActionScreen(
    delegate: Delegate<State, Action>,
    content: @Composable (ActionContentState<State, Action>) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel = viewModel<ActionsViewModel<State, Action>> { ActionsViewModel(delegate) }
    val navController = LocalNavController.current
    val rememberedScreenRout = remember {
        navController.currentBackStackEntry.routeClass()
    }
    EventConsumer(chanel = viewModel.exitChannel) {
        if (rememberedScreenRout == navController.currentBackStackEntry.routeClass()) {
            navController.popBackStack()
        }
    }
    val loadResult by viewModel.stateFlow.collectAsState()

    LoadResultContent(
        loadResult = loadResult,
        content = { state ->
            val actionContentState =
                ActionContentState(state = state, onExecuteAction = viewModel::execute)
            content(actionContentState)
        },
        modifier = modifier
    )
}