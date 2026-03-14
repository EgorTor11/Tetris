package com.dogfight.magic.game_ui.radar.upravlenie.super_container

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dogfight.magic.R
import com.dogfight.magic.game_ui.radar.mvi_radar.RadarViewModel

data class ContentData(val id: Int, val name: String, val description: String, val image: Int)


@OptIn(ExperimentalFoundationApi::class, ExperimentalSharedTransitionApi::class)
//@Preview(showBackground = true)
@Composable
fun SharedContainerWithDrag(
    onClick: () -> Unit,
    prefsKey: String = "fire_button_prefs",
    portraitOffset: Offset = Offset.Zero,
    landscapeOffset: Offset = Offset.Zero,
    isElementDoubleTapNeed: Boolean = false,
    isElementEasyOpenTap: Boolean = false,
    viewModel: RadarViewModel,
    detailContentData: ContentData = ContentData(
        1,
        "Джойстик ",
        "Это элемент управления курсом вашего летательного существа!",
        R.drawable.img_joystick_black
    ),
    detailContent: @Composable ((Modifier, l: () -> Unit) -> Unit)? = null,
    content: @Composable (Modifier) -> Unit,
) {
    var selectedContentData by remember { mutableStateOf<ContentData?>(null) }
    var isNeonBorder by remember { mutableStateOf(false) }


    SharedTransitionLayout(modifier = Modifier.fillMaxSize()) {
        DragContainer(
            landscapeOffset = landscapeOffset,
            portraitOffset = portraitOffset,
            onClick = {
                if (isElementEasyOpenTap)
                    selectedContentData = detailContentData
                else
                    onClick.invoke()
            },
            prefsKey = prefsKey,
            onDoubleTap = {
                if (isElementDoubleTapNeed) {
                    onClick.invoke() // selectedContentData = contentData // здесь по двойному тапу показ диалога , пока решил что только по тапу двумя пальцами оставить
                } else {
                    onClick.invoke()
                }
            },
        ) { contentModifier ->
            AnimatedVisibility(
                visible = detailContentData != selectedContentData,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut(),
            ) {
                Box(
                    modifier = Modifier
                        .sharedBounds(
                            sharedContentState = rememberSharedContentState(key = "${detailContentData.id}-bounds"),
                            animatedVisibilityScope = this,
                            clipInOverlayDuringTransition = OverlayClip(RoundedCornerShape(12.dp))
                        )
                        .background(Color.Transparent, RoundedCornerShape(12.dp))
                        .clip(RoundedCornerShape(12.dp))
                    /*.border(border = BorderStroke(2.dp, borderColor))*/,
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .sharedElement(
                                state = rememberSharedContentState(key = detailContentData.id),
                                animatedVisibilityScope = this@AnimatedVisibility
                            )
                            .pointerInput(Unit) {
                                forEachGesture {
                                    awaitPointerEventScope {
                                        val firstDown = awaitFirstDown()

                                        // Ждём второго пальца (второго поинтера)
                                        var secondPointerAdded = false
                                        do {
                                            val event = awaitPointerEvent()
                                            if (event.changes.size >= 2) {
                                                secondPointerAdded = true
                                            }
                                        } while (!secondPointerAdded)

                                        // Ждём отпускания обоих пальцев
                                        val allUp = waitForUpOrCancellation()

                                        // Если оба отпустили почти одновременно — считаем это двойным тапом двумя пальцами
                                        if (allUp != null && !allUp.pressed) {
                                            // onDoubleTapTwoFingers?.invoke()
                                            selectedContentData = detailContentData
                                        }
                                    }
                                }
                                detectTapGestures(
                                    onDoubleTap = {
                                        // Обработка двойного тапа
                                        //   selectedContentData = contentData
                                    },
                                    onTap = {
                                        // Обычный тап, если нужен
                                        //  onClick.invoke()
                                        //  selectedContentData = contentData
                                    },
                                    onLongPress = {

                                    },
                                    onPress = {

                                    }
                                )
                            }
                    ) {
                        content.invoke(contentModifier)
                    }
                }
            }
        }

        ElementDetailCardItem(
            viewModel = viewModel,
            contentData = selectedContentData,
            content = content,
            onDismiss = { selectedContentData = null },
            prefsKey = prefsKey,
            detailContent = detailContent
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.ElementDetailCardItem(
    prefsKey: String = "fire_button_prefs",
    viewModel: RadarViewModel,
    contentData: ContentData?,
    modifier: Modifier = Modifier,
    detailContent: (@Composable (Modifier, () -> Unit) -> Unit)? = null,
    content: @Composable (Modifier) -> Unit,
    onDismiss: () -> Unit,
) {
    // Анимация для неонового переливания
    val transition = rememberInfiniteTransition()
    val borderColor by transition.animateColor(
        /*initialValue = Color(0xFF00FF00),*/ // Начальный цвет (зеленый)
        /*targetValue = Color(0xFFFF00FF),*/ // Целевой цвет (пурпурный)
        initialValue = Color(0xFF00FFF0),
        targetValue = Color(0xFF0055FF),
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 2000, // длительность анимации 2 секунды
                easing = FastOutSlowInEasing // плавный переход
            ),
            repeatMode = RepeatMode.Reverse // повтор с обратным эффектом
        )
    )
    AnimatedContent(
        modifier = modifier,
        targetState = contentData,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "DetailCardItem"
    ) { data ->


        // Измерение ширины LazyRow для центрирования
        var rowWidth by remember { mutableStateOf(0) }
        val screenWidth = LocalConfiguration.current.screenWidthDp.dp
        val rowWidthDp = with(LocalDensity.current) { rowWidth.toDp() }
        val horizontalOffset by animateDpAsState(
            targetValue = (screenWidth - rowWidthDp) / 2,
            animationSpec = tween(700, easing = FastOutSlowInEasing),
            label = "rowOffset"
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .animateContentSize(
                    animationSpec = tween(
                        durationMillis = 1000, // Увеличь время — будет плавнее
                        easing = FastOutSlowInEasing // Эффект «ускорение → замедление»
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            if (data != null) {
                val gradientBrush = Brush.verticalGradient(
                    colorStops = arrayOf(
                        0.0f to Color.Transparent,
                        0.5f to Color.Black.copy(alpha = 0.6f),
                        1.0f to Color.Transparent
                    )
                )
                // Blur
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { onDismiss() }
                        .background(
                            brush = gradientBrush
                        ),
                )

                Column(
                    modifier = Modifier
                        .offset(x = horizontalOffset)
                        .onGloballyPositioned {
                            rowWidth = it.size.width
                        }
                        .padding(16.dp)
                        .sharedBounds(
                            sharedContentState = rememberSharedContentState(key = "${data.id}-bounds"),
                            animatedVisibilityScope = this@AnimatedContent,
                            clipInOverlayDuringTransition = OverlayClip(RoundedCornerShape(14.dp))
                        )
                        .padding(bottom = 48.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF00FFF0).copy(alpha = 0.7f),
                                    Color(0xFF0055FF).copy(alpha = 0.7f)
                                )
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .border(
                            border = BorderStroke(2.dp, borderColor),
                            shape = RoundedCornerShape(16.dp)
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .padding(top = 10.dp)//.align(Alignment.CenterHorizontally)
                            .animateContentSize(
                                animationSpec = tween(
                                    durationMillis = 1000, // Увеличь время — будет плавнее
                                    easing = FastOutSlowInEasing // Эффект «ускорение → замедление»
                                )
                            )
                            .sharedElement(
                                state = rememberSharedContentState(key = data.id),
                                animatedVisibilityScope = this@AnimatedContent
                            )
                        /*.clickable(onClick = {  onDismiss.invoke() })*/,
                        contentAlignment = Alignment.Center
                    ) {

                        // здесь контент
                        /*    if (prefsKey == "fire_button_prefs")
                                NeonButtonsRow(viewModel, onElementClick = {
                                    viewModel.onFireButtonSelected(it)
                                    onDismiss.invoke()
                                }, content)
                            else*/ if (detailContent != null) detailContent(Modifier, onDismiss)
                    else content(Modifier)

                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = data.description,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = Color.White
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text(stringResource(R.string.select_photo), color = Color.Red)
                        }
                        TextButton(onClick = onDismiss) {
                            Text(stringResource(R.string.close), color = Color.Red)
                        }
                    }
                }
            }
        }
    }
}




