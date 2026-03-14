package com.dogfight.magic.navigationwithcompose.presentation.items

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dogfight.magic.navigationwithcompose.data.LoadResult
import com.dogfight.magic.navigationwithcompose.presentation.components.LoadResultContent
import com.dogfight.magic.navigationwithcompose.presentation.composition_local.LocalNavController
import com.dogfight.magic.navigationwithcompose.presentation.routes.HomeGraph.EditItemRout


@Composable
fun ItemsScreen(modifier: Modifier = Modifier) {
    val navController = LocalNavController.current
    val viewModel: ItemsViewModel = hiltViewModel()
    val screenState = viewModel.stateFlow.collectAsState()
    ItemsContent(
        navToEdit = { id -> navController.navigate(EditItemRout(id = id)) },
        getLoadResult = { screenState.value },
    )
}

@Composable
fun ItemsContent(
    navToEdit: (id: Int) -> Unit,
    getLoadResult: () -> LoadResult<ItemsViewModel.ScreenState>,
) {
    LoadResultContent(loadResult = getLoadResult(), content = { screenState ->
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            itemsIndexed(screenState.items) { index, item ->
                Text(
                    item,
                    modifier = Modifier
                        .padding(12.dp)
                        .fillParentMaxWidth()
                        .clickable { navToEdit(index) })
            }
        }
    })
}

@Composable
@Preview(showSystemUi = true)
fun ItemsScreenPreview() {
    ItemsContent(
        getLoadResult = { LoadResult.Loading },
        navToEdit = {})
}
