package com.dogfight.magic.game_ui.radar.upravlenie.buttons

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun HomingRocketButton(
    modifier: Modifier= Modifier,
    canLock: Boolean,
    onClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            /*painter = painterResource(R.drawable.ic_hard_layer),*/
            imageVector = Icons.Default.Send,
            contentDescription = "Самонаводящаяся ракета",
            tint = if (canLock) Color.Green else Color.Gray,
            modifier = modifier
                .size(64.dp)
               // .clickable(/*enabled = canLock*/) { onClick() }
        )
        Text(
            text = "",
            color = if (canLock) Color.Green else Color.Gray
        )
    }
}