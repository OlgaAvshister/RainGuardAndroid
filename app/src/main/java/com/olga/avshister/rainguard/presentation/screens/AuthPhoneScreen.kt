package com.olga.avshister.rainguard.presentation.screens

import android.R.attr.maxWidth
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.olga.avshister.rainguard.R
import com.olga.avshister.rainguard.presentation.ui.components.PrimaryButton

@Composable
fun AuthPhoneScreen(navController: NavHostController) {
    var phoneNumber by remember { mutableStateOf("") }

    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .imePadding()
            .padding(horizontal = 32.dp)
    ) {

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.phone_number),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.consent_personal_data),
                fontSize = 14.sp,
                color = colorResource(R.color.gray),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            PhoneNumberInput(
                phoneNumber = phoneNumber,
                onNumberChange = { phoneNumber = it },
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 16.dp)
        ) {
            PrimaryButton(
                text = stringResource(R.string.get_sms_code),
                modifier = Modifier.align(Alignment.BottomCenter),
                onClick = {
                    navController.navigate("SMS_CODE_SCREEN/$phoneNumber")
                }
            )
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }
}

@Composable
fun PhoneNumberInput(
    phoneNumber: String,
    onNumberChange: (String) -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    val textStyle = TextStyle(
        fontSize = 22.sp,
        fontWeight = FontWeight.Medium,
        color = Color.Black
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {

        // +7
        Text(
            text = "+7",
            style = textStyle,
            modifier = Modifier.padding(end = 12.dp)
        )

        BoxWithConstraints(
            modifier = Modifier.weight(1f)
        ) {

            // скрытый input
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = {
                    onNumberChange(it.filter(Char::isDigit).take(10))
                },
                modifier = Modifier
                    .size(1.dp)
                    .focusRequester(focusRequester),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = Color.Transparent
                )
            )

            val groups = listOf(3, 3, 2, 2)
            val totalDigits = groups.sum()

            val groupSpacing = 12.dp
            val innerSpacing = 4.dp

            val totalGroupGaps = (groups.size - 1) * groupSpacing
            val totalInnerGaps = groups.sumOf { it - 1 } * innerSpacing

            val digitSize =
                (maxWidth - totalGroupGaps - totalInnerGaps) / totalDigits

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                var index = 0

                groups.forEachIndexed { groupIndex, groupSize ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        repeat(groupSize) {
                            val digit = phoneNumber.getOrNull(index)

                            Box(
                                modifier = Modifier.size(digitSize),
                                contentAlignment = Alignment.Center
                            ) {
                                if (digit != null) {
                                    Text(
                                        text = digit.toString(),
                                        style = textStyle
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(max(digitSize * 0.35f, 6.dp))
                                            .background(
                                                color = colorResource(R.color.gray),
                                                shape = CircleShape
                                            )
                                    )
                                }
                            }
                            index++
                        }
                    }

                    if (groupIndex != groups.lastIndex) {
                        Spacer(modifier = Modifier.width(12.dp))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AuthPhonePreview() {
    AuthPhoneScreen(rememberNavController())
}