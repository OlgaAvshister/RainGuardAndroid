package com.olga.avshister.rainguard.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.olga.avshister.rainguard.R
import com.olga.avshister.rainguard.presentation.state.BSheetContentState
import com.olga.avshister.rainguard.presentation.ui.components.PrimaryButton

@Composable
fun GiveToCheckScreen(
    onNextState: (state: BSheetContentState) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
            .navigationBarsPadding()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.give_products_to_check),
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.if_broken_you_should_pay),
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Image(
            painter = painterResource(id = R.drawable.image_give_to_check),
            contentDescription = null,
            modifier = Modifier.size(250.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        PrimaryButton(
            text = stringResource(R.string.i_understood),
            onClick = {
                onNextState(BSheetContentState.FillStuffNumberState)
            }
        )
    }
}