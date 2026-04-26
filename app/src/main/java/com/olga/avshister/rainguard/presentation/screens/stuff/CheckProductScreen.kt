package com.olga.avshister.rainguard.presentation.screens.stuff

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.olga.avshister.rainguard.R
import com.olga.avshister.rainguard.domain.products.Product
import com.olga.avshister.rainguard.presentation.ui.components.PrimaryButton
import com.olga.avshister.rainguard.presentation.ui.components.SecondaryButton
import com.olga.avshister.rainguard.presentation.ui.theme.RainGuardTheme
import com.olga.avshister.rainguard.presentation.viewmodel.stuff.CheckProductViewModel
import com.olga.avshister.rainguard.presentation.viewmodel.stuff.CheckProductViewModel.CheckProductState
import com.olga.avshister.rainguard.presentation.viewmodel.stuff.CheckProductViewModel.Intent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckProductScreen(navController: NavHostController) {
    val context = LocalContext.current
    val viewModel: CheckProductViewModel = viewModel()
    val currentScreenState  = viewModel.screenState.collectAsState()

    val onClickNext: () -> Unit = {
        viewModel.onIntent(Intent.OnNextButtonClicked)
    }

    val onClickLogout: () -> Unit = {
        viewModel.onIntent(Intent.OnLogoutClick)
    }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is CheckProductViewModel.Event.NavigateToScreen -> {
                    navController.navigate(event.route) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
                CheckProductViewModel.Event.NavigateBack -> {
                    navController.popBackStack()
                }
                is CheckProductViewModel.Event.Error -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = currentScreenState.value.checkProductState.titleText,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
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
                Column {
                    PrimaryButton(
                        text = currentScreenState.value.checkProductState.bottomButtonText,
                        onClick = { onClickNext() }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    PrimaryButton(
                        text = stringResource(R.string.logout),
                        onClick = { onClickLogout() }
                    )
                }
            }
        }
    ) { paddingValues ->
        when (val state = currentScreenState.value.checkProductState) {
            is CheckProductState.FillProductId -> {
                FillIdContent(
                    pd = paddingValues,
                    state = state,
                    onIdFilled = {
                        viewModel.onIntent(Intent.OnIdUpdated(it))
                    }
                )
            }

            is CheckProductState.SetProductCondition -> {
                CheckProductCondition(paddingValues, state.checkedProductCondition) { checkedCondition ->
                    viewModel.onIntent(Intent.OnConditionSelected(id = state.id, checkedCondition))
                }
            }
        }
    }
}

@Composable
fun FillIdContent(pd: PaddingValues, state: CheckProductState.FillProductId, onIdFilled: (id: String) -> Unit) {
    Column(
        modifier =
            Modifier
                .padding(pd)
                .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Spacer(modifier = Modifier.height(108.dp))

        Text(
            text = stringResource(R.string.product_id_label_single),
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = state.id,
            onValueChange = { newValue ->
                onIdFilled(newValue)
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

@Composable
fun CheckProductCondition(
    paddingValues: PaddingValues,
    currentCondition: Product.ProductCondition,
    onClick: (condition: Product.ProductCondition) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            itemsIndexed(Product.ProductCondition.entries) { index, value ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    SecondaryButton(
                        text = value.valueStuff,
                        isChecked = value == currentCondition
                    ) {
                        onClick(value)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CheckProductScreenPreview() {
    RainGuardTheme {
        CheckProductScreen(rememberNavController())
    }
}