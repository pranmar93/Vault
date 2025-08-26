package com.praneet.vault.ui.manage

import android.app.Application
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
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
import androidx.compose.material3.ListItem
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
import com.praneet.vault.data.AccountTypeEntity
import com.praneet.vault.data.VaultRepository
import com.praneet.vault.common.utils.ValidationUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountTypesViewModel @Inject constructor(app: Application, private val repo: VaultRepository, private val savedStateHandle: androidx.lifecycle.SavedStateHandle) : AndroidViewModel(app) {
    private val userId: Long = savedStateHandle.get<Long>("userId") ?: 0L
    val types: StateFlow<List<AccountTypeEntity>> = repo.observeAccountTypes(userId)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun add(name: String) = viewModelScope.launch { repo.addAccountType(userId, name) }
    fun update(type: AccountTypeEntity) = viewModelScope.launch { repo.updateAccountType(type) }
    fun delete(type: AccountTypeEntity) = viewModelScope.launch { repo.deleteAccountType(type) }
}

@Composable
fun AccountTypesScreen(userId: Long, onAccountTypeClick: (Long) -> Unit) {
    val vm: AccountTypesViewModel = androidx.hilt.navigation.compose.hiltViewModel()

    val types by vm.types.collectAsState()
    var showAdd by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<AccountTypeEntity?>(null) }
    var confirmDelete by remember { mutableStateOf<AccountTypeEntity?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAdd = true }) { Icon(Icons.Filled.Add, contentDescription = null) }
        }
    ) { padding ->
        if (types.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No account type added", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(padding)) {
                items(types, key = { it.id }) { type ->
                    ListItem(
                        headlineContent = { Text(type.name) },
                        trailingContent = {
                            EditDeleteActions(onEdit = { editing = type }, onDelete = { confirmDelete = type })
                        },
                        modifier = Modifier.padding(horizontal = 8.dp).clickable { onAccountTypeClick(type.id) }
                    )
                    Divider()
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
                if (ValidationUtils.isBlank(name)) { error = "Field cannot be blank"; return@SingleFieldDialog }
                if (ValidationUtils.existsBy({ it.name }, types, name)) {
                    error = "Type of account already exists"
                } else {
                    vm.add(name); showAdd = false
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
        var text by remember { mutableStateOf(TextFieldValue(type.name, selection = androidx.compose.ui.text.TextRange(0, type.name.length))) }
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
            onConfirm = { if (text.text.isNotBlank()) { vm.update(type.copy(name = text.text.trim())); editing = null } },
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
            onConfirm = { vm.delete(type); confirmDelete = null },
            onDismiss = { confirmDelete = null }
        )
    }
}


