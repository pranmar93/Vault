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
import com.praneet.vault.data.entity.UserEntity
import com.praneet.vault.ui.components.ConfirmDeleteDialog
import com.praneet.vault.ui.components.EditDeleteActions
import com.praneet.vault.ui.components.SingleFieldDialog
import com.praneet.vault.viewmodel.manage.UsersViewModel

@Composable
fun UsersScreen(onUserClick: (Long) -> Unit) {
    val vm: UsersViewModel = hiltViewModel()

    val users by vm.users.collectAsState()
    var showAdd by remember { mutableStateOf(false) }
    var editingUser by remember { mutableStateOf<UserEntity?>(null) }
    var confirmDelete by remember { mutableStateOf<UserEntity?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAdd = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Add user")
            }
        }
    ) { padding ->
        if (users.isEmpty()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding), contentAlignment = Alignment.Center
            ) {
                Text("No user added", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                items(users, key = { it.id }) { user ->
                    androidx.compose.material3.ListItem(
                        headlineContent = { Text(user.name) },
                        trailingContent = {
                            EditDeleteActions(
                                onEdit = { editingUser = user },
                                onDelete = { confirmDelete = user })
                        },
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .then(Modifier)
                            .clickable { onUserClick(user.id) }
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
            title = "Add user",
            label = "User",
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
                if (ValidationUtils.existsBy({ it.name }, users, name, ignoreCase = false)) {
                    error = "User already exists"
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

    editingUser?.let { user ->
        var text by remember {
            mutableStateOf(
                TextFieldValue(
                    user.name,
                    selection = TextRange(0, user.name.length)
                )
            )
        }
        val focusRequester = remember { FocusRequester() }
        val keyboard = LocalSoftwareKeyboardController.current
        SingleFieldDialog(
            title = "Edit user",
            label = "User",
            value = text,
            isError = false,
            supportingText = null,
            modifier = Modifier.focusRequester(focusRequester),
            onValueChange = { text = it },
            onConfirm = {
                if (text.text.isNotBlank()) {
                    vm.update(user.copy(name = text.text.trim()))
                    editingUser = null
                }
            },
            onDismiss = { editingUser = null }
        )
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
            keyboard?.show()
        }
    }

    confirmDelete?.let { user ->
        ConfirmDeleteDialog(
            title = "Delete user?",
            message = "This will remove the user and all its data.",
            onConfirm = {
                vm.delete(user)
                confirmDelete = null
            },
            onDismiss = { confirmDelete = null }
        )
    }
}


