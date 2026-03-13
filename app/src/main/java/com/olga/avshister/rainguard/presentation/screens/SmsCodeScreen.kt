package com.olga.avshister.rainguard.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
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
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
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
import com.olga.avshister.rainguard.data.common.PrefsRepositoryImpl
import com.olga.avshister.rainguard.data.profile.AuthLocalRepository
import com.olga.avshister.rainguard.data.profile.AuthRepository
import com.olga.avshister.rainguard.domain.profile.Role
import com.olga.avshister.rainguard.presentation.core.MAP_SCREEN
import com.olga.avshister.rainguard.presentation.core.SELECT_PRODUCT_TO_CHECK_SCREEN
import com.olga.avshister.rainguard.presentation.ui.theme.RainGuardTheme
import com.olga.avshister.rainguard.presentation.viewmodel.SmsCodeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

const val TIMER_LIMIT = 40

@Composable
fun SmsCodeScreen(navController: NavController, phoneNumber: String) {
    val context = LocalContext.current
    val viewModel: SmsCodeViewModel = viewModel()

    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()

    var secondsLeft by remember { mutableStateOf(TIMER_LIMIT) }
    var timerActive by remember { mutableStateOf(true) }
    var showResendButton by remember { mutableStateOf(false) }

    // Статус ошибки
    var isError by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf("") }
    var infoText by remember { mutableStateOf("Код придет в течение минуты\nна номер +7${phoneNumber}") }

    // Вводимые цифры
    val codeDigits = remember { mutableStateListOf("", "", "", "") }

    // Фокусные запросы для перехода
    val focusRequesters = List(4) { androidx.compose.ui.focus.FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

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

    // Начальное фокусирование и показ клавиатуры
    LaunchedEffect(Unit) {
        focusRequesters[0].requestFocus()
        keyboardController?.show()
    }

    // Проверка правильности кода
    fun auth() {
        val code = codeDigits.joinToString("")
        runCatching {
            viewModel.auth(phoneNumber, code)
        }.getOrNull()?.let { profile ->
            when (profile.role) {
                Role.CUSTOMER -> {
                    navController.navigate(MAP_SCREEN)
                }
                Role.STUFF -> {
                    navController.navigate(SELECT_PRODUCT_TO_CHECK_SCREEN)
                }
                Role.OWNER -> {
                    navController.navigate(MAP_SCREEN)
                }
            }

        } ?: run {
            // Неправильный код
            isError = true
            errorText = context.getString(R.string.invalid_sms_code)
            infoText = context.getString(R.string.sms_try_again_msg)
            // очистить поля
            for (i in 0..3) {
                codeDigits[i] = ""
            }
            // вернуть фокус на первое поле
            scope.launch {
                focusRequesters[0].requestFocus()
            }
        }
    }

    // Обработка изменения цифры
    fun onDigitChange(index: Int, value: String) {
        val digit = value.filter { it.isDigit() }.take(1)
        if (digit.isNotEmpty()) {
            codeDigits[index] = digit
            if (index < 3) {
                focusRequesters[index + 1].requestFocus()
            } else {
                focusManager.clearFocus()
                auth()
            }
        } else {
            // если удалена цифра, оставляем пустой
            codeDigits[index] = ""
        }
    }

    // Обработка клика по полю для сброса цифр
    fun onFieldClick(index: Int) {
        // Очистить это поле
        codeDigits[index] = ""
        // установить фокус на это поле
        scope.launch {
            focusRequesters[index].requestFocus()
        }
    }

    // Обработка нажатия на кнопку "отправить повторно"
    fun resetTimer() {
        secondsLeft = TIMER_LIMIT
        timerActive = true
        showResendButton = false
        isError = false
        infoText = "Код придет в течение минуты\nна номер +7${phoneNumber}"
        for (i in 0..3) {
            codeDigits[i] = ""
        }
        scope.launch {
            focusRequesters[0].requestFocus()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .imePadding()
            .padding(12.dp)
    ) {

        Row(
            modifier = Modifier.align(Alignment.TopStart),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
            }
        }

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = if (isError) errorText else "Введите код из SMS",
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
                for (i in 0..3) {
                    val backgroundColor =
                        if (isError) Color.White else colorResource(R.color.sms_code_fill)

                    OutlinedTextField(
                        value = codeDigits[i],
                        onValueChange = { onDigitChange(i, it) },
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
                            .clickable { onFieldClick(i) }
                            .focusable(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        )
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .imePadding()
                .padding(bottom = 24.dp)
        ) {
            if (timerActive) {
                Text(
                    text = "Отправить код повторно через $secondsLeft секунд",
                    fontSize = 14.sp,
                    color = colorResource(R.color.gray),
                    textAlign = TextAlign.Center
                )
            } else if (showResendButton) {
                Button(
                    onClick = { resetTimer() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.violet),
                        contentColor = Color.White
                    )
                ) {
                    Text("Отправить повторно")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SmsCodeScreenPreview() {
    RainGuardTheme {
        SmsCodeScreen(rememberNavController(), "9000000000")
    }
}