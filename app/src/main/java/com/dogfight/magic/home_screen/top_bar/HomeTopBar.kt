package com.dogfight.magic.home_screen.top_bar

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dogfight.magic.home_screen.shop.shop_top_bar.InventoryBar
import com.dogfight.magic.home_screen.shop.shop_top_bar.InventoryItem
import com.dogfight.magic.home_screen.shop.shop_top_bar.ShopViewModel
import com.dogfight.magic.home_screen.shop.shop_top_bar.getInventoryItems

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.HomeTopAppBar(
    viewModel: ShopViewModel,
    selectedItem: InventoryItem?,
    isVoiceEnabled: Boolean,
    onVoiceToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    onItemClick: (InventoryItem) -> Unit,
) {
    val context = LocalContext.current
    TopAppBar(
        modifier = modifier.shadow(6.dp), // ← вот тень,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = /*Color(0xFFF4ECE2),*/ Color.White,
            titleContentColor = Color(0xFF3E3E3E),
        ),
        title = {
            Text(
                text = "",
                fontSize = 20.sp
            )
        },
        actions = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier) {
                    InventoryBar(
                        viewModel = viewModel,
                        selectedItem = selectedItem,
                        getInventoryItems(context = context),
                        onItemClick = {
                            onItemClick(it)
                        })
                }
            }
        }
    )
}
