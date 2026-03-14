package com.dogfight.magic.home_screen.home_widgets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.dogfight.magic.navigationwithcompose.presentation.composition_local.LocalNavController
import com.dogfight.magic.navigationwithcompose.presentation.routes.HomeGraph
import com.dogfight.magic.R

@OptIn(ExperimentalFoundationApi::class, ExperimentalSharedTransitionApi::class)
//@Preview(showBackground = true)
@Composable
fun ModeButtonsGrid() {
    var selectedButton by remember { mutableStateOf<ModeButtonData?>(null) }
    val navController = LocalNavController.current
    val context = LocalContext.current
    SharedTransitionLayout(modifier = Modifier.fillMaxSize()) {
        LazyVerticalGrid(
            modifier = Modifier.padding(/*top = 24.dp*/),
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(getButtonsData(context = context).size) { index ->
                val button = getButtonsData(context = context)[index]
                AnimatedVisibility(
                    visible = button != selectedButton,
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut(),
                    modifier = Modifier.animateItem()
                ) {
                    Box(
                        modifier = Modifier
                            .sharedBounds(
                                sharedContentState = rememberSharedContentState(key = "${button.id}-bounds"),
                                animatedVisibilityScope = this,
                                clipInOverlayDuringTransition = OverlayClip(RoundedCornerShape(12.dp))
                            )
                            .background(Color.White, RoundedCornerShape(12.dp))
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        ButtonCardItem(
                            buttonData = button,
                            modifier = Modifier.sharedElement(
                                state = rememberSharedContentState(key = button.id),
                                animatedVisibilityScope = this@AnimatedVisibility
                            ),
                            onClick = {
                                when (button.id) {
                                    1 -> navController.navigate(HomeGraph.GameRout(gameLevel = 0))
                                    else -> {
                                        selectedButton = button
                                    }
                                }
                            },
                            applyHue = false
                        )
                    }
                }
            }

            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(Modifier.height(40.dp))
                    ContactBar(
                        email = "magicaldogfight@gmail.com",
                        /*tiktokUrl = "https://www.tiktok.com/@yourappdemo",*/
                        tiktokUrl = "https://www.tiktok.com/@magical.dogfight?_t=ZM-8wZC21HGEWT&_r=1",
                        playStoreUrl = "https://play.google.com/store/apps/details?id=com.dogfight.magic",
                        telegramUrl = "https://t.me/magicaldogfight", // можно null
                        instagramUrl = null, // можно null
                        modifier = Modifier.padding(start = 16.dp, bottom = 4.dp)
                    )
                    val composition by rememberLottieComposition(
                        LottieCompositionSpec.Asset("animation_scales.json")
                    )
                    val progress by animateLottieCompositionAsState(
                        composition,
                        iterations = LottieConstants.IterateForever
                    )
                /*    Row(horizontalArrangement = Arrangement.Center) {
                       *//* LottieAnimation(
                            composition = composition,
                            progress = { progress },
                            modifier = Modifier.fillMaxWidth()
                                .aspectRatio(1f)
                                .let { baseModifier ->
                                     baseModifier
                                },
                        )*//*
                        Text(stringResource(R.string.comments_and_suggestions), fontSize = 8.sp)
                    }*/
                }

            }
        }
        ButtonDetailCardItem(
            buttonData = selectedButton,
            onDismiss = { selectedButton = null },
        )
    }
}





