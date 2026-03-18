package com.olga.avshister.rainguard.presentation.screens.owner

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.olga.avshister.rainguard.R
import com.olga.avshister.rainguard.domain.rent.RentPoint
import com.olga.avshister.rainguard.presentation.state.BSheetContentState
import com.olga.avshister.rainguard.presentation.ui.components.HeaderRentPoint
import com.olga.avshister.rainguard.presentation.viewmodel.owner.OwnerRentPointViewModel

@Composable
fun OwnerRentPointScreen(
    rentPoint: RentPoint,
    onNextState: (state: BSheetContentState) -> Unit,
) {
    val viewModel: OwnerRentPointViewModel = viewModel()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        // Заголовок и адрес
        HeaderRentPoint(
            title = rentPoint.name,
            address = rentPoint.address,
            openingHours = stringResource(R.string.work_schedule)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Это экран для Owner")
    }
}