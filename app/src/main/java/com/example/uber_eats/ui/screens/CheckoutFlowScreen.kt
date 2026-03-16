package com.example.uber_eats.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uber_eats.R
import com.example.uber_eats.model.CartItem
import com.example.uber_eats.ui.theme.UberGreen
import com.example.uber_eats.ui.theme.UberLightGrey

@Composable
fun CheckoutFlowScreen(
    cartItems: List<CartItem>,
    onBackClick: () -> Unit,
    onPaymentSuccess: () -> Unit
) {
    val subtotal = cartItems.sumOf { it.totalPrice }
    val uniqueRestaurants = cartItems.distinctBy { it.restaurantId }
    val totalFees = uniqueRestaurants.sumOf { it.restaurantFees }
    val total = subtotal + totalFees

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.weight(1f).padding(16.dp)) {
            item {
                Text(stringResource(R.string.delivery_address), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = UberGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Ecole Centrale de Nantes, Nantes", style = MaterialTheme.typography.bodyMedium)
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
            }

            item {
                Text(stringResource(R.string.your_order), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(12.dp))
            }

            items(cartItems) { item ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("${item.quantity}x ${item.dish.name}", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    Text(String.format("%.2f€", item.totalPrice), fontWeight = FontWeight.Bold)
                }
            }

            item {
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                
                Text(stringResource(R.string.payment_method), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(UberLightGrey)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CreditCard, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Visa **** 4242", fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.weight(1f))
                    Text(stringResource(R.string.change), color = UberGreen, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                
                Text(stringResource(R.string.payment_summary), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(stringResource(R.string.subtotal), color = Color.Gray)
                    Text(String.format("%.2f€", subtotal))
                }
                
                uniqueRestaurants.forEach { restaurant ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(stringResource(R.string.fees_for, restaurant.restaurantName), color = Color.Gray)
                        Text(String.format("%.2f€", restaurant.restaurantFees))
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(stringResource(R.string.total), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                    Text(String.format("%.2f€", total), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shadowElevation = 8.dp,
            color = Color.White
        ) {
            Button(
                onClick = onPaymentSuccess,
                enabled = true,
                modifier = Modifier.fillMaxWidth().padding(16.dp).height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = UberGreen,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(stringResource(R.string.pay_now), fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        }
    }
}