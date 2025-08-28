package com.praneet.vault.viewmodel.manage

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.praneet.vault.data.entity.UserEntity
import com.praneet.vault.repo.VaultRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UsersViewModel @Inject constructor(app: Application, private val repo: VaultRepository) :
    AndroidViewModel(app) {
    val users: StateFlow<List<UserEntity>> = repo.observeUsers()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun add(name: String) = viewModelScope.launch { repo.addUser(name) }
    fun update(user: UserEntity) = viewModelScope.launch { repo.updateUser(user) }
    fun delete(user: UserEntity) = viewModelScope.launch { repo.deleteUser(user) }
}