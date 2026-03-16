package com.example.uber_eats.data

import com.example.uber_eats.R
import com.example.uber_eats.model.DefaultData
import com.example.uber_eats.model.Restaurant

class RestaurantDataSource {
    private val allRestaurants = listOf(
        Restaurant(
            id = "1", name = "Le Petit Bistro", rating = 4.8, reviewCount = 120,
            minDeliveryTime = 15, maxDeliveryTime = 25, deliveryFee = 2.50, serviceFee = 1.00,
            imageRes = R.drawable.bistrot, categories = listOf("Français", "Élégant"),
            menu = DefaultData.GENERIC_MENU, isModeExpress = true
        ),
        Restaurant(
            id = "2", name = "Shake Burger", rating = 4.2, reviewCount = 500,
            minDeliveryTime = 20, maxDeliveryTime = 35, deliveryFee = 1.99, serviceFee = 1.00,
            imageRes = R.drawable.shake_burger, categories = listOf("Burger", "Fast Food"),
            menu = DefaultData.GENERIC_MENU, isModeExpress = false
        ),
        Restaurant(
            id = "3", name = "Sushi Master", rating = 4.9, reviewCount = 85,
            minDeliveryTime = 10, maxDeliveryTime = 20, deliveryFee = 1.50, serviceFee = 0.50,
            imageRes = R.drawable.sushi_master, categories = listOf("Sushi", "Sain"),
            menu = DefaultData.GENERIC_MENU, isModeExpress = true
        ),
        Restaurant(
            id = "4", name = "Poke Island", rating = 4.5, reviewCount = 210,
            minDeliveryTime = 30, maxDeliveryTime = 45, deliveryFee = 2.99, serviceFee = 1.00,
            imageRes = R.drawable.poke_island, categories = listOf("Sain", "Poke"),
            menu = DefaultData.GENERIC_MENU, isModeExpress = false
        )
    )

    fun loadRestaurants(): List<Restaurant> = allRestaurants
}
