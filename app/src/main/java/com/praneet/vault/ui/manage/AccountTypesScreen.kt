package com.praneet.vault.ui.manage

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.praneet.vault.common.utils.ValidationUtils
import com.praneet.vault.data.entity.AccountTypeEntity
import com.praneet.vault.ui.components.ConfirmDeleteDialog
import com.praneet.vault.ui.components.EditDeleteActions
import com.praneet.vault.ui.components.SingleFieldDialog
import com.praneet.vault.viewmodel.manage.AccountTypesViewModel

@Composable
fun AccountTypesScreen(onAccountTypeClick: (Long) -> Unit) {
    val vm: AccountTypesViewModel = hiltViewModel()

    val types by vm.types.collectAsState()
    var showAdd by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<AccountTypeEntity?>(null) }
    var confirmDelete by remember { mutableStateOf<AccountTypeEntity?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAdd = true }) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = null
                )
            }
        }
    ) { padding ->
        if (types.isEmpty()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding), contentAlignment = Alignment.Center
            ) {
                Text("No account type added", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                items(types, key = { it.id }) { type ->
                    ListItem(
                        headlineContent = { Text(type.name) },
                        trailingContent = {
                            EditDeleteActions(
                                onEdit = { editing = type },
                                onDelete = { confirmDelete = type })
                        },
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .clickable { onAccountTypeClick(type.id) }
                    )
                    HorizontalDivider()
                }
            }
        }
    }

    if (showAdd) {
        var text by remember { mutableStateOf(TextFieldValue("")) }
        var error by remember { mutableStateOf<String?>(null) }
        val focusRequester = remember { FocusRequester() }
        val keyboard = LocalSoftwareKeyboardController.current
        SingleFieldDialog(
            title = "Add account type",
            label = "Type",
            value = text,
            isError = error != null,
            supportingText = error,
            modifier = Modifier.focusRequester(focusRequester),
            onValueChange = { text = it },
            onConfirm = {
                val name = text.text.trim()
                if (ValidationUtils.isBlank(name)) {
                    error = "Field cannot be blank"
                    return@SingleFieldDialog
                }
                if (ValidationUtils.existsBy({ it.name }, types, name)) {
                    error = "Type of account already exists"
                } else {
                    vm.add(name)
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

    editing?.let { type ->
        var text by remember {
            mutableStateOf(
                TextFieldValue(
                    type.name,
                    selection = TextRange(0, type.name.length)
                )
            )
        }
        val focusRequester = remember { FocusRequester() }
        val keyboard = LocalSoftwareKeyboardController.current
        SingleFieldDialog(
            title = "Edit account type",
            label = "Type",
            value = text,
            isError = false,
            supportingText = null,
            modifier = Modifier.focusRequester(focusRequester),
            onValueChange = { text = it },
            onConfirm = {
                if (text.text.isNotBlank()) {
                    vm.update(type.copy(name = text.text.trim()))
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

    confirmDelete?.let { type ->
        ConfirmDeleteDialog(
            title = "Delete account type?",
            message = "This will remove the type and its entries.",
            onConfirm = {
                vm.delete(type)
                confirmDelete = null
            },
            onDismiss = { confirmDelete = null }
        )
    }
}


