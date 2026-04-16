package com.olga.avshister.rainguard.presentation.screens.owner

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.olga.avshister.rainguard.domain.products.Product
import com.olga.avshister.rainguard.domain.products.Product.Companion.withGeneratedArticul
import com.olga.avshister.rainguard.presentation.ui.components.CustomDropdownMenu
import com.olga.avshister.rainguard.presentation.ui.utils.Utils

@Composable
fun AddProductSection(
    onAddProduct: (Product) -> Unit
) {
    var productId by remember { mutableStateOf("") }
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
                text = "Добавить товар",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            OutlinedTextField(
                value = productId,
                onValueChange = { productId = it },
                label = { Text("Инвентарный номер") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            CustomDropdownMenu(
                label = "Вид",
                options = Product.ProductType.values().toList(),
                selectedOption = selectedType,
                onOptionSelected = {
                    selectedType = it
                    selectedFormFactor = null
                },
                valueFormatter = { it.value }
            )

            if (selectedType != null) {
                CustomDropdownMenu(
                    label = "Тип",
                    options = availableFormFactors,
                    selectedOption = selectedFormFactor,
                    onOptionSelected = { selectedFormFactor = it },
                    valueFormatter = { it.value }
                )
            }

            CustomDropdownMenu(
                label = "Цвет",
                options = Product.Colors.values().toList(),
                selectedOption = selectedColor,
                onOptionSelected = { selectedColor = it },
                valueFormatter = { it.value }
            )

            CustomDropdownMenu(
                label = "Принт",
                options = Product.PrintType.values().toList(),
                selectedOption = selectedPrintType,
                onOptionSelected = { selectedPrintType = it },
                valueFormatter = { it.value }
            )

            Button(
                onClick = {
                    productId.toLongOrNull()?.let { id ->
                        if (selectedType != null && selectedFormFactor != null &&
                            selectedColor != null && selectedPrintType != null
                        ) {
                            onAddProduct(
                                Product(
                                    id = id,
                                    productType = selectedType!!,
                                    formFactor = selectedFormFactor!!,
                                    color = selectedColor!!,
                                    printType = selectedPrintType!!,
                                    size = null,
                                ).withGeneratedArticul()
                            )
                            // Очищаем форму
                            productId = ""
                            selectedType = null
                            selectedFormFactor = null
                            selectedColor = null
                            selectedPrintType = null
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                enabled = productId.isNotBlank() &&
                        selectedType != null &&
                        selectedFormFactor != null &&
                        selectedColor != null &&
                        selectedPrintType != null
            ) {
                Text("Сохранить")
            }
        }
    }
}