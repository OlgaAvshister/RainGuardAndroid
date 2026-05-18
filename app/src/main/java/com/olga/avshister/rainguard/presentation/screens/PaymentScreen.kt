package com.olga.avshister.rainguard.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.olga.avshister.rainguard.domain.payment.Card
import com.olga.avshister.rainguard.R
import com.olga.avshister.rainguard.presentation.state.BSheetContentState
import com.olga.avshister.rainguard.presentation.viewmodel.PaymentViewModel

@Composable
fun PaymentScreen(
    onNextState: (state: BSheetContentState) -> Unit,
) {
    val viewModelKey = remember {
        "PaymentScreen${System.currentTimeMillis()}"
    }

    val viewModel: PaymentViewModel = viewModel(key = viewModelKey)


    when (viewModel.uiState) {
        PaymentViewModel.PaymentUiState.Select -> {
            SelectState(viewModel, onNextClicked = { onNextState(BSheetContentState.IdleState) })
        }
        PaymentViewModel.PaymentUiState.Add -> AddState(viewModel)
        PaymentViewModel.PaymentUiState.Success -> {
            SuccessScreen(
                message = stringResource(R.string.card_added),
                buttonText = stringResource(R.string.great),
                onClick = { viewModel.backToSelect() }
            )
        }
    }
}

// Экран с сохраненными картами пользователя
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectState(
    viewModel: PaymentViewModel,
    onNextClicked: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(16.dp)
    ) {

        TopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.select_pay_variant_toolbar),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            },
            navigationIcon = {
                IconButton(onClick = { viewModel.onBack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back)
                    )
                }
            }
        )

        Spacer(Modifier.height(24.dp))

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(viewModel.cards) { card ->
                CardItem(
                    card = card,
                    isSelected = card == viewModel.selectedCard
                ) {
                    viewModel.selectCard(card)
                }
                Spacer(Modifier.height(12.dp))
            }
        }

        Button(
            onClick = { viewModel.goToAdd() },
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(6.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(stringResource(R.string.add_new_card), color = Color.Black)
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.confirmSelection()
                onNextClicked.invoke()
        },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(stringResource(R.string.proceed))
        }
    }
}

@Composable
private fun CardItem(
    card: Card,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5), RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Image(
            painter = painterResource(id = R.drawable.ic_card),
            contentDescription = null,
            modifier = Modifier.size(32.dp)
        )

        Spacer(Modifier.width(16.dp))

        Text(
            text = "Visa ${card.number}",
            modifier = Modifier.weight(1f)
        )

        if (isSelected) {
            Icon(
                painter = painterResource(id = R.drawable.ic_checked),
                contentDescription = null
            )
        }
    }
}

// Экран добавления новой карты

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddState(viewModel: PaymentViewModel) {

    var number by remember { mutableStateOf("") }
    var expired by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(16.dp)
    ) {

        TopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.fill_card_data_toolbar),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            },
            navigationIcon = {
                IconButton(onClick = { viewModel.onBack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back)
                    )
                }
            }
        )

        Spacer(Modifier.height(16.dp))

        Text(stringResource(R.string.check_card_text))

        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = number,
            onValueChange = { number = it },
            label = { Text(stringResource(R.string.card_number_label)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        Row {
            OutlinedTextField(
                value = expired,
                onValueChange = { expired = it },
                label = { Text(stringResource(R.string.card_expires_label)) },
                modifier = Modifier.weight(1f)
            )

            Spacer(Modifier.width(16.dp))

            OutlinedTextField(
                value = cvv,
                onValueChange = { cvv = it },
                label = { Text(stringResource(R.string.card_expires_cvv_label)) },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.card_data_encrypted),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.weight(1f))

        Button(
            onClick = {
                viewModel.addCard(number, expired, cvv)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(stringResource(R.string.add))
        }
    }
}
