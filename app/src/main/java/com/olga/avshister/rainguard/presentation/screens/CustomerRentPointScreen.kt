package com.olga.avshister.rainguard.presentation.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.olga.avshister.rainguard.R
import com.olga.avshister.rainguard.domain.products.Product
import com.olga.avshister.rainguard.domain.rent.RentPoint
import com.olga.avshister.rainguard.presentation.screens.RentPointBottomSheet.PAGE_COUNT
import com.olga.avshister.rainguard.presentation.screens.RentPointBottomSheet.TAB_RAINCOAT
import com.olga.avshister.rainguard.presentation.screens.RentPointBottomSheet.TAB_UMBRELLA
import com.olga.avshister.rainguard.presentation.state.BSheetContentState
import com.olga.avshister.rainguard.presentation.state.rent.RentState
import com.olga.avshister.rainguard.presentation.ui.components.HeaderRentPoint
import com.olga.avshister.rainguard.presentation.ui.components.PrimaryButton
import com.olga.avshister.rainguard.presentation.ui.components.RentInfo
import com.olga.avshister.rainguard.presentation.viewmodel.CustomerRentPointViewModel
import com.olga.avshister.rainguard.presentation.viewmodel.CustomerRentPointViewModel.Intent
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CustomerRentPointScreen(
    rentPoint: RentPoint,
    onNextState: (state: BSheetContentState) -> Unit,
) {
    val context = LocalContext.current

    val viewModelKey = remember {
        "RentPoint_${System.currentTimeMillis()}"
    }

    val viewModel: CustomerRentPointViewModel = viewModel(
        key = viewModelKey,
        factory = CustomerRentPointViewModel.RentPointViewModelFactory(context, rentPoint)
    )

    val customerRentPointState by viewModel.customerRentPointState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onIntent(Intent.LoadData)
    }

    if (customerRentPointState.loadingState) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .imePadding()
                .padding(12.dp)
        ) {
            if (customerRentPointState.rentState.items.isNotEmpty()) {
                ActiveRentBSheetContent(
                    rentPoint = rentPoint,
                    rentState = customerRentPointState.rentState,
                    onNextState = { onNextState(it) }
                )
            } else {
                FiltersBSheetContent(
                    rentPoint = rentPoint,
                    state = customerRentPointState,
                    onIntent = {
                        viewModel.onIntent(it)
                    },
                    onNextState = {
                        onNextState(
                            BSheetContentState.CatalogState(
                                filter = customerRentPointState.filterState,
                                rentPointId = rentPoint.id
                            )
                        )
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun FiltersBSheetContent(
    rentPoint: RentPoint,
    state: CustomerRentPointViewModel.CustomerRentPointState,
    onIntent: (intent: Intent) -> Unit,
    onNextState: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(
        initialPage = TAB_UMBRELLA,
        pageCount = { PAGE_COUNT }
    )

    val tabs = listOf(
        stringResource(R.string.tab_umbrella),
        stringResource(R.string.tab_raincoats)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        HeaderRentPoint(
            title = rentPoint.name,
            address = rentPoint.address,
            openingHours = rentPoint.workHours
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Tabs
        TabRow(selectedTabIndex = pagerState.currentPage) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        scope.launch {
                            pagerState.scrollToPage(index)
                        }
                    },
                    text = { Text(title) }
                )
            }
        }

        // Контент вкладок
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) { page ->
            when (page) {
                TAB_UMBRELLA -> UmbrellaContent(
                    state,
                    onIntent = { onIntent(it) }
                )
                TAB_RAINCOAT -> RaincoatContent(
                    state,
                    onIntent = { onIntent(it) }
                )
            }
        }

        // Нижняя кнопка
        BottomSection(
            buttonText = stringResource(R.string.next),
            onClicked = {
                onNextState()
            }
        )
    }
}

