package com.dogfight.magic.navigationwithcompose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.withContext

@Composable
fun <T> EventConsumer(chanel: ReceiveChannel<T>, block: (T) -> Unit) {
    val blockState by rememberUpdatedState(block)
    LaunchedEffect(Unit) {
        withContext(Dispatchers.Main.immediate) {
            for (event in chanel) blockState(event)
        }
    }
}