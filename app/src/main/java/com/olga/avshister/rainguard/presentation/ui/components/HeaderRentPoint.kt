package com.olga.avshister.rainguard.presentation.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HeaderRentPoint(
    title: String?,
    address: String?,
    openingHours: String,
) {
    Column {
        Text(
            text = title.orEmpty(),
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = address.orEmpty(),
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = openingHours,
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}