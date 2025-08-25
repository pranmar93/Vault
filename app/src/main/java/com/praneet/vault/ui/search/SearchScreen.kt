package com.praneet.vault.ui.search

import android.app.Application
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.clickable
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import com.praneet.vault.ui.components.DropdownField
import com.praneet.vault.ui.components.ReadOnlyScrollableField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.praneet.vault.data.AccountTypeEntity
import com.praneet.vault.data.UserEntity
import com.praneet.vault.data.VaultRepository
import kotlinx.coroutines.flow.*

class SearchViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = VaultRepository.get(app)
    val users: StateFlow<List<UserEntity>> = repo.observeUsers().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val selectedUserId = MutableStateFlow<Long?>(null)
    val accountTypes: StateFlow<List<AccountTypeEntity>> = selectedUserId
        .flatMapLatest { id -> if (id == null) flowOf(emptyList()) else repo.observeAccountTypes(id) }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val selectedTypeId = MutableStateFlow<Long?>(null)
    private val selectedKey = MutableStateFlow<String?>(null)

    // Keys for selected account type
    val keys: StateFlow<List<String>> = selectedTypeId
        .flatMapLatest { typeId ->
            if (typeId == null) flowOf(emptyList()) else repo.observeEntries(typeId).map { list -> list.map { it.key } }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val value: StateFlow<String> = combine(selectedTypeId, selectedKey) { typeId, key ->
        Pair(typeId, key)
    }.flatMapLatest { (typeId, key) ->
        if (typeId == null || key.isNullOrBlank()) flowOf("") else repo.observeValue(typeId, key).map { it ?: "" }
    }.stateIn(viewModelScope, SharingStarted.Lazily, "")

    fun onUserSelected(id: Long?) { selectedUserId.value = id; selectedTypeId.value = null; selectedKey.value = null }
    fun onTypeSelected(id: Long?) { selectedTypeId.value = id; selectedKey.value = null }
    fun onKeySelected(key: String?) { selectedKey.value = key }
}

@Composable
fun SearchScreen(vm: SearchViewModel = run {
    val app = LocalContext.current.applicationContext as Application
    viewModel(factory = object: ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return SearchViewModel(app) as T
        }
    })
}) {
    val users by vm.users.collectAsState()
    val types by vm.accountTypes.collectAsState()
    val keys by vm.keys.collectAsState()
    val value by vm.value.collectAsState()

    var selectedUserIndex by remember { mutableStateOf(-1) }
    var selectedTypeIndex by remember { mutableStateOf(-1) }
    var selectedKeyIndex by remember { mutableStateOf(-1) }

    Column(Modifier.padding(16.dp)) {
        DropdownField(
            label = "User",
            options = listOf("Select User") + users.map { it.name },
            selectedIndex = if (selectedUserIndex < 0) 0 else selectedUserIndex + 1,
            enabled = true,
            onSelected = { idx ->
                if (idx == 0) { selectedUserIndex = -1; vm.onUserSelected(null) }
                else { selectedUserIndex = idx - 1; vm.onUserSelected(users[selectedUserIndex].id) }
                selectedTypeIndex = -1
            }
        )

        DropdownField(
            label = "Account Type",
            options = listOf("Select Account Type") + types.map { it.name },
            selectedIndex = if (selectedTypeIndex < 0) 0 else selectedTypeIndex + 1,
            enabled = selectedUserIndex >= 0,
            onSelected = { idx ->
                if (idx == 0) { selectedTypeIndex = -1; vm.onTypeSelected(null); selectedKeyIndex = -1; vm.onKeySelected(null) }
                else { selectedTypeIndex = idx - 1; vm.onTypeSelected(types[selectedTypeIndex].id); selectedKeyIndex = -1; vm.onKeySelected(null) }
            }
        )

        DropdownField(
            label = "Key",
            options = listOf("Select Key") + keys,
            selectedIndex = if (selectedKeyIndex < 0) 0 else selectedKeyIndex + 1,
            enabled = selectedUserIndex >= 0 && selectedTypeIndex >= 0,
            onSelected = { idx ->
                if (idx == 0) { selectedKeyIndex = -1; vm.onKeySelected(null) }
                else { selectedKeyIndex = idx - 1; vm.onKeySelected(keys[selectedKeyIndex]) }
            }
        )

        ReadOnlyScrollableField(
            label = "Value",
            value = value,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )
    }

    // Reset fields when this screen becomes visible again (e.g., switch from Manage to Search)
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                selectedUserIndex = -1
                selectedTypeIndex = -1
                selectedKeyIndex = -1
                vm.onUserSelected(null)
                vm.onTypeSelected(null)
                vm.onKeySelected(null)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
}

// Dropdown UI is now in ui/components/Dropdown.kt


