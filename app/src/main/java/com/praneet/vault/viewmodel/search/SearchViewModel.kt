package com.praneet.vault.viewmodel.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.praneet.vault.data.entity.AccountTypeEntity
import com.praneet.vault.data.entity.UserEntity
import com.praneet.vault.repo.VaultRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(app: Application, private val repo: VaultRepository) :
    AndroidViewModel(app) {
    val users: StateFlow<List<UserEntity>> =
        repo.observeUsers().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val selectedUserId = MutableStateFlow<Long?>(null)
    val accountTypes: StateFlow<List<AccountTypeEntity>> = selectedUserId
        .flatMapLatest { id -> if (id == null) flowOf(emptyList()) else repo.observeAccountTypes(id) }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val selectedTypeId = MutableStateFlow<Long?>(null)
    private val selectedKey = MutableStateFlow<String?>(null)

    // Keys for selected account type
    val keys: StateFlow<List<String>> = selectedTypeId
        .flatMapLatest { typeId ->
            if (typeId == null) flowOf(emptyList()) else repo.observeEntries(typeId)
                .map { list -> list.map { it.key } }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val value: StateFlow<String> = combine(selectedTypeId, selectedKey) { typeId, key ->
        Pair(typeId, key)
    }.flatMapLatest { (typeId, key) ->
        if (typeId == null || key.isNullOrBlank()) flowOf("") else repo.observeValue(typeId, key)
            .map { it ?: "" }
    }.stateIn(viewModelScope, SharingStarted.Lazily, "")

    fun onUserSelected(id: Long?) {
        selectedUserId.value = id
        selectedTypeId.value = null
        selectedKey.value = null
    }

    fun onTypeSelected(id: Long?) {
        selectedTypeId.value = id
        selectedKey.value = null
    }

    fun onKeySelected(key: String?) {
        selectedKey.value = key
    }
}