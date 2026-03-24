package com.olga.avshister.rainguard.presentation.screens.owner

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.olga.avshister.rainguard.R
import com.olga.avshister.rainguard.presentation.ui.components.ValidatedTextField

@Composable
fun AddStuffSection(
    onAddStuff: (name: String, phone: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.owner_add_stuff_label),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            ValidatedTextField(
                value = name,
                onValueChange = { name = it },
                label = stringResource(R.string.owner_add_stuff_name),
                validator = { validateInput(it) }
            )

            ValidatedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = stringResource(R.string.owner_add_stuff_phone),
                validator = { validateInput(it) }
            )

            Button(
                onClick = {
                    if (name.isNotBlank() && phone.isNotBlank()) {
                        onAddStuff(name, phone)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                enabled = name.isNotBlank() && phone.isNotBlank()
            ) {
                Text(stringResource(R.string.save_changes))
            }
        }
    }
}