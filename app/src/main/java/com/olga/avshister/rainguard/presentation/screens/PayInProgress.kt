package com.olga.avshister.rainguard.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.olga.avshister.rainguard.R
import com.olga.avshister.rainguard.presentation.state.BSheetContentState
import com.olga.avshister.rainguard.presentation.viewmodel.PayInProgressViewModel

@Composable
fun PayInProgress(
    onNextState: (state: BSheetContentState) -> Unit,
) {
    val viewModel: PayInProgressViewModel = viewModel()
    val state = viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.waiting_for_money),
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.it_may_take_time),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
        Image(
            painter = painterResource(id = R.drawable.image_waiting_for_money),
            contentDescription = null,
            modifier = Modifier.size(250.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (state.value.isLoading) {
            CircularProgressIndicator()
        }

        if (state.value.isPayFinished) {
            onNextState(BSheetContentState.PaySuccessState)
        }
    }
}