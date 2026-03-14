package com.dogfight.magic

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.dogfight.magic.game_ui.radar.mvi_radar.TestGameScreen
import com.dogfight.magic.game_ui.radar.upravlenie.view_model.ControlViewModel
import com.dogfight.magic.game_ui.widgets.SoundManager
import com.dogfight.magic.game_ui.widgets.voice.SuperRequestMicPermission
import com.dogfight.magic.game_ui.widgets.voice.VoiceCommandListener
import com.dogfight.magic.game_ui.widgets.voice.VoiceSwitch
import com.dogfight.magic.home_screen.NeonHomeScreen
import com.dogfight.magic.home_screen.shop.shop_top_bar.InventoryItem
import com.dogfight.magic.home_screen.shop.shop_top_bar.ShopItemDetailCard
import com.dogfight.magic.home_screen.shop.shop_top_bar.ShopViewModel
import com.dogfight.magic.home_screen.top_bar.HomeTopAppBar
import com.dogfight.magic.navigationwithcompose.presentation.add.AddItemScreen
import com.dogfight.magic.navigationwithcompose.presentation.composition_local.LocalNavController
import com.dogfight.magic.navigationwithcompose.presentation.edid.EditItemScreen
import com.dogfight.magic.navigationwithcompose.presentation.routes.HomeGraph
import com.dogfight.magic.navigationwithcompose.presentation.routes.HomeGraph.AddItemRout
import com.dogfight.magic.navigationwithcompose.presentation.routes.HomeGraph.EditItemRout
import com.dogfight.magic.navigationwithcompose.presentation.routes.HomeGraph.GameRout
import com.dogfight.magic.navigationwithcompose.presentation.routes.HomeGraph.HomeRout
import com.dogfight.magic.navigationwithcompose.presentation.routes.MainTabs
import com.dogfight.magic.navigationwithcompose.presentation.routes.ProfileGraph
import com.dogfight.magic.navigationwithcompose.presentation.routes.ProfileGraph.ProfileRout
import com.dogfight.magic.navigationwithcompose.presentation.routes.SettingsGraph
import com.dogfight.magic.navigationwithcompose.presentation.routes.SettingsGraph.SettingsRout
import com.dogfight.magic.navigationwithcompose.presentation.routes.routeClass
import com.dogfight.magic.navigationwithcompose.ui.AppNavigationBar
import com.dogfight.magic.settings_screen.RadarSettingsScreen
import com.dogfight.magic.unity_ads.loadRewardedAd
import com.unity3d.ads.UnityAds
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var voiceCommandListener: VoiceCommandListener


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SoundManager.init(this)
        // Unity Ads инициализация
        UnityAds.initialize(this, "5859679", testMode = false)
        loadRewardedAd("Rewarded_Android")
        val viewModel: VoiceCommandViewModel by viewModels()
        voiceCommandListener = VoiceCommandListener(this, onCommandRecognized = { command ->
            viewModel.updateCommand(command)

            Toast.makeText(this, getString(R.string.recognized, command), Toast.LENGTH_SHORT)
                .show()
        })

        enableEdgeToEdge()
        setContent {
            // MainGameScreen(viewModel)
            NavApp(voiceCommandListener)
        }
    }

    override fun onResume() {
        super.onResume()
        voiceCommandListener.startListening()
        // Прячем статус-бар и нав-бар
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            // Android 10
            /*            lifecycleScope.launch {
                             delay(300)
                             WindowCompat.setDecorFitsSystemWindows(window, true )
                             WindowInsetsControllerCompat(window, window.decorView).apply {
                                 hide(WindowInsetsCompat.Type.systemBars()) // Скрыть и статус и нав-бар
                                 systemBarsBehavior =
                                     WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                             }
                         }*/
        }
    }

    override fun onPause() {
        super.onPause()
        voiceCommandListener.stopListening() // Остановка прослушивания
    }

}