@Composable
private fun ActiveRentBSheetContent(
    rentPoint: RentPoint,
    rentState: RentState,
    onNextState: (state: BSheetContentState) -> Unit,
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        // Заголовок и адрес
        HeaderRentPoint(
            title = rentPoint.name,
            address = rentPoint.address,
            openingHours = rentPoint.workHours
        )

        Spacer(modifier = Modifier.height(16.dp))

        RentInfo(state = rentState)

        // Нижняя кнопка
        BottomSection(
            buttonText = stringResource(R.string.return_here_active_rent_bsheet),
            onClicked = {
                onNextState(BSheetContentState.SelectIdsStateToDropState(rentPoint.id))
            }
        )
    }
}

@Composable
fun UmbrellaContent(
    state: CustomerRentPointViewModel.CustomerRentPointState,
    onIntent: (Intent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CheckboxGroup(
            title = stringResource(R.string.filter_type_select),
            options = mapOf(
                stringResource(R.string.filter_type_select_folding) to Product.FormFactor.FOLDING,
                stringResource(R.string.filter_type_select_stick) to Product.FormFactor.STICK
            ),
            selected = state.filterState.umbrellaFilter?.formFactor,
            onSelected = {
                onIntent(
                    Intent.SelectFormFactor(type = Product.ProductType.UMBRELLA, it)
                )
            }
        )

        CheckboxGroup(
            title = stringResource(R.string.filter_print_select),
            options = mapOf(
                stringResource(R.string.filter_print_select_yes) to Product.PrintType.WITH_PRINT,
                stringResource(R.string.filter_print_select_no) to Product.PrintType.WITHOUT_PRINT
            ),
            selected = state.filterState.umbrellaFilter?.printType,
            onSelected = {
                onIntent(Intent.SelectPrintType(type = Product.ProductType.UMBRELLA, it))
            }
        )
    }
}

@Composable
fun RaincoatContent(
    state: CustomerRentPointViewModel.CustomerRentPointState,
    onIntent: (Intent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CheckboxGroup(
            title = stringResource(R.string.filter_type_select),
            options = mapOf(
                stringResource(R.string.filter_type_select_jacket) to Product.FormFactor.JACKET,
                stringResource(R.string.filter_type_select_raincoat) to Product.FormFactor.RAINCOAT
            ),
            selected = state.filterState.raincoatFilter?.formFactor,
            onSelected = {
                onIntent(Intent.SelectFormFactor(type = Product.ProductType.RAINCOAT, it))
            }
        )

        CheckboxGroup(
            title = stringResource(R.string.filter_print_select),
            options = mapOf(
                stringResource(R.string.filter_print_select_yes) to Product.PrintType.WITH_PRINT,
                stringResource(R.string.filter_print_select_no) to Product.PrintType.WITHOUT_PRINT
            ),
            selected = state.filterState.raincoatFilter?.printType,
            onSelected = {
                onIntent(Intent.SelectPrintType(Product.ProductType.RAINCOAT,it))
            }
        )

        CheckboxGroup(
            title = stringResource(R.string.filter_size_select),
            options = mapOf(
                Product.Size.XS.value to Product.Size.XS,
                Product.Size.S.value to Product.Size.S,
                Product.Size.M.value to Product.Size.M,
                Product.Size.L.value to Product.Size.L,
                Product.Size.XL.value to Product.Size.XL
            ),
            selected = state.filterState.raincoatFilter?.size,
            onSelected = {
                onIntent(Intent.SelectSize(it))
            }
        )
    }
}

@Composable
fun <T> CheckboxGroup(
    title: String,
    options: Map<String, T>,
    selected: T?,
    onSelected: (T) -> Unit
) {
    Column {
        Text(
            text = title,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            options.forEach { (label, value) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        onSelected(value)
                    }
                ) {
                    Checkbox(
                        modifier = Modifier.size(40.dp),
                        checked = selected == value,
                        onCheckedChange = { onSelected(value) }
                    )
                    Text(text = label)
                }
            }
        }
    }
}

@Composable
fun BottomSection(
    buttonText: String,
    onClicked: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {

        PrimaryButton(
            text = buttonText,
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                onClicked.invoke()
            }
        )
    }
}

object RentPointBottomSheet {
    const val TAB_UMBRELLA = 0
    const val TAB_RAINCOAT = 1
    const val PAGE_COUNT = 2
}