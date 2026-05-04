package com.olga.avshister.rainguard.presentation.screens.owner

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.olga.avshister.rainguard.domain.products.Product
import com.olga.avshister.rainguard.domain.products.Product.Companion.DEFAULT_PRODUCT_ID
import com.olga.avshister.rainguard.domain.products.Product.Companion.withGeneratedArticle
import com.olga.avshister.rainguard.presentation.ui.components.CustomDropdownMenu
import com.olga.avshister.rainguard.R

@Composable
fun AddProductSection(
    onAddProduct: (Product) -> Unit
) {
    var selectedType by remember { mutableStateOf<Product.ProductType?>(null) }
    var selectedFormFactor by remember { mutableStateOf<Product.FormFactor?>(null) }
    var selectedColor by remember { mutableStateOf<Product.Colors?>(null) }
    var selectedPrintType by remember { mutableStateOf<Product.PrintType?>(null) }
    var size by remember { mutableStateOf<Product.Size?>(null) }

    val availableFormFactors = when (selectedType) {
        Product.ProductType.UMBRELLA -> listOf(Product.FormFactor.FOLDING, Product.FormFactor.STICK)
        Product.ProductType.RAINCOAT -> listOf(Product.FormFactor.JACKET, Product.FormFactor.RAINCOAT)
        else -> emptyList()
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.add_product),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            CustomDropdownMenu(
                label = stringResource(R.string.add_product_product_type),
                options = Product.ProductType.entries,
                selectedOption = selectedType,
                onOptionSelected = {
                    selectedType = it
                    selectedFormFactor = null
                },
                valueFormatter = { it.value }
            )

            if (selectedType != null) {
                CustomDropdownMenu(
                    label = stringResource(R.string.add_product_form_factor),
                    options = availableFormFactors,
                    selectedOption = selectedFormFactor,
                    onOptionSelected = { selectedFormFactor = it },
                    valueFormatter = { it.value }
                )
            }

            CustomDropdownMenu(
                label = stringResource(R.string.add_product_color),
                options = if (selectedType == Product.ProductType.UMBRELLA) {
                    Product.Colors.entries
                } else {
                    listOf(
                        Product.Colors.YELLOW, Product.Colors.RED, // для дождевика только эти два цвета
                    )
               },
                selectedOption = selectedColor,
                onOptionSelected = { selectedColor = it },
                valueFormatter = { it.value }
            )

            CustomDropdownMenu(
                label = stringResource(R.string.add_product_print),
                options = Product.PrintType.entries,
                selectedOption = selectedPrintType,
                onOptionSelected = { selectedPrintType = it },
                valueFormatter = { it.value }
            )

            Button(
                onClick = {
                    if (selectedType != null && selectedFormFactor != null &&
                        selectedColor != null && selectedPrintType != null
                    ) {
                        onAddProduct(
                            Product(
                                id = DEFAULT_PRODUCT_ID, // в БД у нас автоинкремент (сгенерируется сам)
                                productType = selectedType!!,
                                formFactor = selectedFormFactor!!,
                                color = selectedColor!!,
                                printType = selectedPrintType!!,
                                size = null,
                            ).withGeneratedArticle()
                        )
                        // Очищаем форму
                        selectedType = null
                        selectedFormFactor = null
                        selectedColor = null
                        selectedPrintType = null
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                enabled = selectedType != null &&
                        selectedFormFactor != null &&
                        selectedColor != null &&
                        selectedPrintType != null
            ) {
                Text(stringResource(R.string.save_changes))
            }
        }
    }
}