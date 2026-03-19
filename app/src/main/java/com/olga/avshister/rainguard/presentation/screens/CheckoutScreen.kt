package com.olga.avshister.rainguard.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.olga.avshister.rainguard.R
import com.olga.avshister.rainguard.domain.Checkout
import com.olga.avshister.rainguard.domain.products.Product
import com.olga.avshister.rainguard.domain.rent.Rate
import com.olga.avshister.rainguard.presentation.core.CARDS_SCREEN
import com.olga.avshister.rainguard.presentation.state.BSheetContentState
import com.olga.avshister.rainguard.presentation.ui.theme.RainGuardTheme
import com.olga.avshister.rainguard.presentation.viewmodel.CheckoutViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
fun CheckoutScreen(
    onNextState: (state: BSheetContentState) -> Unit,
) {
    val viewModelKey = remember {
        "CheckoutScreen_${System.currentTimeMillis()}"
    }

    val viewModel: CheckoutViewModel = viewModel(
        key = viewModelKey
    )

    val uiState by viewModel.uiState.collectAsState()

    val rates = Rate.entries
    val pagerState = rememberPagerState(
        initialPage = uiState.selectedRateIndex,
        pageCount = { rates.size }
    )

    // Синхронизация ViewPager с состоянием ViewModel
    LaunchedEffect(pagerState.currentPage) {
        viewModel.selectRate(pagerState.currentPage)
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // ViewPager с тарифами
            RatePager(
                rates = rates,
                pagerState = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Заголовок секции товаров
            Text(
                text = stringResource(R.string.checkout_your_choice),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Список товаров
            ProductsList(
                products = uiState.checkout.products,
                modifier = Modifier.weight(1f)
            )

            // Нижняя панель с итогом
            BottomSummary(
                rate = uiState.checkout.rate,
                totalPrice = uiState.checkout.rate.priceValue * uiState.checkout.products.size,
                productCount = uiState.checkout.products.size,
                modifier = Modifier.fillMaxWidth(),
                toCardsClicked = {
                    viewModel.saveCheckout(uiState.checkout.rate)
                    onNextState(BSheetContentState.CardsState)
                }
            )
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun RatePager(
    rates: List<Rate>,
    pagerState: androidx.compose.foundation.pager.PagerState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
                .padding(horizontal = 4.dp), // Небольшой отступ по краям
            horizontalArrangement = Arrangement.spacedBy(4.dp) // Отступ между индикаторами
        ) {
            repeat(rates.size) { page ->
                val isSelected = pagerState.currentPage == page
                Box(
                    modifier = Modifier
                        .weight(1f) // Каждый индикатор занимает равную долю ширины
                        .height(6.dp)
                        .background(
                            color = if (isSelected) Color(0xFF6A00FF) else Color.Black,
                            shape = RoundedCornerShape(4.dp)
                        )
                )
            }
        }

        // HorizontalPager
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(164.dp)
                .padding(top = 12.dp)
        ) { page ->
            RateCard(
                rate = rates[page],
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 4.dp)
            )
        }
    }
}

@Composable
fun RateCard(
    rate: Rate,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFFCC25FF), Color(0xFF5E00FF))
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                modifier = Modifier.padding(vertical = 16.dp),
                text = rate.textValue,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${rate.priceValue} ₽/${rate.timeUnit}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}

@Composable
fun ProductsList(
    products: List<Product>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Группируем товары по парам
        items(
            items = products.chunked(2),
            key = { it.joinToString("-") { it.id.toString() } }
        ) { pairProducts ->
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Первый товар в паре
                pairProducts.getOrNull(0)?.let { product ->
                    ProductItem(
                        product = product,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Второй товар в паре
                pairProducts.getOrNull(1)?.let { product ->
                    ProductItem(
                        product = product,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Если второй товар отсутствует, добавляем пустой Spacer для баланса
                if (pairProducts.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun ProductItem(
    product: Product,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Изображение товара
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = product.image),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(4.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = product.productType.value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Характеристики товара
            val characteristics = buildList {
                add(product.color.value)
                if (product.size != null) {
                    add("${product.size.value}")
                }
                add(product.formFactor.value)
                add(product.printType.value)
            }

            Text(
                text = characteristics.joinToString(" • "),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                maxLines = 2,
                minLines = 2,
                lineHeight = 14.sp,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )

            if (product.article != -1L) {
                Text(
                    text = "Арт. ${product.article}",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun BottomSummary(
    rate: Rate,
    totalPrice: Int,
    productCount: Int,
    modifier: Modifier = Modifier,
    toCardsClicked: () -> Unit
) {
    Surface(
        modifier = modifier,
        tonalElevation = 3.dp,
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(R.string.checkout_total),
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Text(
                    text = "$totalPrice ₽/${rate.timeUnit}",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "за $productCount ${pluralize(productCount, "вещь", "вещи", "вещей")}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }

            Button(
                onClick = {
                    toCardsClicked.invoke()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF5E00FF)
                ),
                modifier = Modifier
                    .height(50.dp)
                    .width(120.dp)
            ) {
                Text(
                    text = stringResource(R.string.checkout_choose),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// Вспомогательная функция для склонения
private fun pluralize(count: Int, one: String, few: String, many: String): String {
    return when {
        count % 10 == 1 && count % 100 != 11 -> one
        count % 10 in 2..4 && (count % 100 !in 12..14) -> few
        else -> many
    }
}

@Preview(showBackground = true)
@Composable
fun RateCardPreview() {
    RainGuardTheme {
        RateCard(
            Rate.PER_MINUTE,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 4.dp)
        )
    }
}