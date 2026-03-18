package com.olga.avshister.rainguard.presentation.screens

import CartScreen
import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.olga.avshister.rainguard.R
import com.olga.avshister.rainguard.domain.profile.Role
import com.olga.avshister.rainguard.domain.rent.RentPoint
import com.olga.avshister.rainguard.presentation.core.PROFILE_SCREEN
import com.olga.avshister.rainguard.presentation.screens.owner.OwnerRentPointScreen
import com.olga.avshister.rainguard.presentation.state.BSheetContentState
import com.olga.avshister.rainguard.presentation.state.MapMainContentState
import com.olga.avshister.rainguard.presentation.ui.TextImageProvider
import com.olga.avshister.rainguard.presentation.ui.components.LabelRounded
import com.olga.avshister.rainguard.presentation.viewmodel.MapViewModel
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider
import kotlinx.coroutines.launch

@Composable
fun MapScreen(navController: NavHostController) {
    val bSheetVisibilityState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
        confirmValueChange = { true }
    )

    var currentBSheetContentState by remember {
        mutableStateOf<BSheetContentState>(BSheetContentState.IdleState)
    }

    // Стек навигации
    var backStack by remember {
        mutableStateOf(listOf<BSheetContentState>())
    }

    val viewModel: MapViewModel = viewModel()
    val scope = rememberCoroutineScope()

    val mapMainContentState by viewModel.mainContentState.collectAsStateWithLifecycle()

    fun navigateTo(state: BSheetContentState) {
        backStack = backStack + currentBSheetContentState
        currentBSheetContentState = state
    }

    fun navigateBack() {
        if (backStack.isNotEmpty()) {
            currentBSheetContentState = backStack.last()
            backStack = backStack.dropLast(1)
        } else {
            // Закрываем bottom sheet если стек пуст
            scope.launch {
                bSheetVisibilityState.hide()
            }
        }
    }


    // Обработка системной кнопки "Назад"
    BackHandler(
        enabled = currentBSheetContentState != BSheetContentState.IdleState
    ) {
        if (backStack.isNotEmpty()) {
            navigateBack()
        } else {
            scope.launch {
                bSheetVisibilityState.hide()
            }
        }
    }

    // Обработка закрытия боттомшита свайпом вниз
    LaunchedEffect(bSheetVisibilityState.currentValue) {
        when (bSheetVisibilityState.currentValue) {
            ModalBottomSheetValue.Hidden -> {
                currentBSheetContentState = BSheetContentState.IdleState
                bSheetVisibilityState.hide()
                backStack = emptyList()
            }

            else -> {}
        }
    }

    // Следим за изменением контента и управляем видимостью bottom sheet
    LaunchedEffect(currentBSheetContentState) {
        Log.d(
            "CUSTOMER_RENT_POINT_VM",
            "LaunchedEffect(currentBSheetContentState): $currentBSheetContentState"
        )
        when (currentBSheetContentState) {
            BSheetContentState.IdleState -> {
                viewModel.onIntent(BSheetContentState.IdleState)
                bSheetVisibilityState.hide()
            }

            else -> {
                if (bSheetVisibilityState.isVisible) {
                    // Если уже виден, просто обновляем контент
                    // Ничего не делаем с видимостью
                } else {
                    bSheetVisibilityState.show()
                }
            }
        }
    }

    ModalBottomSheetLayout(
        sheetState = bSheetVisibilityState,
        sheetContent = {
            key(currentBSheetContentState::class.simpleName) {
                // Контент bottom sheet
                when (currentBSheetContentState) {
                    is BSheetContentState.RentPointState -> {
                        CustomerRentPointScreen(
                            rentPoint = (currentBSheetContentState as BSheetContentState.RentPointState).rentPoint,
                            onNextState = { state ->
                                navigateTo(state)
                            }
                        )
                    }

                    is BSheetContentState.CurrentRentState -> {
                        CurrentRentBSheet(
                            onDismiss = {
                                scope.launch {
                                    navigateTo(BSheetContentState.IdleState)
                                }
                            }
                        )
                    }

                    is BSheetContentState.CatalogState -> {
                        (currentBSheetContentState as BSheetContentState.CatalogState).let {
                            CatalogScreen(
                                it.filter,
                                it.rentPointId,
                                onNextState = { state ->
                                    navigateTo(state)
                                }
                            )
                        }
                    }

                    is BSheetContentState.CartState -> {
                        (currentBSheetContentState as BSheetContentState.CartState).let {
                            CartScreen(
                                it.selectedArticles,
                                it.rentPointId,
                                onNextState = { state ->
                                    navigateTo(state)
                                }
                            )
                        }
                    }

                    is BSheetContentState.SelectIdsStateToTakeState -> {
                        SelectIdsScreen(
                            openToTake = true,
                            onNextState = { state ->
                                navigateTo(state)
                            }
                        )
                    }

                    is BSheetContentState.CheckoutState -> {
                        CheckoutScreen(
                            onNextState = { state ->
                                navigateTo(state)
                            }
                        )
                    }

                    is BSheetContentState.CardsState -> {
                        PaymentScreen(
                            onNextState = { state ->
                                navigateTo(state)
                            }
                        )
                    }

                    is BSheetContentState.SelectIdsStateToDropState -> {
                        SelectIdsScreen(
                            openToTake = false,
                            onNextState = { state ->
                                navigateTo(state)
                            }
                        )
                    }

                    is BSheetContentState.GiveToCheckState -> {
                        GiveToCheckScreen(
                            onNextState = { state ->
                                navigateTo(state)
                            }
                        )
                    }

                    is BSheetContentState.FillStuffNumberState -> {
                        FillStuffNumber(
                            onNextState = { state ->
                                navigateTo(state)
                            }
                        )
                    }

                    is BSheetContentState.RentTotalState -> {
                        RentTotalScreen(
                            onNextState = { state ->
                                navigateTo(state)
                            }
                        )
                    }

                    is BSheetContentState.PayInProgressState -> {
                        PayInProgress(
                            onNextState = { state ->
                                navigateTo(state)
                            }
                        )
                    }

                    is BSheetContentState.PaySuccessState -> {
                        SuccessScreen(
                            message = stringResource(R.string.pay_success),
                            buttonText = stringResource(R.string.great),
                            onClick = {
                                navigateTo(BSheetContentState.IdleState)
                            }
                        )
                    }

                    //region Owner states
                    is BSheetContentState.OwnerRentPointState -> {
                        OwnerRentPointScreen(
                            rentPoint = (currentBSheetContentState as BSheetContentState.OwnerRentPointState).rentPoint
                        ) { state ->
                            navigateTo(state)
                        }
                    }
                    //endregion

                    else -> {
                        navigateTo(BSheetContentState.IdleState)
                    }
                }
            }

        }
    ) {
        // Основной контент (всегда отображается, когда bottom sheet скрыт)
        MainContent(
            mapMainContentState,
            navController,
            onBSheetContent = { content ->
                currentBSheetContentState = content
            }
        )
    }
}

