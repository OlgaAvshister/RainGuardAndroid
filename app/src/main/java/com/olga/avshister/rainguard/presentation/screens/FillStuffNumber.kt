package com.olga.avshister.rainguard.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.olga.avshister.rainguard.R
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.olga.avshister.rainguard.presentation.state.BSheetContentState
import com.olga.avshister.rainguard.presentation.ui.components.PrimaryButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FillStuffNumber(
    onNextState: (state: BSheetContentState) -> Unit,
) {
    var number by remember { mutableStateOf("") }

    Scaffold(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.toolbar_fill_stuff_number),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {/* navController.popBackStack() */}) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                PrimaryButton(
                    text = stringResource(R.string.next),
                    onClick = {
                        onNextState(BSheetContentState.RentTotalState)
                    }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = stringResource(R.string.stuff_number),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = number,
                onValueChange = { newValue ->
                    number = newValue
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(
                        color = Color(0xFFEDE7FF),
                        shape = RoundedCornerShape(28.dp)
                    ),
                shape = RoundedCornerShape(28.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                singleLine = true,
                placeholder = { // Добавляем placeholder для примера
                    Text("Введите номер")
                },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent,
                    unfocusedContainerColor = Color(0xFFEDE7FF),
                    focusedContainerColor = Color(0xFFEDE7FF)
                )
            )
        }
    }
}