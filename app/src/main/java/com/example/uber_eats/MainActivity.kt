package com.example.uber_eats

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.uber_eats.model.CartItem
import com.example.uber_eats.ui.UberEatsViewModel
import com.example.uber_eats.ui.screens.HomeScreen
import com.example.uber_eats.ui.screens.RestaurantDetailScreen
import com.example.uber_eats.ui.screens.ComparisonScreen
import com.example.uber_eats.ui.screens.CheckoutFlowScreen
import com.example.uber_eats.ui.theme.UberGreen
import com.example.uber_eats.ui.theme.Uber_EatsTheme

enum class UberEatsScreen {
    Home,
    Detail,
    Comparison,
    Checkout
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Uber_EatsTheme {
                UberEatsApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UberEatsAppBar(
    currentScreen: String,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    restaurantName: String? = null
) {
    if (currentScreen != UberEatsScreen.Home.name) {
        val title = when (currentScreen) {
            UberEatsScreen.Detail.name -> restaurantName ?: stringResource(R.string.details)
            UberEatsScreen.Comparison.name -> stringResource(R.string.quick_comparator)
            UberEatsScreen.Checkout.name -> stringResource(R.string.checkout)
            else -> ""
        }

        TopAppBar(
            title = { Text(title, fontWeight = FontWeight.Bold) },
            modifier = modifier,
            navigationIcon = {
                if (canNavigateBack) {
                    IconButton(onClick = navigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
        )
    }
}


@Composable
fun UberEatsApp(
    viewModel: UberEatsViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    val uiState by viewModel.uiState.collectAsState()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = backStackEntry?.destination?.route ?: UberEatsScreen.Home.name

    Scaffold(
        topBar = {
            UberEatsAppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() },
                restaurantName = uiState.currentRestaurant?.name
            )
        },
        bottomBar = {
            if (currentScreen == UberEatsScreen.Home.name) {
                UberBottomNavigation()
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            NavHost(
                navController = navController,
                startDestination = UberEatsScreen.Home.name,
                modifier = Modifier.fillMaxSize()
            ) {
                composable(route = UberEatsScreen.Home.name) {
                    HomeScreen(
                        onRestaurantClick = { 
                            viewModel.setCurrentRestaurant(it)
                            navController.navigate(UberEatsScreen.Detail.name)
                        },
                        onCompareClick = {
                            navController.navigate(UberEatsScreen.Comparison.name)
                        },
                        viewModel = viewModel
                    )
                }
                composable(route = UberEatsScreen.Detail.name) {
                    uiState.currentRestaurant?.let { restaurant ->
                        RestaurantDetailScreen(
                            restaurant = restaurant,
                            onBackClick = { navController.popBackStack() },
                            onAddToCart = { viewModel.addToCart(it) },
                            isCartEmpty = uiState.cartItems.none { it.restaurantId == restaurant.id }
                        )
                    }
                }
                composable(route = UberEatsScreen.Comparison.name) {
                    if (uiState.selectedRestaurantsForComparison.size >= 2) {
                        ComparisonScreen(
                            restaurant1 = uiState.selectedRestaurantsForComparison[0],
                            restaurant2 = uiState.selectedRestaurantsForComparison[1],
                            onBackClick = { navController.popBackStack() }
                        )
                    }
                }
                composable(route = UberEatsScreen.Checkout.name) {
                    CheckoutFlowScreen(
                        cartItems = uiState.cartItems,
                        onBackClick = { navController.popBackStack() },
                        onPaymentSuccess = {
                            viewModel.clearCart()
                            navController.popBackStack(UberEatsScreen.Home.name, inclusive = false)
                        }
                    )
                }
            }

            if (uiState.cartItems.isNotEmpty() && currentScreen != UberEatsScreen.Checkout.name) {
                CartSummaryBar(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    cartItems = uiState.cartItems,
                    onClick = { navController.navigate(UberEatsScreen.Checkout.name) }
                )
            }
        }
    }
}

@Composable
fun CartSummaryBar(modifier: Modifier = Modifier, cartItems: List<CartItem>, onClick: () -> Unit) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp)
            .clickable { onClick() },
        color = UberGreen,
        shape = RoundedCornerShape(8.dp),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val totalQuantity = cartItems.sumOf { it.quantity }
            val totalPrice = cartItems.sumOf { it.totalPrice } + 
                            cartItems.distinctBy { it.restaurantId }.sumOf { it.restaurantFees }

            Text("$totalQuantity", color = Color.White, fontWeight = FontWeight.Bold)
            Text(stringResource(R.string.view_cart), color = Color.White, fontWeight = FontWeight.Bold)
            Text(String.format("%.2f€", totalPrice), color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun UberBottomNavigation() {
    NavigationBar(containerColor = Color.White) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { Text(stringResource(R.string.nav_home)) },
            selected = true,
            onClick = { }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Search, contentDescription = null) },
            label = { Text(stringResource(R.string.nav_browse)) },
            selected = false,
            onClick = { }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.ShoppingBasket, contentDescription = null) },
            label = { Text(stringResource(R.string.nav_baskets)) },
            selected = false,
            onClick = { }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = null) },
            label = { Text(stringResource(R.string.nav_account)) },
            selected = false,
            onClick = { }
        )
    }
}