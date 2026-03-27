package com.olga.avshister.rainguard.presentation.screens.owner

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.olga.avshister.rainguard.R
import com.olga.avshister.rainguard.presentation.ui.components.PrimaryButton
import com.olga.avshister.rainguard.presentation.ui.components.ValidationResult
import com.olga.avshister.rainguard.presentation.ui.components.ValidatedTextField
import com.olga.avshister.rainguard.presentation.viewmodel.owner.AddRentPointViewModel
import com.olga.avshister.rainguard.presentation.viewmodel.owner.AddRentPointViewModel.AddRentPointIntent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRentPointScreen(navController: NavController) {
    val viewModel: AddRentPointViewModel = viewModel()
    val inputState = viewModel.inputState.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    Scaffold(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.add_rent_point_toolbar_title),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
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
                    text = stringResource(R.string.save_changes),
                    onClick = {
                        viewModel.onIntent(AddRentPointIntent.SaveRentPoint)
                    }
                )
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { paddingValues ->

        LaunchedEffect(Unit) {
            viewModel.events.collect { event ->
                when (event) {
                    is AddRentPointViewModel.Event.Close -> {
                        navController.popBackStack()
                    }
                    is AddRentPointViewModel.Event.Error -> {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                message = event.message,
                                duration = SnackbarDuration.Short
                            )
                        }
                    }
                    else -> {}
                }
            }
        }

        if (inputState.value.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
        ) {
            ValidatedTextField(
                value = inputState.value.name,
                onValueChange = {
                    viewModel.onIntent(AddRentPointIntent.OnRentPointNameChanged(it))
                },
                label = stringResource(R.string.add_rent_point_name),
                validator = { validateInput(it) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Адрес
            ValidatedTextField(
                value = inputState.value.address,
                onValueChange = {
                    viewModel.onIntent(AddRentPointIntent.OnRentPointAddressChanged(it))
                },
                label = stringResource(R.string.add_rent_point_address),
                validator = { validateInput(it) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Время работы
            ValidatedTextField(
                value = inputState.value.workHours,
                onValueChange = {
                    viewModel.onIntent(AddRentPointIntent.OnRentPointWorkHoursChanged(it))
                },
                label = stringResource(R.string.add_rent_work_hours),
                validator = { validateInput(it) }
            )
        }
    }
}

fun validateInput(text: String): ValidationResult {
    return when {
        text.isEmpty() -> ValidationResult.Error("Поле не может быть пустым")
        text.length < 3 -> ValidationResult.Error("Минимум 3 символа")
        else -> ValidationResult.Valid
    }
}