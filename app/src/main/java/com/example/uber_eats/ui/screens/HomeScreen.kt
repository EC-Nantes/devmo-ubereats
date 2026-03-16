package com.example.uber_eats.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.uber_eats.R
import com.example.uber_eats.model.Restaurant
import com.example.uber_eats.ui.UberEatsUiState
import com.example.uber_eats.ui.UberEatsViewModel
import com.example.uber_eats.ui.theme.UberGreen
import com.example.uber_eats.ui.theme.UberLightGrey

@Composable
fun HomeScreen(
    viewModel: UberEatsViewModel,
    onRestaurantClick: (Restaurant) -> Unit,
    onCompareClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    HomeScreenContent(
        uiState = uiState,
        onRestaurantClick = onRestaurantClick,
        onExpressToggle = { viewModel.toggleExpressMode() },
        onCompareToggle = { viewModel.toggleComparisonMode() },
        onRestaurantSelect = { viewModel.selectRestaurantForComparison(it) },
        onCompareClick = onCompareClick,
        onDismissGpsWarning = { viewModel.dismissGpsWarning() }
    )
}

@Composable
fun HomeScreenContent(
    uiState: UberEatsUiState,
    onRestaurantClick: (Restaurant) -> Unit,
    onExpressToggle: (Boolean) -> Unit,
    onCompareToggle: (Boolean) -> Unit,
    onRestaurantSelect: (Restaurant) -> Unit,
    onCompareClick: () -> Unit,
    onDismissGpsWarning: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        item { SearchBarDesign() }
        item { FilterChipsRow() }
        if (uiState.showGpsWarning) {
            item { GpsWarning(onDismiss = onDismissGpsWarning) }
        }
        item { AddressMapCard() }
        item { CategoryRow() }
        item {
            RestaurantSectionHeader(
                isComparisonMode = uiState.isComparisonMode,
                selectedCount = uiState.selectedRestaurantsForComparison.size,
                onCompareToggle = onCompareToggle,
                onCompareClick = onCompareClick,
                isExpressOnly = uiState.isExpressMode,
                onExpressToggle = onExpressToggle
            )
        }
        item {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(uiState.filteredRestaurants) { restaurant ->
                    RestaurantSquareCard(
                        restaurant = restaurant,
                        isComparisonMode = uiState.isComparisonMode,
                        isSelected = uiState.selectedRestaurantsForComparison.contains(restaurant),
                        onClick = { 
                            if (uiState.isComparisonMode) {
                                onRestaurantSelect(restaurant)
                            } else {
                                onRestaurantClick(restaurant)
                            }
                        }
                    )
                }
            }
        }

        item { SectionHeader(title = stringResource(R.string.section_groceries)) }

        item { GroceryRow() }
        
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
fun SearchBarDesign() {
    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
        OutlinedTextField(
            value = "",
            onValueChange = { },
            placeholder = { Text(stringResource(R.string.search)) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(28.dp),
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = UberLightGrey,
                unfocusedContainerColor = UberLightGrey,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent
            ),
            readOnly = true
        )
    }
}

@Composable
fun FilterChipsRow() {
    LazyRow(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item { FilterChipItem(Icons.Default.FavoriteBorder, stringResource(R.string.pickup)) }
        item { FilterChipItem(Icons.Default.Star, stringResource(R.string.uberone)) }
        item { FilterChipItem(Icons.Default.Person, stringResource(R.string.groceries)) }
        item { FilterChipItem(Icons.AutoMirrored.Filled.List, stringResource(R.string.orders)) }
    }
}

