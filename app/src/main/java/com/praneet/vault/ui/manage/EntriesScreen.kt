package com.praneet.vault.ui.manage

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.praneet.vault.common.utils.ValidationUtils
import com.praneet.vault.data.entity.EntryEntity
import com.praneet.vault.ui.components.ConfirmDeleteDialog
import com.praneet.vault.ui.components.EditDeleteActions
import com.praneet.vault.ui.components.TwoFieldDialog
import com.praneet.vault.viewmodel.manage.EntriesViewModel

@Composable
fun EntriesScreen() {
    val vm: EntriesViewModel = hiltViewModel()

    val entries by vm.entries.collectAsState()
    var showAdd by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<EntryEntity?>(null) }
    var confirmDelete by remember { mutableStateOf<EntryEntity?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                showAdd = true
            }) { Icon(Icons.Filled.Add, contentDescription = null) }
        }
    ) { padding ->
        if (entries.isEmpty()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding), contentAlignment = Alignment.Center
            ) {
                Text("No keys added", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                items(entries, key = { it.id }) { entry ->
                    ListItem(
                        headlineContent = { Text(entry.key) },
                        supportingContent = { Text(entry.value) },
                        trailingContent = {
                            EditDeleteActions(
                                onEdit = { editing = entry },
                                onDelete = { confirmDelete = entry })
                        }
                    )
                    HorizontalDivider()
                }
            }
        }
    }

    if (showAdd) {
        var key by remember { mutableStateOf(TextFieldValue("")) }
        var value by remember { mutableStateOf(TextFieldValue("")) }
        var keyError by remember { mutableStateOf<String?>(null) }
        var valueError by remember { mutableStateOf<String?>(null) }
        val focusRequester = remember { FocusRequester() }
        val keyboard = LocalSoftwareKeyboardController.current
        TwoFieldDialog(
            title = "Add key/value",
            keyValue = key,
            valueValue = value,
            keyError = keyError,
            valueError = valueError,
            modifier = Modifier.focusRequester(focusRequester),
            onKeyChange = {
                key = it
                if (keyError != null) keyError = null
            },
            onValueChange = {
                value = it
                if (valueError != null) valueError = null
            },
            onConfirm = {
                val k = key.text.trim()
                val v = value.text
                var hasError = false
                if (ValidationUtils.isBlank(k)) {
                    keyError = "Field cannot be blank"
                    hasError = true
                }
                if (ValidationUtils.isBlank(v)) {
                    valueError = "Field cannot be blank"
                    hasError = true
                }
                if (hasError) return@TwoFieldDialog
                if (ValidationUtils.existsBy({ it.key }, entries, k)) {
                    keyError = "Key already exists"
                } else {
                    vm.add(k, v)
                    showAdd = false
                }
            },
            onDismiss = { showAdd = false }
        )
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
            keyboard?.show()
        }
    }

    editing?.let { entry ->
        var key by remember {
            mutableStateOf(
                TextFieldValue(
                    entry.key,
                    selection = TextRange(0, entry.key.length)
                )
            )
        }
        var value by remember {
            mutableStateOf(
                TextFieldValue(
                    entry.value,
                    selection = TextRange(0, entry.value.length)
                )
            )
        }
        val focusRequester = remember { FocusRequester() }
        val keyboard = LocalSoftwareKeyboardController.current
        TwoFieldDialog(
            title = "Edit entry",
            keyValue = key,
            valueValue = value,
            keyError = null,
            valueError = null,
            modifier = Modifier.focusRequester(focusRequester),
            onKeyChange = { key = it },
            onValueChange = { value = it },
            onConfirm = {
                if (key.text.isNotBlank()) {
                    vm.update(entry.copy(key = key.text.trim(), value = value.text))
                    editing = null
                }
            },
            onDismiss = { editing = null }
        )
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
            keyboard?.show()
        }
    }

    confirmDelete?.let { entry ->
        ConfirmDeleteDialog(
            title = "Delete entry?",
            message = "This will remove the key/value.",
            onConfirm = {
                vm.delete(entry)
                confirmDelete = null
            },
            onDismiss = { confirmDelete = null }
        )
    }
}


