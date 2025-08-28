package com.praneet.vault.ui.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.praneet.vault.ui.components.DropdownField
import com.praneet.vault.ui.components.ReadOnlyScrollableField
import com.praneet.vault.viewmodel.search.SearchViewModel

@Composable
fun SearchScreen(vm: SearchViewModel = androidx.hilt.navigation.compose.hiltViewModel()) {
    val users by vm.users.collectAsState()
    val types by vm.accountTypes.collectAsState()
    val keys by vm.keys.collectAsState()
    val value by vm.value.collectAsState()

    var selectedUserIndex by remember { mutableIntStateOf(-1) }
    var selectedTypeIndex by remember { mutableIntStateOf(-1) }
    var selectedKeyIndex by remember { mutableIntStateOf(-1) }

    Column(Modifier.padding(16.dp)) {
        DropdownField(
            label = "User",
            options = listOf("Select User") + users.map { it.name },
            selectedIndex = if (selectedUserIndex < 0) 0 else selectedUserIndex + 1,
            enabled = true,
            onSelected = { idx ->
                if (idx == 0) {
                    selectedUserIndex = -1
                    vm.onUserSelected(null)
                } else {
                    selectedUserIndex = idx - 1
                    vm.onUserSelected(users[selectedUserIndex].id)
                }
                selectedTypeIndex = -1
            }
        )

        DropdownField(
            label = "Account Type",
            options = listOf("Select Account Type") + types.map { it.name },
            selectedIndex = if (selectedTypeIndex < 0) 0 else selectedTypeIndex + 1,
            enabled = selectedUserIndex >= 0,
            onSelected = { idx ->
                if (idx == 0) {
                    selectedTypeIndex = -1
                    vm.onTypeSelected(null)
                    selectedKeyIndex = -1
                    vm.onKeySelected(null)
                } else {
                    selectedTypeIndex = idx - 1
                    vm.onTypeSelected(types[selectedTypeIndex].id)
                    selectedKeyIndex = -1
                    vm.onKeySelected(null)
                }
            }
        )

        DropdownField(
            label = "Key",
            options = listOf("Select Key") + keys,
            selectedIndex = if (selectedKeyIndex < 0) 0 else selectedKeyIndex + 1,
            enabled = selectedUserIndex >= 0 && selectedTypeIndex >= 0,
            onSelected = { idx ->
                if (idx == 0) {
                    selectedKeyIndex = -1
                    vm.onKeySelected(null)
                } else {
                    selectedKeyIndex = idx - 1
                    vm.onKeySelected(keys[selectedKeyIndex])
                }
            }
        )

        ReadOnlyScrollableField(
            label = "Value",
            value = value,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
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


