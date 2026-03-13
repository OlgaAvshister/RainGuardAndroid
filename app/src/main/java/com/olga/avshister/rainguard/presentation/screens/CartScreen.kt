import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.olga.avshister.rainguard.R
import com.olga.avshister.rainguard.domain.products.Product
import com.olga.avshister.rainguard.presentation.state.BSheetContentState
import com.olga.avshister.rainguard.presentation.ui.components.PrimaryButton
import com.olga.avshister.rainguard.presentation.viewmodel.CartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    selectedArticles: Set<Long>,
    rentPointId: Long,
    onNextState: (state: BSheetContentState) -> Unit,
) {
    val context = LocalContext.current

    if (selectedArticles == null || rentPointId == null) {
        // Показываем заглушку пока параметры не загружены
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val viewModel: CartViewModel = viewModel(
        factory = remember(rentPointId, selectedArticles) {
            CartViewModel.CartViewModelFactory(
                context = context,
                rentPointId = rentPointId,
                selectedArticles = selectedArticles
            )
        }
    )

    // Собираем состояние из ViewModel
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {

        // ---------- Toolbar ----------
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.cart_toolbar),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
            },
            navigationIcon = {
                IconButton(onClick = { /*navController.popBackStack()*/ }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back)
                    )
                }
            }
        )

        // Показываем состояние загрузки
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Column
        }

        // Показываем ошибку если есть
        state.error?.let { error ->
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = error,
                        color = Color.Red,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { /*navController.popBackStack()*/ }
                    ) {
                        Text(stringResource(R.string.back))
                    }
                }
            }
            return@Column
        }

        // ---------- Содержимое корзины ----------
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = colorResource(R.color.gray_f8f8f8))
                .padding(top = 24.dp, start = 24.dp, end = 24.dp)
        ) {
            // Используем данные из State, а не вызываем метод getProductsByArticles
            CartProductList(
                cartItems = state.cart.items,
                onQuantityChange = { product, isAdded ->
                    if (isAdded) {
                        viewModel.onIntent(CartViewModel.Intent.AddToCart(product.article))
                    } else {
                        viewModel.onIntent(CartViewModel.Intent.RemoveFromCart(product.article))
                    }
                }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        PrimaryButton(
            text = stringResource(R.string.next),
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = {
                onNextState(BSheetContentState.SelectIdsStateToTakeState)
                /*navController.navigate(SELECT_IDS_SCREEN)*/
            }
        )
    }
}

@Composable
fun CartProductList(
    cartItems: List<CartViewModel.CartProductUI>,
    onQuantityChange: (Product, Boolean) -> Unit
) {
    if (cartItems.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Корзина пуста",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }
        return
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(cartItems) { cartItem ->
            CartProductItem(
                cartItem = cartItem,
                onQuantityChange = onQuantityChange
            )
        }
    }
}

@Composable
fun CartProductItem(
    cartItem: CartViewModel.CartProductUI,
    onQuantityChange: (Product, Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top // Меняем на Top для правильного выравнивания
    ) {
        // Колонка для картинки и текста цвета
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(136.dp)
        ) {
            // Картинка товара
            Image(
                modifier = Modifier
                    .width(120.dp) // Фиксированная ширина
                    .height(140.dp), // Фиксированная высота
                painter = painterResource(id = cartItem.product.image),
                contentDescription = null,
                contentScale = ContentScale.Fit
            )

            // Текст цвета прямо под картинкой
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp) // Отступ от картинки
            ) {
                Text(
                    text = cartItem.product.color.value,
                    fontSize = 14.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Текст и счетчик
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = cartItem.product.article.toString(),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Отображаем доступное количество
            Text(
                text = "Доступно: ${cartItem.maxQuantity} шт.",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Счетчик
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                CounterButton(
                    icon = if (cartItem.currentQuantity > 0) {
                        R.drawable.ic_minus
                    } else {
                        R.drawable.ic_minus_disabled
                    },
                    enabled = cartItem.currentQuantity > 0,
                    onClick = {
                        onQuantityChange(cartItem.product, false)
                    }
                )

                Text(
                    text = cartItem.currentQuantity.toString(),
                    modifier = Modifier.padding(horizontal = 16.dp),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold
                )

                CounterButton(
                    icon = if (cartItem.currentQuantity < cartItem.maxQuantity) {
                        R.drawable.ic_plus
                    } else {
                        R.drawable.ic_plus_disabled
                    },
                    enabled = cartItem.currentQuantity < cartItem.maxQuantity,
                    onClick = {
                        onQuantityChange(cartItem.product, true)
                    }
                )
            }

            // Показываем предупреждение если достигнут максимум
            if (cartItem.currentQuantity >= cartItem.maxQuantity) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Достигнут лимит доступных товаров",
                    fontSize = 12.sp,
                    color = Color.Red
                )
            }
        }
    }
}

@Composable
private fun CounterButton(
    icon: Int,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Surface(
        shape = CircleShape,
        color = if (enabled) Color(0xFF6A00FF) else Color.LightGray,
        modifier = Modifier
            .size(44.dp)
            .clickable(
                enabled = enabled,
                onClick = onClick
            )
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = "Кнопка",
                colorFilter = if (enabled) null else ColorFilter.tint(Color.White)
            )
        }
    }
}