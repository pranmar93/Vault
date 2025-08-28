package com.praneet.vault.viewmodel.manage

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.praneet.vault.data.entity.EntryEntity
import com.praneet.vault.repo.VaultRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EntriesViewModel @Inject constructor(
    app: Application,
    private val repo: VaultRepository,
    private val savedStateHandle: SavedStateHandle
) : AndroidViewModel(app) {
    private val accountTypeId: Long = savedStateHandle.get<Long>("accountTypeId") ?: 0L
    val entries: StateFlow<List<EntryEntity>> = repo.observeEntries(accountTypeId)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun add(key: String, value: String) =
        viewModelScope.launch { repo.addEntry(accountTypeId, key, value) }

    fun update(entry: EntryEntity) = viewModelScope.launch { repo.updateEntry(entry) }
    fun delete(entry: EntryEntity) = viewModelScope.launch { repo.deleteEntry(entry) }
}