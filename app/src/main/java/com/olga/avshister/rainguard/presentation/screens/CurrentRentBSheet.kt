package com.olga.avshister.rainguard.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.olga.avshister.rainguard.R
import com.olga.avshister.rainguard.presentation.state.rent.RentState
import com.olga.avshister.rainguard.presentation.ui.components.PrimaryButton
import com.olga.avshister.rainguard.presentation.ui.components.RentInfo
import com.olga.avshister.rainguard.presentation.viewmodel.CurrentRentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrentRentBSheet(
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true, // запрещаем частичное открытие
        confirmValueChange = { true }
    )

    val viewModelKey = remember {
        "CurrentRentBSheet_${System.currentTimeMillis()}"
    }
    val viewModel: CurrentRentViewModel = viewModel(key = viewModelKey)
    val state by viewModel.state.collectAsState()

    // Обработка событий
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is CurrentRentViewModel.Event.Close -> {
                    onDismiss()
                }
                else -> {}
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        scrimColor = Color.Black.copy(alpha = 0.3f),
    ) {
        CurrentRentContent(
            state = state,
            onCloseClick = {
                viewModel.handleIntent(CurrentRentViewModel.Intent.OnCloseButton)
            }
        )
    }
}

@Composable
private fun CurrentRentContent(
    state: RentState,
    onCloseClick: () -> Unit
) {
    if (state.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .navigationBarsPadding()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(16.dp)
    ) {
        // Заголовок
        Text(
            text = stringResource(R.string.current_rent_title),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Тот же компонент, что и на экране завершения аренды
        RentInfo(state = state)

        Spacer(modifier = Modifier.height(20.dp))

        // Кнопка завершения
        PrimaryButton(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = stringResource(R.string.close_active_rent_bsheet),
            onClick = onCloseClick
        )

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCurrentRentBSheet() {
    MaterialTheme {
        CurrentRentBSheet(
            onDismiss = {},
        )
    }
}