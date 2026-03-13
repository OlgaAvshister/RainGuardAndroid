package com.olga.avshister.rainguard.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.olga.avshister.rainguard.R

@Composable
fun SuccessScreen(
    message: String,
    buttonText: String,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Icon(
            painter = painterResource(id = R.drawable.ic_card_added),
            contentDescription = null,
            modifier = Modifier.size(96.dp),
            tint = Color(0xFF00C853)
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = message,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = { onClick() },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(buttonText)
        }
    }
}