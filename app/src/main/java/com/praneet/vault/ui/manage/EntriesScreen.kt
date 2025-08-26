package com.praneet.vault.ui.manage

import android.app.Application
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
import com.praneet.vault.ui.components.TwoFieldDialog
import com.praneet.vault.ui.components.ConfirmDeleteDialog
import androidx.compose.material3.ListItem
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
import com.praneet.vault.data.EntryEntity
import com.praneet.vault.data.VaultRepository
import com.praneet.vault.common.utils.ValidationUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EntriesViewModel @Inject constructor(app: Application, private val repo: VaultRepository, private val savedStateHandle: androidx.lifecycle.SavedStateHandle) : AndroidViewModel(app) {
    private val accountTypeId: Long = savedStateHandle.get<Long>("accountTypeId") ?: 0L
    val entries: StateFlow<List<EntryEntity>> = repo.observeEntries(accountTypeId)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun add(key: String, value: String) = viewModelScope.launch { repo.addEntry(accountTypeId, key, value) }
    fun update(entry: EntryEntity) = viewModelScope.launch { repo.updateEntry(entry) }
    fun delete(entry: EntryEntity) = viewModelScope.launch { repo.deleteEntry(entry) }
}

@Composable
fun EntriesScreen(accountTypeId: Long) {
    val vm: EntriesViewModel = androidx.hilt.navigation.compose.hiltViewModel()

    val entries by vm.entries.collectAsState()
    var showAdd by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<EntryEntity?>(null) }
    var confirmDelete by remember { mutableStateOf<EntryEntity?>(null) }

    Scaffold(
        floatingActionButton = { FloatingActionButton(onClick = { showAdd = true }) { Icon(Icons.Filled.Add, contentDescription = null) } }
    ) { padding ->
        if (entries.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No keys added", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(padding)) {
                items(entries, key = { it.id }) { entry ->
                    ListItem(
                        headlineContent = { Text(entry.key) },
                        supportingContent = { Text(entry.value) },
                        trailingContent = {
                            EditDeleteActions(onEdit = { editing = entry }, onDelete = { confirmDelete = entry })
                        }
                    )
                    Divider()
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
            keyModifier = Modifier.focusRequester(focusRequester),
            onKeyChange = { key = it; if (keyError != null) keyError = null },
            onValueChange = { value = it; if (valueError != null) valueError = null },
            onConfirm = {
                val k = key.text.trim()
                val v = value.text
                var hasError = false
                if (ValidationUtils.isBlank(k)) { keyError = "Field cannot be blank"; hasError = true }
                if (ValidationUtils.isBlank(v)) { valueError = "Field cannot be blank"; hasError = true }
                if (hasError) return@TwoFieldDialog
                if (ValidationUtils.existsBy({ it.key }, entries, k)) {
                    keyError = "Key already exists"
                } else {
                    vm.add(k, v); showAdd = false
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
        var key by remember { mutableStateOf(TextFieldValue(entry.key, selection = androidx.compose.ui.text.TextRange(0, entry.key.length))) }
        var value by remember { mutableStateOf(TextFieldValue(entry.value, selection = androidx.compose.ui.text.TextRange(0, entry.value.length))) }
        val focusRequester = remember { FocusRequester() }
        val keyboard = LocalSoftwareKeyboardController.current
        TwoFieldDialog(
            title = "Edit entry",
            keyValue = key,
            valueValue = value,
            keyError = null,
            valueError = null,
            keyModifier = Modifier.focusRequester(focusRequester),
            onKeyChange = { key = it },
            onValueChange = { value = it },
            onConfirm = { if (key.text.isNotBlank()) { vm.update(entry.copy(key = key.text.trim(), value = value.text)); editing = null } },
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
            onConfirm = { vm.delete(entry); confirmDelete = null },
            onDismiss = { confirmDelete = null }
        )
    }
}