@Composable
fun MainContent(
    mapMainContentState: MapMainContentState,
    navController: NavController,
    onBSheetContent: (state: BSheetContentState) -> Unit,
) {
    val context = LocalContext.current
    val mapView = remember { mutableStateOf<MapView?>(null) }

    val initialPoint = Point(55.752511, 37.621570)

    val placemarkTapListener = MapObjectTapListener { rentPoint, point ->
        val rentPointState = when (mapMainContentState.role) {
            Role.CUSTOMER -> BSheetContentState.RentPointState(rentPoint.userData as RentPoint)
            Role.OWNER -> BSheetContentState.OwnerRentPointState(rentPoint.userData as RentPoint)
            else -> { throw IllegalArgumentException("Interaction with the map must be as a Customer or Owner only") }
        }
        onBSheetContent(rentPointState)
        true
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        bottomBar = {
            BottomBar(
                onRentClicked = {
                    if (mapMainContentState.hasActiveRent) {
                        onBSheetContent(BSheetContentState.CurrentRentState)
                    } else {
                        Toast.makeText(
                            context,
                            context.getString(R.string.has_not_active_rent),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                },
                onProfileClicked = {
                    navController.navigate(PROFILE_SCREEN)
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            AndroidView(
                factory = { context ->
                    MapView(context).apply {
                        mapView.value = this
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
            ) { mapView ->
            }
        }

        if (mapMainContentState.hasActiveRent) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.TopCenter
            ) {
                LabelRounded(
                    text = stringResource(R.string.map_label_select_return_point)
                )
            }
        }

        // Обработка разрешений
        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { isGranted ->
                if (isGranted) {
                    // Разрешение получено
                } else {
                    // TODO: выводить тост, что без разрешения работать не будет
                }
            }
        )

        LaunchedEffect(Unit) {
            val permission = Manifest.permission.ACCESS_FINE_LOCATION
            if (ContextCompat.checkSelfPermission(
                    context,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionLauncher.launch(permission)
            }
        }

        // Инициализация карты
        LaunchedEffect(mapView.value) {
            mapView.value?.let { mapView ->
                mapView.onStart()
                mapView.mapWindow.map.move(
                    CameraPosition(
                        initialPoint,
                        12.0f,
                        150.0f,
                        30.0f
                    )
                )
            }
        }

        mapView.value?.let { mapView ->
            val clusterizedCollection =
                mapView.mapWindow.map.mapObjects.addClusterizedPlacemarkCollection { cluster ->
                    cluster.appearance.setIcon(
                        TextImageProvider(
                            context,
                            cluster.size.toString(),
                            R.drawable.ic_placemark
                        )
                    )
                }

            val imageProvider = ImageProvider.fromResource(context, R.drawable.ic_placemark)

            Log.d("MAP_SCREEN", "adding placemarks: rent points number=${mapMainContentState.rentPoints.size}")
            mapMainContentState.rentPoints.forEach { rentPoint ->
                clusterizedCollection.addPlacemark().apply {
                    geometry = Point(rentPoint.latitude, rentPoint.longitude)
                    userData = rentPoint
                    addTapListener(placemarkTapListener)
                    setIcon(imageProvider)
                }
            }
            clusterizedCollection.clusterPlacemarks(60.0, 15)
        }
    }
}


@Composable
fun BottomBar(onRentClicked: () -> Unit, onProfileClicked: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(30.dp)
            .height(68.dp)
    ) {
        Row(
            modifier = Modifier
                .background(Color.White)
                .fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomBarItem(BottomBarItem.RENT) {
                onRentClicked()
            }
            BottomBarItem(BottomBarItem.HOME) {
                // мы уже здесь
            }
            BottomBarItem(BottomBarItem.PROFILE) {
                onProfileClicked()
            }
        }
    }
}

@Composable
fun BottomBarItem(item: BottomBarItem, onClick: (item: BottomBarItem) -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick(item) }
    ) {
        Icon(
            painterResource(id = item.drawableId),
            contentDescription = stringResource(item.labelResId),
            tint = Color.Unspecified
        )
        Text(stringResource(item.labelResId))
    }
}

enum class BottomBarItem(
    val labelResId: Int,
    val drawableId: Int,
) {
    RENT(R.string.rent_tab, R.drawable.ic_bottom_bar_rent),
    HOME(R.string.home_tab, R.drawable.ic_bottom_bar_home),
    PROFILE(R.string.profile_tab, R.drawable.ic_bottom_bar_profile),
}