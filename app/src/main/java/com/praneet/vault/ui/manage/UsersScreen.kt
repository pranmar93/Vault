package com.praneet.vault.ui.manage

import android.app.Application
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import com.praneet.vault.ui.components.EditDeleteActions
import com.praneet.vault.ui.components.SingleFieldDialog
import com.praneet.vault.ui.components.ConfirmDeleteDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import com.praneet.vault.data.UserEntity
import com.praneet.vault.data.VaultRepository
import com.praneet.vault.common.utils.ValidationUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UsersViewModel @Inject constructor(app: Application, private val repo: VaultRepository) : AndroidViewModel(app) {
    val users: StateFlow<List<UserEntity>> = repo.observeUsers()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun add(name: String) = viewModelScope.launch { repo.addUser(name) }
    fun update(user: UserEntity) = viewModelScope.launch { repo.updateUser(user) }
    fun delete(user: UserEntity) = viewModelScope.launch { repo.deleteUser(user) }
}

@Composable
fun UsersScreen(onUserClick: (Long) -> Unit, vm: UsersViewModel = androidx.hilt.navigation.compose.hiltViewModel()) {
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
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No user added", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(padding)) {
                items(users, key = { it.id }) { user ->
                    androidx.compose.material3.ListItem(
                        headlineContent = { Text(user.name) },
                        trailingContent = {
                            EditDeleteActions(onEdit = { editingUser = user }, onDelete = { confirmDelete = user })
                        },
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .then(Modifier)
                            .clickable { onUserClick(user.id) }
                    )
                    androidx.compose.material3.Divider()
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
                if (ValidationUtils.isBlank(name)) { error = "Field cannot be blank"; return@SingleFieldDialog }
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
        var text by remember { mutableStateOf(TextFieldValue(user.name, selection = androidx.compose.ui.text.TextRange(0, user.name.length))) }
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
            onConfirm = { if (text.text.isNotBlank()) { vm.update(user.copy(name = text.text.trim())); editingUser = null } },
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
            onConfirm = { vm.delete(user); confirmDelete = null },
            onDismiss = { confirmDelete = null }
        )
    }
}


