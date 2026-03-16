package com.example.uber_eats.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.uber_eats.R
import com.example.uber_eats.model.*
import com.example.uber_eats.ui.theme.UberGreen

@Composable
fun RestaurantDetailScreen(
    restaurant: Restaurant,
    onBackClick: () -> Unit,
    onAddToCart: (CartItem) -> Unit,
    isCartEmpty: Boolean
) {
    var selectedDish by remember { mutableStateOf<Dish?>(null) }
    val menu = DefaultData.GENERIC_MENU

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
            item {
                RestaurantHeader(restaurant)
                Text(stringResource(R.string.popular_dishes), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 16.dp))
            }
            items(menu) { dish ->
                DishItem(dish, restaurant, onClick = { selectedDish = dish }, showFees = isCartEmpty)
            }
        }

        if (selectedDish != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { selectedDish = null }
            ) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .fillMaxHeight(0.8f),
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                    color = Color.White
                ) {
                    CustomizationContent(
                        dish = selectedDish!!,
                        restaurant = restaurant,
                        onDismiss = { selectedDish = null },
                        onConfirmAdd = { onAddToCart(it); selectedDish = null },
                        isFirstItem = isCartEmpty
                    )
                }
            }
        }
    }
}

@Composable
fun RestaurantHeader(restaurant: Restaurant) {
    Column {
        Box(modifier = Modifier.fillMaxWidth().height(150.dp).clip(RoundedCornerShape(8.dp))) {
            Image(painter = painterResource(id = restaurant.imageRes), contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        }
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
            Icon(Icons.Default.Timer, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray)
            Text(restaurant.estimatedDeliveryRange, style = MaterialTheme.typography.bodySmall, color = Color.Gray, modifier = Modifier.padding(start = 4.dp))
            Text(" • ", color = Color.Gray)
            Text(stringResource(R.string.total_fees_label, restaurant.totalFees), style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
        Text(stringResource(R.string.total_starts_from, restaurant.minDishPrice + restaurant.totalFees), style = MaterialTheme.typography.bodyMedium, color = UberGreen, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 4.dp))
    }
}

@Composable
fun DishItem(dish: Dish, restaurant: Restaurant, onClick: () -> Unit, showFees: Boolean) {
    Column(modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = 12.dp)) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(dish.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(dish.description, style = MaterialTheme.typography.bodySmall, color = Color.Gray, maxLines = 2)
                Text("${String.format("%.2f", dish.price)}€", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
                if (showFees) {
                    Text(stringResource(R.string.total_fees_approx, dish.price + restaurant.totalFees), style = MaterialTheme.typography.labelSmall, color = UberGreen)
                }
            }
        }
        HorizontalDivider(modifier = Modifier.padding(top = 12.dp))
    }
}

@Composable
fun CustomizationContent(dish: Dish, restaurant: Restaurant, onDismiss: () -> Unit, onConfirmAdd: (CartItem) -> Unit, isFirstItem: Boolean) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp).verticalScroll(rememberScrollState())) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "Close")
            }
        }
        Text(dish.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(dish.description, color = Color.Gray, modifier = Modifier.padding(bottom = 24.dp))
        
        Button(
            onClick = { onConfirmAdd(CartItem(dish, restaurant.id, restaurant.name, restaurant.totalFees)) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = UberGreen)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(stringResource(R.string.add_to_cart))
                Text(if (isFirstItem) stringResource(R.string.total_with_price, dish.price + restaurant.totalFees) else String.format("%.2f€", dish.price), fontWeight = FontWeight.Bold)
            }
        }
        if (isFirstItem) {
            Text(stringResource(R.string.total_fees_label, restaurant.totalFees), modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 8.dp), style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}