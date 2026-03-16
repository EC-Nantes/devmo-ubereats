package com.example.uber_eats.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
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
import androidx.compose.ui.unit.sp
import com.example.uber_eats.R
import com.example.uber_eats.model.Restaurant
import com.example.uber_eats.ui.theme.UberGreen
import com.example.uber_eats.ui.theme.UberLightGrey

@Composable
fun ComparisonScreen(
    restaurant1: Restaurant,
    restaurant2: Restaurant,
    onBackClick: () -> Unit
) {
    var showMenuCompare by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            RestaurantHeaderMini(restaurant1, modifier = Modifier.weight(1f))
            RestaurantHeaderMini(restaurant2, modifier = Modifier.weight(1f))
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        ComparisonRow(label = stringResource(R.string.rating), val1 = "${restaurant1.rating} ⭐", val2 = "${restaurant2.rating} ⭐")
        ComparisonRow(label = stringResource(R.string.delivery_time), val1 = restaurant1.estimatedDeliveryRange, val2 = restaurant2.estimatedDeliveryRange)
        ComparisonRow(label = stringResource(R.string.total_fees), val1 = String.format("%.2f€", restaurant1.totalFees), val2 = String.format("%.2f€", restaurant2.totalFees))
        ComparisonRow(label = stringResource(R.string.starts_from), val1 = String.format("%.2f€", restaurant1.totalStartingPrice), val2 = String.format("%.2f€", restaurant2.totalStartingPrice))
        
        Spacer(modifier = Modifier.height(24.dp))
        
        val insight = when {
            restaurant1.totalStartingPrice < restaurant2.totalStartingPrice -> 
                stringResource(R.string.insight_budget_1, restaurant1.name)
            restaurant2.totalStartingPrice < restaurant1.totalStartingPrice -> 
                stringResource(R.string.insight_budget_2, restaurant2.name)
            restaurant1.minDeliveryTime < restaurant2.minDeliveryTime -> 
                stringResource(R.string.insight_speed_1, restaurant1.name)
            restaurant2.minDeliveryTime < restaurant1.minDeliveryTime -> 
                stringResource(R.string.insight_speed_2, restaurant2.name)
            else -> stringResource(R.string.insight_similar)
        }
        
        Surface(
            color = UberGreen.copy(alpha = 0.1f),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = insight,
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black,
                fontWeight = FontWeight.Medium
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showMenuCompare = !showMenuCompare }
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(R.string.compare_menus), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Icon(
                imageVector = if (showMenuCompare) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = null
            )
        }
        
        if (showMenuCompare) {
            Column {
                val maxMenuSize = maxOf(restaurant1.menu.size, restaurant2.menu.size)
                for (i in 0 until maxMenuSize) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        val dish1 = restaurant1.menu.getOrNull(i)
                        val dish2 = restaurant2.menu.getOrNull(i)
                        
                        if (dish1 != null) {
                            DishComparisonCard(dish1.name, dish1.price, modifier = Modifier.weight(1f))
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                        
                        if (dish2 != null) {
                            DishComparisonCard(dish2.name, dish2.price, modifier = Modifier.weight(1f))
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RestaurantHeaderMini(restaurant: Restaurant, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.height(100.dp).fillMaxWidth().clip(RoundedCornerShape(8.dp))) {
            Image(
                painter = painterResource(id = restaurant.imageRes),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(restaurant.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
    }
}

@Composable
fun ComparisonRow(label: String, val1: String, val2: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(val1, modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(val2, modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
        HorizontalDivider(modifier = Modifier.padding(top = 8.dp), color = UberLightGrey)
    }
}

@Composable
fun DishComparisonCard(name: String, price: Double, modifier: Modifier = Modifier) {
    Surface(
        color = UberLightGrey,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(name, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, maxLines = 1)
            Text(
                text = String.format("%.2f€", price),
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
        }
    }
}