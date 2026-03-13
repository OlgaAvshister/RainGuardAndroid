package com.olga.avshister.rainguard.presentation.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PrimaryButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Button(
        modifier = modifier
            .width(224.dp)
            .height(56.dp),
        onClick = {
            onClick.invoke()
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        )
    ) {
        Text(text = text)
    }
}

@Composable
fun SecondaryButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Button(
        modifier = modifier
            .width(224.dp)
            .height(56.dp),
        onClick = {
            onClick.invoke()
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary,
        )
    ) {
        Text(
            text = text,
        )
    }
}