@Composable
fun RestaurantSectionHeader(
    isComparisonMode: Boolean,
    selectedCount: Int,
    onCompareToggle: (Boolean) -> Unit,
    onCompareClick: () -> Unit,
    isExpressOnly: Boolean,
    onExpressToggle: (Boolean) -> Unit
) {
    Column(modifier = Modifier.padding(top = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SectionHeader(title = stringResource(R.string.section_restaurants), modifier = Modifier.weight(1f))
            
            if (isComparisonMode && selectedCount >= 2) {
                Button(
                    onClick = onCompareClick,
                    colors = ButtonDefaults.buttonColors(containerColor = UberGreen),
                    modifier = Modifier.height(36.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(stringResource(R.string.compare), style = MaterialTheme.typography.labelMedium)
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(
                    checked = isExpressOnly, 
                    onCheckedChange = onExpressToggle, 
                    colors = SwitchDefaults.colors(checkedThumbColor = UberGreen)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.express), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(
                    checked = isComparisonMode, 
                    onCheckedChange = onCompareToggle, 
                    colors = SwitchDefaults.colors(checkedThumbColor = UberGreen)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isComparisonMode && selectedCount > 0) stringResource(R.string.compare_with_count, selectedCount) else stringResource(R.string.compare),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun FilterChipItem(icon: ImageVector, label: String) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = UberLightGrey,
        modifier = Modifier.clickable { }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun AddressMapCard() {
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp).height(120.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize().background(Color(0xFFE0E0E0))) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(stringResource(R.string.delivery_to, "Ecole Centrale de Nantes"), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Red, modifier = Modifier.align(Alignment.Center).size(32.dp))
        }
    }
}

@Composable
fun GpsWarning(onDismiss: () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.errorContainer,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.gps_warning), style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.weight(1f))
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.fix), color = MaterialTheme.colorScheme.error) }
        }
    }
}

@Composable
fun SectionHeader(title: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
        }
    }
}

@Composable
fun CategoryRow() {
    LazyRow(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { CategoryCircle("Burgers", R.drawable.cat_burger) }
        item { CategoryCircle("Sushi", R.drawable.cat_sushi) }
        item { CategoryCircle("Pizza", R.drawable.cat_pizza) }
        item { CategoryCircle("Tacos", R.drawable.cat_tacos) }
        item { CategoryCircle("Dessert", R.drawable.cat_dessert) }
    }
}

@Composable
fun CategoryCircle(name: String, imageRes: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.size(70.dp).clip(CircleShape).background(UberLightGrey), contentAlignment = Alignment.Center) {
            Image(painter = painterResource(id = imageRes), contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(name, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun RestaurantSquareCard(
    restaurant: Restaurant,
    isSelected: Boolean = false,
    isComparisonMode: Boolean = false,
    onClick: () -> Unit
) {
    Column(modifier = Modifier.width(160.dp).clickable { onClick() }) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(UberLightGrey)
                .then(
                    if (isComparisonMode && isSelected) {
                        Modifier.border(3.dp, UberGreen, RoundedCornerShape(8.dp))
                    } else Modifier
                )
        ) {
            Image(
                painter = painterResource(id = restaurant.imageRes),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alpha = if (isComparisonMode && !isSelected) 0.6f else 1.0f
            )
            
            if (isComparisonMode) {
                Surface(
                    modifier = Modifier.padding(8.dp).align(Alignment.TopEnd),
                    shape = CircleShape,
                    color = if (isSelected) UberGreen else Color.White,
                    border = if (isSelected) null else androidx.compose.foundation.BorderStroke(2.dp, UberLightGrey)
                ) {
                    Icon(
                        imageVector = if (isSelected) Icons.Default.Check else Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp).padding(4.dp),
                        tint = if (isSelected) Color.White else Color.Gray
                    )
                }
            } else {
                Text(stringResource(R.string.brand), modifier = Modifier.padding(8.dp).background(Color.White.copy(alpha = 0.7f), RoundedCornerShape(4.dp)).padding(horizontal = 4.dp), style = MaterialTheme.typography.labelSmall, color = Color.Black)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(restaurant.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
        Text(text = stringResource(R.string.total_fees_label, restaurant.totalFees), style = MaterialTheme.typography.labelSmall, color = Color.Gray)
    }
}

@Composable
fun GroceryRow() {
    LazyRow(
        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        item { GroceryLogoPlaceholder("Carrefour", R.drawable.grocery_carrefour) }
        item { GroceryLogoPlaceholder("Franprix", R.drawable.grocery_franprix) }
        item { GroceryLogoPlaceholder("U Express", R.drawable.grocery_uexpress) }
        item { GroceryLogoPlaceholder("Monoprix", R.drawable.grocery_monoprix) }
    }
}

@Composable
fun GroceryLogoPlaceholder(name: String, imageRes: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.size(60.dp), contentAlignment = Alignment.Center) {
            Image(painter = painterResource(id = imageRes), contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Fit)
        }
        Text(name, style = MaterialTheme.typography.labelSmall, color = Color.Black, fontWeight = FontWeight.Bold)
    }
}