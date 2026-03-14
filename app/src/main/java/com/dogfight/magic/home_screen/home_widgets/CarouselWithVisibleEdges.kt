package com.dogfight.magic.home_screen.home_widgets

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.dogfight.magic.R
import com.dogfight.magic.game_ui.radar.mvi_radar.game_mode.GameMode
import com.dogfight.magic.game_ui.widgets.WorkInProgressDialog
import com.dogfight.magic.home_screen.shop.MissionItemCard
import com.dogfight.magic.home_screen.shop.missionItems
import com.dogfight.magic.home_screen.shop.onlineStubItems
import com.dogfight.magic.navigationwithcompose.presentation.composition_local.LocalNavController
import com.dogfight.magic.navigationwithcompose.presentation.routes.HomeGraph.GameRout

@Composable
fun CarouselWithVisibleEdges(
    titleLambda: (pageIndex: Int) -> Unit = {},
    gameMode: GameMode = GameMode.TRAINING,
) {
    val navController = LocalNavController.current

    // Состояние пагинации
    val pagerState = rememberPagerState(
        initialPage = 0,  // Начальная страница
        pageCount = {
            when (gameMode) {
                GameMode.DOGFIGHT -> 5
                GameMode.ONLINE -> 1
                else -> 2
            }
        } // Общее количество карточек
    )

    // Горизонтальный пагер с настройками
    HorizontalPager(
        state = pagerState,
        modifier = Modifier
            .padding(vertical = 16.dp)// Отступы сверху снизу для пэйджера
            .fillMaxWidth(),
        pageSpacing = 0.dp, // Расстояние между карточками
        contentPadding = PaddingValues(horizontal = 60.dp) // Отступы внутри пагера
    ) { pageIndex ->
        val painterResourceImg = when (pageIndex) {
            0 -> painterResource(R.drawable.img_pilot_pingvin/*R.drawable.img_easy*/)
            1 -> painterResource(R.drawable.img_pilot_dac)
            2 -> painterResource(R.drawable.img_pilot_classic)
            3 -> painterResource(R.drawable.img_pilot_super_as)
            4 -> painterResource(R.drawable.img_pilot_vladik)
            else -> painterResource(R.drawable.img_pilot_pingvin)
        }
        val enemyLottieAsset = when (pageIndex + 1) {
            1 -> "animation_panda.json"
            2 -> "animation_dobraya_vedma.json"
            3 -> "animation_astronaut.json"
            4 -> "animation_bez_metly.json"
            5 -> "animation_alian.json"
            else -> "animation_bez_metly.json"
        }

        // Определяем размер и масштаб карточки в зависимости от ее положения
        val scale = when {
            pageIndex == pagerState.currentPage -> {
                titleLambda.invoke(pageIndex)
                1.2f
            } // Центральная карточка
            pageIndex == pagerState.currentPage - 1 || pageIndex == pagerState.currentPage + 1 -> 1.0f // Соседние карточки
            else -> 0.8f // Остальные карточки
        }

        Box(
            modifier = Modifier
                .width(200.dp) // Ширина карточки
                .height(200.dp) // Высота карточки
                .graphicsLayer(scaleX = scale, scaleY = scale) // Применяем масштаб
                .padding(8.dp)
        ) {
            // Определяем высоту тени в зависимости от положения карточки
            val elevation = when {
                pageIndex == pagerState.currentPage -> CardDefaults.cardElevation(
                    defaultElevation = 12.dp,
                    pressedElevation = 16.dp,
                    focusedElevation = 14.dp
                ) // Центральная карточка
                else -> CardDefaults.cardElevation(
                    defaultElevation = 12.dp,
                    pressedElevation = 16.dp,
                    focusedElevation = 14.dp
                ) // Остальные карточки
            }
            val isMissionMode = gameMode == GameMode.MISSIONS
            var showDialog by remember { mutableStateOf(false) }
            // Создаем карточку
            if (isMissionMode) {
                val item = missionItems[pageIndex % missionItems.size]
                MissionItemCard(item) {
                    // TODO: Обработка покупки
                    if (item.id == 1)
                        navController.navigate(
                            route = GameRout(
                                gameLevel = 11
                            )
                        ) else showDialog= true

                }
                if (showDialog) {
                    WorkInProgressDialog(onDismiss = { showDialog = false })
                }
            } else if (gameMode == GameMode.ONLINE){
                val item = onlineStubItems[pageIndex % onlineStubItems.size]
                MissionItemCard(item) {
                    // TODO: Обработка покупки

                }
            } else {
                Card(
                    modifier = Modifier
                        .clickable(onClick = {
                            navController.navigate(
                                route = GameRout(
                                    gameLevel = pageIndex + 1
                                )
                            )
                        })
                        .fillMaxSize()
                        .align(Alignment.Center),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = elevation
                ) {
                    val NightSky = Color(0xFF0D0D2B)      // тёмно-синий, почти чёрный
                    val DeepPurple = Color(0xFF1E1B4B)     // глубокий фиолетовый
                    val NeonBlue = Color(0xFF00CFFF)       // ярко-голубой неон
                    val ElectricPurple = Color(0xFF9D00FF) // неоновый фиолетовый
                    val MagentaGlow = Color(0xFFFF00E5)    // яркий магента
                    val infiniteTransition = rememberInfiniteTransition()
                    val hueMatrix = if (true) {
                        val hueRotation by infiniteTransition.animateFloat(
                            initialValue = 0f,
                            targetValue = 360f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(durationMillis = 4000, easing = LinearEasing),
                                repeatMode = RepeatMode.Restart
                            )
                        )
                        MatrixUtils.partialHueRotateMatrix(hueRotation, fraction = 0.3f)
                    } else {
                        ColorMatrix()
                    }
                    val brush = if (true) {
                        val holographicProgress by infiniteTransition.animateFloat(
                            initialValue = 0f,
                            targetValue = 1f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(durationMillis = 2000, easing = LinearEasing),
                                repeatMode = RepeatMode.Reverse
                            )
                        )
                        val offsetX =
                            with(LocalDensity.current) { 300.dp.toPx() } * holographicProgress
                        // Holog-Color Graph.
                        Brush.linearGradient(
                            colors = listOf(
                                NightSky,
                                DeepPurple,
                                NeonBlue,
                                ElectricPurple,
                                MagentaGlow
                                /*   Color(0xFFFF0000),
                                   Color(0xFFFFFF00),
                                   Color(0xFF00FF00),
                                   Color(0xFF00FFFF),
                                   Color(0xFF0000FF),
                                   Color(0xFFFF00FF),
                                   Color(0xFFFF0000)*/
                            ),
                            start = Offset(offsetX, 0f),
                            end = Offset(offsetX + 600f, 600f),
                            tileMode = androidx.compose.ui.graphics.TileMode.Mirror
                        )
                    } else {
                        null
                    }
                    Box(
                        modifier = Modifier
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        /* Color(0xFF00FFF0).copy(alpha = 1f),
                                         Color(0xFF0055FF).copy(alpha = 1f)*/
                                        Color.White,
                                        Color.White
                                    )
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )
                            /*.border(
                                border = BorderStroke(2.dp, Color.Green),
                                shape = RoundedCornerShape(16.dp)
                            )*/
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {

                        //   Text(text = "Card $pageIndex")

                        if (enemyLottieAsset != null) {
                            val composition by rememberLottieComposition(
                                LottieCompositionSpec.Asset(enemyLottieAsset)
                            )
                            val progress by animateLottieCompositionAsState(
                                composition,
                                iterations = LottieConstants.IterateForever
                            )
                            LottieAnimation(
                                composition = composition,
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f),
                            )
                        } else {
                            Image(
                                painter = /*painterResource(R.drawable.img)*/ painterResourceImg,
                                "",
                                modifier = Modifier.let { baseModifier ->
                                    if (true && brush != null) {
                                        baseModifier.drawWithContent {
                                            drawContent()
                                            drawRect(
                                                brush = brush,
                                                alpha = 0.3f,
                                                blendMode = BlendMode.Overlay
                                            )
                                        }
                                    } else baseModifier
                                },
                                colorFilter = ColorFilter.colorMatrix(hueMatrix)
                            )
                        }

                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewCarouselWithVisibleEdges() {
    CarouselWithVisibleEdges()
}