@OptIn(ExperimentalSharedTransitionApi::class)
@SuppressLint("ContextCastToActivity")
@Composable
fun NavApp(voiceCommandListener: VoiceCommandListener) {
    val shopViewModel= hiltViewModel<ShopViewModel>()
    val navController = rememberNavController()
    var isVoiceEnabled by remember { mutableStateOf(false) }
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry?.destination
    // 👇 Здесь перечисли маршруты, где BottomBar должен быть видим
    val bottomBarRoutes = listOf(
        HomeRout::class,
        SettingsRout::class,
        ProfileRout::class
    )
    SuperRequestMicPermission { granted ->
        isVoiceEnabled = true
    }
    LaunchedEffect(isVoiceEnabled) {
        if (isVoiceEnabled)
            voiceCommandListener.startListening()
        else voiceCommandListener.stopListening()

    }
    // 👇 Проверка: нужно ли показывать BottomBar
    var selectedItem by remember { mutableStateOf<InventoryItem?>(null) }
    val showCustomBars = currentDestination?.routeClass() in bottomBarRoutes
    SharedTransitionLayout {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            /*floatingActionButton = {
                if (currentBackStackEntry.routeClass() == HomeRout::class)
                    Card(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .width(56.dp)
                            .height(56.dp),
                        elevation = CardDefaults.cardElevation(6.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(
                            modifier = Modifier,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isVoiceEnabled) Icons.Default.Mic else Icons.Default.MicOff,
                                contentDescription = "Voice Command",
                                tint = if (isVoiceEnabled) Color(0xFF00796B) else Color.Gray
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Switch(
                                modifier = Modifier,
                                checked = isVoiceEnabled,
                                onCheckedChange = { *//*onVoiceToggle(it)*//*
                                isVoiceEnabled = it
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFF00796B),
                                uncheckedThumbColor = Color.LightGray
                            )
                        )
                    }
                }
            *//*                FloatingActionButton(onClick = { navController.navigate(AddItemRout) }) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    "add"
                                )
                            }*//*
        }*/
            floatingActionButton = {
                if (currentBackStackEntry.routeClass() == HomeRout::class) {
                    Box(contentAlignment = Alignment.Center) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                        ) {
                            VoiceSwitch(
                                checked = isVoiceEnabled,
                                onCheckedChange = { isVoiceEnabled = it }
                            )
                            //  Spacer(Modifier.height(4.dp))
                            val color = if (isVoiceEnabled) Color(0xFF00796B) else Color.Gray
                            Text(
                                text = stringResource(R.string.voice_control),
                                fontSize = 10.sp,
                                color = color,
                                modifier = Modifier,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                }
            },
            topBar = {
                if (showCustomBars)
                    HomeTopAppBar(
                        viewModel = shopViewModel ,
                        selectedItem = selectedItem,
                        isVoiceEnabled = isVoiceEnabled,
                        onVoiceToggle = { isVoiceEnabled = it },
                        onItemClick = { selectedItem = it }
                    )
            },
            bottomBar = {
                if (showCustomBars)
                    AppNavigationBar(
                        navController = navController,
                        tabs = MainTabs,

                        )
            }) { innerPadding ->
            CompositionLocalProvider(
                LocalNavController provides navController
            ) {
                val intentHost = (LocalContext.current as Activity).intent.data?.host
                val startDestination: Any = when (intentHost) {
                    "settings" -> SettingsGraph
                    "items" -> HomeGraph
                    else -> ProfileGraph
                }
                val mainViewModel = hiltViewModel<ControlViewModel>()
                SharedTransitionLayout(modifier = Modifier.fillMaxSize()) {
                    NavHost(
                        navController = navController,
                        startDestination = HomeGraph,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(/*innerPadding*/),
                    ) {
                        navigation<HomeGraph>(startDestination = HomeRout) {
                            composable<HomeRout> {
                                NeonHomeScreen(modifier = Modifier.padding(innerPadding))
                            }
                            composable<AddItemRout> { AddItemScreen() }
                            composable<EditItemRout>(deepLinks = listOf(EditItemRout.Link)) { navBackStackEntry ->
                                val rout: EditItemRout = navBackStackEntry.toRoute()
                                EditItemScreen(id = rout.id)
                            }
                            composable<GameRout>(deepLinks = listOf(GameRout.Link)) { navBackStackEntry ->
                                val rout: GameRout = navBackStackEntry.toRoute()
                                  //  GameScreen(gameLevel = rout.gameLevel)
                                TestGameScreen(gameLevel = rout.gameLevel)

                            }

                        }
                        navigation<SettingsGraph>(
                            startDestination = SettingsRout,
                            deepLinks = listOf(SettingsGraph.Link)
                        ) {
                            composable<SettingsRout> {
                                RadarSettingsScreen(
                                    Modifier.padding(
                                        innerPadding
                                    )
                                )
                            }
                        }
                        navigation<ProfileGraph>(startDestination = ProfileRout) {
                            composable<ProfileRout> { }
                        }
                    }
                }
                ShopItemDetailCard(viewModel = shopViewModel ,shopItem = selectedItem, onDismiss = { selectedItem = null }, onGetForAd = { }, onGetForStars = {})
            }
        }
    }
}

