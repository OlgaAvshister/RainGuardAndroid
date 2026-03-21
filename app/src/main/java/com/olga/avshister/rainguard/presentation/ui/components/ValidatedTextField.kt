package com.olga.avshister.rainguard.presentation.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ValidatedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    validator: ((String) -> ResultValidation)? = null,
    isEnabled: Boolean = true
) {
    var isFocused by remember { mutableStateOf(false) }

    val validationResult = remember(value) {
        validator?.invoke(value) ?: ResultValidation.Valid
    }

    val state = when {
        !isEnabled -> FieldState.Disabled
        validationResult is ResultValidation.Error -> FieldState.Error
        value.isNotEmpty() && validator != null -> FieldState.Success
        isFocused -> FieldState.Focused
        else -> FieldState.Default
    }

    val borderColor by animateColorAsState(
        targetValue = when (state) {
            FieldState.Default -> Color.Transparent
            FieldState.Focused -> Color(0xFF6C3BFF)
            FieldState.Success -> Color(0xFF00C853)
            FieldState.Error -> Color(0xFFFF5252)
            FieldState.Disabled -> Color.Transparent
        }
    )

    val backgroundColor = Color(0xFFF2F2F2)

    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = when (state) {
                FieldState.Error -> Color(0xFFFF5252)
                FieldState.Focused -> Color(0xFF6C3BFF)
                else -> Color.Gray
            },
            modifier = Modifier.padding(start = 16.dp, bottom = 6.dp)
        )

        // INPUT
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(backgroundColor)
                .border(
                    width = if (state == FieldState.Default) 0.dp else 2.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(28.dp)
                )
                .onFocusChanged { isFocused = it.isFocused }
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                enabled = isEnabled,
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    color = Color.Black
                ),
                cursorBrush = SolidColor(Color.Black),
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (validationResult is ResultValidation.Error) {
            Text(
                text = validationResult.message,
                color = Color(0xFFFF5252),
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}


sealed class ResultValidation {
    object Valid : ResultValidation()
    data class Error(val message: String) : ResultValidation()
}

private enum class FieldState {
    Default,
    Focused,
    Success,
    Error,
    Disabled
}

