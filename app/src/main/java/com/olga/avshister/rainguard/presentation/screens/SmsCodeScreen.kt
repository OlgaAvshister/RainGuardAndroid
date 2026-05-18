package com.olga.avshister.rainguard.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.olga.avshister.rainguard.R
import com.olga.avshister.rainguard.presentation.ui.theme.RainGuardTheme
import com.olga.avshister.rainguard.presentation.viewmodel.SmsCodeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

const val TIMER_LIMIT = 40

@Composable
fun SmsCodeScreen(
    navController: NavController,
    phoneNumber: String
) {

    val viewModel: SmsCodeViewModel = viewModel()
    val defaultInfoText = stringResource(R.string.sms_you_get_this_after_minute, phoneNumber)
    val digitsCount = 4

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()

    var secondsLeft by remember { mutableStateOf(TIMER_LIMIT) }
    var timerActive by remember { mutableStateOf(true) }
    var showResendButton by remember { mutableStateOf(false) }

    var isError by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf("") }
    var infoText by remember {
        mutableStateOf(defaultInfoText)
    }

    val codeDigits = remember {
        mutableStateListOf("", "", "", "")
    }

    val focusRequesters = remember {
        List(digitsCount) { FocusRequester() }
    }

    // Таймер
    LaunchedEffect(timerActive) {
        if (timerActive) {
            while (secondsLeft > 0) {
                delay(1000L)
                secondsLeft--
            }

            timerActive = false
            showResendButton = true
        }
    }

    // Стартовый фокус
    LaunchedEffect(Unit) {
        focusRequesters[0].requestFocus()
        keyboardController?.show()
    }

    // Actions
    LaunchedEffect(Unit) {
        viewModel.action.collect { action ->
            when (action) {
                is SmsCodeViewModel.Action.NavigateToScreen -> {
                    navController.navigate(action.screen) {
                        popUpTo(0) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }

                is SmsCodeViewModel.Action.ShowError -> {
                    isError = true
                    errorText = action.errorMessage
                    infoText = defaultInfoText

                    for (i in 0..< digitsCount) {
                        codeDigits[i] = ""
                    }
                    scope.launch {
                        focusRequesters[0].requestFocus()
                        keyboardController?.show()
                    }
                }
            }
        }
    }

    fun onDigitChange(index: Int, value: String) {

        val digit = value.filter { it.isDigit() }.take(1)

        // Удаление
        if (digit.isEmpty()) {
            codeDigits[index] = ""
            if (index > 0) {
                focusRequesters[index - 1].requestFocus()
            }
            return
        }

        // Ввод
        codeDigits[index] = digit

        isError = false

        if (index < digitsCount - 1 ) {
            scope.launch {
                delay(100)
                focusRequesters[index + 1].requestFocus()
                keyboardController?.show()
            }

        } else {

            focusManager.clearFocus()

            viewModel.onIntent(
                SmsCodeViewModel.Intent.Auth(
                    phone = phoneNumber,
                    code = codeDigits.joinToString("")
                )
            )
        }
    }

    fun resetTimer() {
        secondsLeft = TIMER_LIMIT
        timerActive = true
        showResendButton = false
        isError = false
        infoText = defaultInfoText

        for (i in 0..<digitsCount) {
            codeDigits[i] = ""
        }

        scope.launch {
            focusRequesters[0].requestFocus()
            keyboardController?.show()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .imePadding()
            .padding(12.dp)
    ) {

        // Back button
        Row(
            modifier = Modifier.align(Alignment.TopStart),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = {
                    navController.popBackStack()
                }
            ) {

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back)
                )
            }
        }

        // Content
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = if (isError) {
                    errorText
                } else {
                    stringResource(R.string.fill_sms_code)
                },
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = infoText,
                fontSize = 14.sp,
                color = colorResource(R.color.gray),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                horizontalArrangement = Arrangement.Center
            ) {

                for (i in 0..< digitsCount) {
                    val backgroundColor =
                        if (isError) {
                            Color.White
                        } else {
                            colorResource(R.color.sms_code_fill)
                        }

                    OutlinedTextField(
                        value = codeDigits[i],

                        onValueChange = {
                            onDigitChange(i, it)
                        },

                        singleLine = true,

                        textStyle = LocalTextStyle.current.copy(
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        ),

                        modifier = Modifier
                            .height(68.dp)
                            .width(54.dp)
                            .padding(2.dp)
                            .background(backgroundColor)
                            .focusRequester(focusRequesters[i])
                            .onPreviewKeyEvent { event ->
                                if (
                                    event.type == KeyEventType.KeyDown &&
                                    event.key == Key.Backspace
                                ) {

                                    // если текущее поле пустое — удаляем символ из предыдущего
                                    if (codeDigits[i].isEmpty() && i > 0) {

                                        codeDigits[i - 1] = ""

                                        scope.launch {
                                            delay(100)
                                            focusRequesters[i - 1].requestFocus()
                                        }

                                        true
                                    } else {
                                        false
                                    }

                                } else {
                                    false
                                }
                            },

                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = if (i == digitsCount - 1) {
                                ImeAction.Done
                            } else {
                                ImeAction.Next
                            }
                        )
                    )
                }
            }
        }

        // Bottom
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .imePadding()
                .padding(bottom = 24.dp)
        ) {
            if (timerActive) {
                Text(
                    text = stringResource(R.string.sms_resend_after_n_seconds, secondsLeft),
                    fontSize = 14.sp,
                    color = colorResource(R.color.gray),
                    textAlign = TextAlign.Center
                )

            } else if (showResendButton) {
                Button(
                    onClick = {
                        resetTimer()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.violet),
                        contentColor = Color.White
                    )
                ) {
                    Text(stringResource(R.string.sms_send_again))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SmsCodeScreenPreview() {
    RainGuardTheme {
        SmsCodeScreen(
            navController = rememberNavController(),
            phoneNumber = "9000000000"
        )
    }
}