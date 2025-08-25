package com.praneet.vault.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ConfirmDeleteDialog(title: String, message: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = { TextButton(onClick = onConfirm) { Text("Yes") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("No") } }
    )
}

@Composable
fun SingleFieldDialog(
    title: String,
    label: String,
    value: androidx.compose.ui.text.input.TextFieldValue,
    isError: Boolean,
    supportingText: String?,
    modifier: Modifier = Modifier,
    onValueChange: (androidx.compose.ui.text.input.TextFieldValue) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                isError = isError,
                label = { Text(label) },
                supportingText = { if (supportingText != null) Text(supportingText) },
                modifier = modifier
            )
        },
        confirmButton = { TextButton(onClick = onConfirm) { Text("Save") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun TwoFieldDialog(
    title: String,
    keyValue: androidx.compose.ui.text.input.TextFieldValue,
    valueValue: androidx.compose.ui.text.input.TextFieldValue,
    keyError: String?,
    valueError: String?,
    keyModifier: Modifier = Modifier,
    onKeyChange: (androidx.compose.ui.text.input.TextFieldValue) -> Unit,
    onValueChange: (androidx.compose.ui.text.input.TextFieldValue) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                OutlinedTextField(value = keyValue, onValueChange = onKeyChange, singleLine = true, isError = keyError != null, label = { Text("Key") }, supportingText = { if (keyError != null) Text(keyError) }, modifier = keyModifier)
                OutlinedTextField(value = valueValue, onValueChange = onValueChange, isError = valueError != null, label = { Text("Value") }, supportingText = { if (valueError != null) Text(valueError) })
            }
        },
        confirmButton = { TextButton(onClick = onConfirm) { Text("Save") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}


