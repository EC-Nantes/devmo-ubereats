package com.example.uber_eats.model

import com.example.uber_eats.R

data class Restaurant(
    val id: String,
    val name: String,
    val rating: Double,
    val reviewCount: Int,
    val minDeliveryTime: Int,
    val maxDeliveryTime: Int,
    val deliveryFee: Double,
    val serviceFee: Double,
    val imageRes: Int = R.drawable.ic_launcher_background,
    val categories: List<String>,
    val menu: List<Dish> = emptyList(),
    val isModeExpress: Boolean = false
) {
    val estimatedDeliveryRange: String get() = "$minDeliveryTime–$maxDeliveryTime min"
    val totalFees: Double get() = deliveryFee + serviceFee
    val minDishPrice: Double get() = menu.minOfOrNull { it.price } ?: 0.0
    val totalStartingPrice: Double get() = minDishPrice + totalFees
}

data class Dish(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val imageRes: Int = R.drawable.ic_launcher_background
)

data class CartItem(
    val dish: Dish,
    val restaurantId: String,
    val restaurantName: String,
    val restaurantFees: Double,
    val quantity: Int = 1
) {
    val totalPrice: Double get() = dish.price * quantity
}

object DefaultData {
    val GENERIC_MENU = listOf(
        Dish("d1", "Plat Signature", "Notre meilleur plat, préparé avec soin.", 14.50, R.drawable.bistrot),
        Dish("d2", "Accompagnement", "Parfait pour compléter votre repas.", 5.00),
        Dish("d3", "Boisson Fraîche", "33cl au choix.", 3.50)
    )
}
