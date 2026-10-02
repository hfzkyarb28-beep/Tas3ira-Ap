package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.AdminLoginScreen
import com.example.ui.screens.AuthGateScreen
import com.example.ui.screens.CustomerCatalogScreen
import com.example.ui.screens.CustomerLoginScreen
import com.example.ui.screens.CustomerStatusScreen
import com.example.ui.theme.AhlulbaytPricingTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

  private val viewModel: MainViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      AhlulbaytPricingTheme {
        MainAppContent(viewModel = viewModel)
      }
    }
  }
}

@Composable
fun MainAppContent(viewModel: MainViewModel) {
  val currentScreen by viewModel.currentScreen.collectAsState()
  val customer by viewModel.activeCustomer.collectAsState()
  val userMessage by viewModel.userMessage.collectAsState()
  val snackbarHostState = remember { SnackbarHostState() }

  LaunchedEffect(userMessage) {
    userMessage?.let {
      snackbarHostState.showSnackbar(it)
      viewModel.clearMessage()
    }
  }

  // Handle system back button gracefully
  when (currentScreen) {
    AppScreen.AuthGate -> {
      // System default behavior (exits app)
    }
    AppScreen.CustomerLogin, AppScreen.AdminLogin -> {
      BackHandler {
        viewModel.navigateTo(AppScreen.AuthGate)
      }
    }
    AppScreen.CustomerStatus -> {
      BackHandler {
        viewModel.navigateTo(AppScreen.AuthGate)
      }
    }
    AppScreen.CustomerCatalog -> {
      BackHandler {
        viewModel.navigateTo(AppScreen.AuthGate)
      }
    }
    AppScreen.AdminDashboard -> {
      BackHandler {
        viewModel.navigateTo(AppScreen.AuthGate)
      }
    }
  }

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    snackbarHost = { SnackbarHost(snackbarHostState) }
  ) { innerPadding ->
    val modifier = Modifier.padding(innerPadding)
    when (currentScreen) {
      AppScreen.AuthGate -> AuthGateScreen(viewModel = viewModel, modifier = modifier)
      AppScreen.CustomerLogin -> CustomerLoginScreen(viewModel = viewModel, modifier = modifier)
      AppScreen.CustomerStatus -> CustomerStatusScreen(customer = customer, viewModel = viewModel, modifier = modifier)
      AppScreen.CustomerCatalog -> CustomerCatalogScreen(viewModel = viewModel, modifier = modifier)
      AppScreen.AdminLogin -> AdminLoginScreen(viewModel = viewModel, modifier = modifier)
      AppScreen.AdminDashboard -> AdminDashboardScreen(viewModel = viewModel, modifier = modifier)
    }
  }
}
