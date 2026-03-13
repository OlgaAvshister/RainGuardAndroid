package com.olga.avshister.rainguard.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.olga.avshister.rainguard.R
import com.olga.avshister.rainguard.presentation.state.BSheetContentState
import com.olga.avshister.rainguard.presentation.ui.components.PrimaryButton
import com.olga.avshister.rainguard.presentation.viewmodel.RentTotalViewModel

@Composable
fun RentTotalScreen(
    onNextState: (state: BSheetContentState) -> Unit,
) {
    val viewModel: RentTotalViewModel = viewModel()
    val state = viewModel.state.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {

            Text(
                text = stringResource(R.string.everything_good),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Подзаголовок
            Text(
                text = stringResource(R.string.you_can_pay_now),
                fontSize = 16.sp,
                color = Color(0xFF666666),
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Карточка с деталями
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF8F9FA)
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 0.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    // Время аренды
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(R.string.total_rent_time),
                            fontSize = 16.sp,
                            color = Color(0xFF666666)
                        )
                        Text(
                            text = state.value.totalTime,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1A1A)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Стоимость
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(R.string.total_to_pay),
                            fontSize = 16.sp,
                            color = Color(0xFF666666)
                        )
                        Text(
                            text = state.value.totalCost,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1A1A)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Кнопка оплаты
            PrimaryButton(
                text = stringResource(R.string.pay_btn_text),
                onClick = {
                    onNextState(BSheetContentState.PayInProgressState)
                }
            )
        }
    }
}