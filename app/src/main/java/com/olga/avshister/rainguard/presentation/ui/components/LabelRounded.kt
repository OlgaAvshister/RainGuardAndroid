package com.olga.avshister.rainguard.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabelRounded(
    modifier: Modifier = Modifier,
    text: String,
) {
    Box(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 28.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.5.sp
        )
    }
}