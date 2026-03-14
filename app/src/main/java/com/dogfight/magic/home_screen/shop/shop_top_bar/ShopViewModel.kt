package com.dogfight.magic.home_screen.shop.shop_top_bar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dogfight.magic.game_ui.radar.upravlenie.repository.ControlRepository
import com.dogfight.magic.unity_ads.ResourceDepletionType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShopViewModel @Inject constructor(
    private val controlRepository: ControlRepository,
) : ViewModel() {

    private val _shopState = MutableStateFlow(ShopState())
    val shopState: StateFlow<ShopState> = _shopState.asStateFlow()

    init {
        observeItemCounts()
    }

    private fun observeItemCounts() {
        viewModelScope.launch {
            launch {
                controlRepository.superRocketCountFlow.collectLatest { count ->
                    _shopState.value = _shopState.value.copy(superRocketCount = count)
                }
            }
            launch {
                controlRepository.homingCountFlow.collectLatest { count ->
                    _shopState.value = _shopState.value.copy(homingCount = count)
                }
            }
            launch {
                controlRepository.shieldCountFlow.collectLatest { count ->
                    _shopState.value = _shopState.value.copy(shieldCount = count)
                }
            }
            launch {
                controlRepository.avadaCountFlow.collectLatest { count ->
                    _shopState.value = _shopState.value.copy(avadaCount = count)
                }
            }
            launch {
                controlRepository.turnCountFlow.collectLatest { count ->
                    _shopState.value = _shopState.value.copy(turnCount = count)
                }
            }
            launch {
                controlRepository.reverseCountFlow.collectLatest { count ->
                    _shopState.value = _shopState.value.copy(reverseCount = count)
                }
            }
            launch {
                controlRepository.forsagCountFlow.collectLatest { count ->
                    _shopState.value = _shopState.value.copy(forsagCount = count)
                }
            }
            launch {
                controlRepository.ammoCountFlow.collectLatest { count ->
                    _shopState.value = _shopState.value.copy(ammoCount = count)
                }
            }
            launch {
                controlRepository.starCountFlow.collectLatest { count ->
                    _shopState.value = _shopState.value.copy(starCount = count)
                }
            }
        }
    }

    // Методы обновления товаров
    fun updateSuperRocketCount(count: Int) = viewModelScope.launch {
        controlRepository.setSuperRocketCount(count)
    }

    fun updateHomingCount(count: Int) = viewModelScope.launch {
        controlRepository.setHomingCount(count)
    }

    fun updateShieldCount(count: Int) = viewModelScope.launch {
        controlRepository.setShieldCount(count)
    }

    fun updateAvadaCount(count: Int) = viewModelScope.launch {
        controlRepository.setAvadaCount(count)
    }

    fun updateTurnCount(count: Int) = viewModelScope.launch {
        controlRepository.setTurnCount(count)
    }

    fun updateReverseCount(count: Int) = viewModelScope.launch {
        controlRepository.setReverseCount(count)
    }

    fun updateForsagCount(count: Int) = viewModelScope.launch {
        controlRepository.setForsagCount(count)
    }

    fun updateAmmoCount(count: Int) = viewModelScope.launch {
        controlRepository.setAmmoCount(count)
    }

    fun updateStarCount(count: Int) = viewModelScope.launch {
        controlRepository.setStarCount(count)
    }

    fun rewardForResource(type: ResourceDepletionType?, amount: Int) {
        when (type) {
            is ResourceDepletionType.Ammo -> updateAmmoCount(shopState.value.ammoCount + amount)
            is ResourceDepletionType.Homing -> updateHomingCount(shopState.value.homingCount + amount)
            is ResourceDepletionType.Shield -> updateShieldCount(shopState.value.shieldCount + amount)
            is ResourceDepletionType.Afterburner -> updateForsagCount(shopState.value.forsagCount + amount)
            is ResourceDepletionType.SuperRocket -> updateSuperRocketCount(shopState.value.superRocketCount + amount)
            is ResourceDepletionType.Avada -> updateAvadaCount(shopState.value.avadaCount + amount)
            is ResourceDepletionType.Turn -> updateTurnCount(shopState.value.turnCount + amount)
            is ResourceDepletionType.Reverse -> updateReverseCount(shopState.value.reverseCount + amount)
            is ResourceDepletionType.Stars -> updateStarCount(shopState.value.starCount + amount)
            null -> {}
        }
    }
    // Когда заканчивается ресурс чего-нибудь
    fun updateDepletionType(type: ResourceDepletionType?) {
        viewModelScope.launch {
            _shopState.update { it.copy(depletionType = type) }
        }
    }

}

data class ShopState(
    val superRocketCount: Int = 0,
    val homingCount: Int = 0,
    val shieldCount: Int = 0,
    val avadaCount: Int = 0,
    val turnCount: Int = 0,
    val reverseCount: Int = 0,
    val forsagCount: Int = 0,
    val ammoCount: Int = 0,
    val starCount: Int = 0,
    val depletionType: ResourceDepletionType? = null,
)
