package com.example.uber_eats.ui

import androidx.lifecycle.ViewModel
import com.example.uber_eats.data.RestaurantDataSource
import com.example.uber_eats.model.CartItem
import com.example.uber_eats.model.Restaurant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class UberEatsUiState(
    val restaurants: List<Restaurant> = emptyList(),
    val filteredRestaurants: List<Restaurant> = emptyList(),
    val isExpressMode: Boolean = false,
    val cartItems: List<CartItem> = emptyList(),
    val selectedRestaurantsForComparison: List<Restaurant> = emptyList(),
    val isComparisonMode: Boolean = false,
    val currentRestaurant: Restaurant? = null,
    val showGpsWarning: Boolean = true
)

class UberEatsViewModel : ViewModel() {
    private val datasource = RestaurantDataSource()
    private val _uiState = MutableStateFlow(UberEatsUiState(
        restaurants = datasource.loadRestaurants(), 
        filteredRestaurants = datasource.loadRestaurants()
    ))
    val uiState: StateFlow<UberEatsUiState> = _uiState.asStateFlow()

    fun toggleExpressMode() {
        _uiState.update { currentState ->
            val newExpressMode = !currentState.isExpressMode
            val filtered = if (newExpressMode) {
                currentState.restaurants.filter { it.isModeExpress }
            } else {
                currentState.restaurants
            }
            currentState.copy(isExpressMode = newExpressMode, filteredRestaurants = filtered)
        }
    }

    fun toggleComparisonMode() {
        _uiState.update { currentState ->
            currentState.copy(
                isComparisonMode = !currentState.isComparisonMode,
                selectedRestaurantsForComparison = emptyList()
            )
        }
    }

    fun selectRestaurantForComparison(restaurant: Restaurant) {
        _uiState.update { currentState ->
            val currentSelected = currentState.selectedRestaurantsForComparison
            if (currentSelected.any { it.id == restaurant.id }) {
                currentState.copy(selectedRestaurantsForComparison = currentSelected.filter { it.id != restaurant.id })
            } else if (currentSelected.size < 2) {
                currentState.copy(selectedRestaurantsForComparison = currentSelected + restaurant)
            } else {
                currentState
            }
        }
    }

    fun addToCart(item: CartItem) {
        _uiState.update { it.copy(cartItems = it.cartItems + item) }
    }

    fun clearCart() {
        _uiState.update { it.copy(cartItems = emptyList()) }
    }

    fun setCurrentRestaurant(restaurant: Restaurant?) {
        _uiState.update { it.copy(currentRestaurant = restaurant) }
    }
    
    fun dismissGpsWarning() {
        _uiState.update { it.copy(showGpsWarning = false) }
    }
}
