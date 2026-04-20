package com.olga.avshister.rainguard.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.olga.avshister.rainguard.R
import com.olga.avshister.rainguard.presentation.state.BSheetContentState
import com.olga.avshister.rainguard.presentation.viewmodel.SelectIdsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectIdsScreen(
    openToTake: Boolean, // открываем экран для аренды/возврата
    rentPointId: Long,
    onNextState: (state: BSheetContentState) -> Unit,
) {
    val context = LocalContext.current

    val viewModelKey = remember {
        "SelectIdsScreen${System.currentTimeMillis()}"
    }

    val viewModel: SelectIdsViewModel = viewModel(
        key = viewModelKey,
        factory = remember(context, openToTake) {
            SelectIdsViewModel.SelectIdsViewModelFactory(context, openToTake, rentPointId)
        }
    )

    val state by viewModel.state.collectAsStateWithLifecycle()

    if (state.isLoading) {
        // Показываем заглушку пока параметры не загружены
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    LaunchedEffect(Unit) {
        viewModel.action.collect { action ->
            when (action) {
                is SelectIdsViewModel.Action.OnNextState -> {
                    onNextState(action.state)
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.fill_ids),
                        fontSize = 20.sp,
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
                Button(
                    onClick = {
                        if (openToTake) {
                            // todo: вот здесь может проблема с асинхронностью возникнуть, нужно вернуться
                            viewModel.onIntent(SelectIdsViewModel.Intent.ToCheckout)
                            //onNextState(BSheetContentState.CheckoutState)
                        } else {
                            viewModel.onIntent(SelectIdsViewModel.Intent.GiveToCheck)
                            //onNextState(BSheetContentState.GiveToCheckState)
                        }
                    },
                    shape = RoundedCornerShape(28.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text(text = stringResource(R.string.next), fontSize = 18.sp)
                }
            }
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            itemsIndexed(state.suggestedIds) { index, value ->
                Column {
                    Text(
                        text = stringResource(R.string.product_id_label, index + 1),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = value.toString(),
                        onValueChange = { newValue ->
                            viewModel.onIntent(SelectIdsViewModel.Intent.UpdateValue(index, newValue.toLong()))
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
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = Color.Transparent
                        )
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun SelectIdsScreenPreview() {
    SelectIdsScreen(openToTake = true, rentPointId = 1, onNextState = {})
}