package com.praneet.vault.viewmodel.manage

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.praneet.vault.data.entity.AccountTypeEntity
import com.praneet.vault.repo.VaultRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountTypesViewModel @Inject constructor(
    app: Application,
    private val repo: VaultRepository,
    private val savedStateHandle: SavedStateHandle
) : AndroidViewModel(app) {
    private val userId: Long = savedStateHandle.get<Long>("userId") ?: 0L
    val types: StateFlow<List<AccountTypeEntity>> = repo.observeAccountTypes(userId)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun add(name: String) = viewModelScope.launch { repo.addAccountType(userId, name) }
    fun update(type: AccountTypeEntity) = viewModelScope.launch { repo.updateAccountType(type) }
    fun delete(type: AccountTypeEntity) = viewModelScope.launch { repo.deleteAccountType(type) }
}