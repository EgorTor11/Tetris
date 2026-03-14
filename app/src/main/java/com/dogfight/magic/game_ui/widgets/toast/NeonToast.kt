package com.dogfight.magic.game_ui.widgets.toast
import androidx.annotation.DrawableRes
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import com.dogfight.magic.R


@Composable
fun NeonToast(
    message: String,
    @DrawableRes iconResMain: Int= R.drawable.img_hard,
   // @DrawableRes iconResInfo: Int,
    visible: Boolean,
    onDismiss: () -> Unit,
    duration: Long = 2000L
) {
    // Автоматическое скрытие тоста после заданной задержки
    LaunchedEffect(visible) {
        if (visible) {
            delay(duration)
            onDismiss()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn() + slideInVertically { it },
            exit = fadeOut() + slideOutVertically { it }
        ) {
            Box(
                modifier = Modifier
                    .padding(bottom = 48.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFF00FFF0), Color(0xFF0055FF))
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .border(
                        width = 2.dp,
                        color = Color.Cyan,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 5.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = iconResMain),
                        contentDescription = null,
                        modifier = Modifier
                            .size(50.dp)
                            .padding(end = 8.dp)
                    )
                    Text(
                        text = message,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
/*                    Image(
                        painter = painterResource(id = iconResInfo),
                        contentDescription = null,
                        modifier = Modifier
                            .size(50.dp)
                            .padding(end = 8.dp)
                    )*/
                }
            }
        }
    }
}
