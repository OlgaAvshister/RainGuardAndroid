package com.olga.avshister.rainguard

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.olga.avshister.rainguard.data.profile.AuthLocalRepository
import com.olga.avshister.rainguard.data.profile.AuthRepository
import com.olga.avshister.rainguard.domain.profile.Role
import com.olga.avshister.rainguard.presentation.core.AUTH_PHONE_SCREEN
import com.olga.avshister.rainguard.presentation.core.MAP_SCREEN
import com.olga.avshister.rainguard.presentation.core.NAV_ARGUMENT_PHONE_NUMBER
import com.olga.avshister.rainguard.presentation.core.SELECT_PRODUCT_TO_CHECK_SCREEN
import com.olga.avshister.rainguard.presentation.core.SMS_CODE_SCREEN
import com.olga.avshister.rainguard.presentation.screens.AuthPhoneScreen
import com.olga.avshister.rainguard.presentation.screens.MapScreen
import com.olga.avshister.rainguard.presentation.screens.SmsCodeScreen
import com.olga.avshister.rainguard.presentation.screens.stuff.CheckProductScreen
import com.olga.avshister.rainguard.presentation.ui.theme.RainGuardTheme
import com.yandex.mapkit.MapKitFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RainGuardTheme {
                RainGuardApp()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
    }

    override fun onStop() {
        super.onStop()
        MapKitFactory.getInstance().onStop()
    }
}

@Composable
fun RainGuardApp() {
    var showBottomSheet by remember { mutableStateOf(true) }

    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = getStartDestination(LocalContext.current)
    ) {
        composable(AUTH_PHONE_SCREEN) { AuthPhoneScreen(navController) }
        composable(MAP_SCREEN) { MapScreen(navController) }
        composable(SELECT_PRODUCT_TO_CHECK_SCREEN) { CheckProductScreen(navController) }
        composable(
            route = SMS_CODE_SCREEN,
            arguments = listOf(
                navArgument(NAV_ARGUMENT_PHONE_NUMBER) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val phoneNumber =
                backStackEntry.arguments?.getString(NAV_ARGUMENT_PHONE_NUMBER).orEmpty()
            SmsCodeScreen(navController, phoneNumber)
        }
        /*composable(
            route = RENT_POINT_SCREEN_PATH,
            arguments = listOf(
                navArgument(NAV_ARGUMENT_RENT_POINT_ID) {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val rentPointId = backStackEntry.arguments?.getLong(NAV_ARGUMENT_RENT_POINT_ID)!!
            //RentPointBottomSheet(navController, rentPointId)
            if (showBottomSheet) {
                RentPointBottomSheet(
                    navController = navController,
                    rentPointId = rentPointId,
                    onDismiss = { showBottomSheet = false }
                )
            }
        }*/

        //composable(CATALOG_SCREEN) { CatalogScreen(navController) }
        //composable(CART_SCREEN) { CartScreen(navController) }
        //composable(SELECT_IDS_SCREEN) { SelectIdsScreen(navController) }
        //composable(CHECKOUT_SCREEN) { CheckoutScreen(navController) }
        //composable(CARDS_SCREEN) { PaymentScreen(navController) }
        //composable(RENT_SCREEN) { RentScreen(navController) }

    }
}

fun getStartDestination(context: Context): String {
    val authRepository: AuthRepository = AuthLocalRepository(context)
    return authRepository.getProfile()?.let {
        when (it.role) {
            Role.CUSTOMER -> {
                MAP_SCREEN
            }

            Role.STUFF -> {
                SELECT_PRODUCT_TO_CHECK_SCREEN
            }

            Role.OWNER -> {
                MAP_SCREEN
            }
        }
    } ?: run {
        AUTH_PHONE_SCREEN
    }
}
