package com.olga.avshister.rainguard.presentation.screens.owner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.olga.avshister.rainguard.presentation.state.BSheetContentState
import com.olga.avshister.rainguard.presentation.ui.components.HeaderRentPoint
import com.olga.avshister.rainguard.presentation.viewmodel.owner.OwnerEvent
import com.olga.avshister.rainguard.presentation.viewmodel.owner.OwnerRentPointViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerRentPointScreen(
    rentPointId: Long,
    onNextState: (state: BSheetContentState) -> Unit,
) {
    val viewModel: OwnerRentPointViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()


    LaunchedEffect(Unit) {
        viewModel.loadRentPoint(rentPointId)
    }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is OwnerEvent.RentPointDeleted -> {
                    onNextState(BSheetContentState.IdleState)
                }
                else -> {}
            }
        }
    }

    Scaffold(
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 1. Информация о пункте выдачи
            item {
                HeaderRentPoint(
                    title = uiState.rentPoint?.name,
                    address = uiState.rentPoint?.address,
                    openingHours = uiState.rentPoint?.workHours ?: ""
                )
            }

            // 2. Секция добавления сотрудника
            item {
                AddStuffSection(
                    onAddStuff = { name, phone ->
                        viewModel.addStuff(name, phone)
                    }
                )
            }

            // 3. Секция добавления товара
            item {
                AddProductSection(
                    onAddProduct = { product ->
                        viewModel.addProduct(product)
                    }
                )
            }

            // 4. Секция Финансы
            item {
                FinanceSection(
                    onShowFinance = {
                        viewModel.loadFinancialData(rentPointId)
                    }
                )
            }
            // 5. Секция статуса товаров
            item {
                ProductsStatusCard(uiState.productsStatus)
            }

            // 6. Секция Удалить пункт выдачи
            item {
                DeleteRentPointSection(
                    onDelete = {
                        viewModel.showDeleteConfirmation(true)
                    }
                )
            }
        }
    }

    if (uiState.showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { viewModel.showDeleteConfirmation(false) },
            title = { Text("Удалить пункт выдачи") },
            text = { Text("Вы уверены, что хотите удалить этот пункт выдачи? Это действие нельзя отменить.") },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.deleteRentPoint(rentPointId) }
                ) {
                    Text("Удалить", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.showDeleteConfirmation(false) }
                ) {
                    Text("Отмена")
                }
            }
        )
    }

    // Диалог с графиками финансов
    if (uiState.showFinancialDialog) {
        FinancialDialog(
            financialData = viewModel.financialData.value,
            onDismiss = { viewModel.showFinancialDialog(false) }
        )
    }

    // Snackbar для ошибок
    if (uiState.error != null) {
        SnackbarHost(
            hostState = remember { SnackbarHostState() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Snackbar(
                action = {
                    TextButton(onClick = { viewModel.clearError() }) {
                        Text("OK")
                    }
                }
            ) {
                Text(uiState.error ?: "")
            }
        }
    }
}