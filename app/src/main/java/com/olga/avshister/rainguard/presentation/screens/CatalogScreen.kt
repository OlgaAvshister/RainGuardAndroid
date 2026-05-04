package com.olga.avshister.rainguard.presentation.screens

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.olga.avshister.rainguard.R
import com.olga.avshister.rainguard.data.rent_point.RentPointRemoteRepository
import com.olga.avshister.rainguard.data.rent_point.RentPointRepository
import com.olga.avshister.rainguard.domain.filter.ConcatFilter
import com.olga.avshister.rainguard.domain.products.Product
import com.olga.avshister.rainguard.domain.products.Product.Companion.toSetByArticle
import com.olga.avshister.rainguard.presentation.state.BSheetContentState
import com.olga.avshister.rainguard.presentation.ui.components.PrimaryButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    filter: ConcatFilter,
    rentPointId: Long,
    onNextState: (state: BSheetContentState) -> Unit,
    ) {

    Log.d("CatalogScreen", "filter=$filter, rentPointId=$rentPointId")

    val rentPointRepository: RentPointRepository = RentPointRemoteRepository(LocalContext.current)

    var selectedArticles by remember {
        mutableStateOf<Set<Long>>(emptySet())
    }

    var productGroups by remember {
        mutableStateOf<List<Product>>(emptyList())
    }

    var products by remember {
        mutableStateOf<List<Product>>(emptyList())
    }

    LaunchedEffect(Unit) {
        products = rentPointRepository
            .searchProducts(filter, rentPointId)
            .filter { it.condition == Product.ProductCondition.READY }
            .apply {
            productGroups = this.toSetByArticle()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {

        // ---------- Toolbar ----------
        TopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.catalog_select_products),
                    fontSize = 18.sp,
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

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {

            LazyHorizontalGrid(
                rows = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(productGroups) { group ->
                    val isSelected = selectedArticles.contains(group.article)

                    ProductItem(
                        groupUiModel = getGroupUiModel(
                            group,
                            products.filter { it.article == group.article }.size
                        ),
                        isSelected = isSelected,
                        onClick = {
                            selectedArticles = if (isSelected) {
                                selectedArticles - group.article
                            } else {
                                selectedArticles + group.article
                            }
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ---------- Bottom button ----------
        PrimaryButton(
            text = stringResource(R.string.next),
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = {
                onNextState(BSheetContentState.CartState(selectedArticles, rentPointId))
            }
        )
    }
}

@Composable
fun ProductItem(
    groupUiModel: GroupUiModel,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        border = if (isSelected) {
            BorderStroke(2.dp, colorResource(R.color.violet))
        } else {
            null
        },
        modifier = Modifier
            .wrapContentHeight()
            .width(136.dp)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = groupUiModel.resIdImg),
                contentDescription = groupUiModel.text,
                modifier = Modifier.size(136.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = groupUiModel.text,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        }
    }
}

fun getGroupUiModel(group: Product, count: Int): GroupUiModel {
    val text =
        "Артикул: ${group.article}\nЦвет: ${group.color.value}\nРазмер: ${group.size ?: "отсутствует"}\nТип: ${group.formFactor.value}\nПринт: ${group.printType.value}\nКоличество: $count"
    return GroupUiModel(resIdImg = group.getImageResource(), text)
}

data class GroupUiModel(
    val resIdImg: Int,
    val text: String,
)

@Preview(showBackground = true)
@Composable
fun CatalogScreenPreview() {
    MaterialTheme {
        CatalogScreen(
            filter = ConcatFilter(
                umbrellaFilter = null,
                raincoatFilter = null,
            ),
            rentPointId = 1,
            onNextState = {})
    }
}