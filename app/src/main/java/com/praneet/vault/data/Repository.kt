package com.praneet.vault.data

import android.content.Context
import kotlinx.coroutines.flow.Flow

class VaultRepository private constructor(
    private val userDao: UserDao,
    private val accountTypeDao: AccountTypeDao,
    private val entryDao: EntryDao
) {
    // Users
    fun observeUsers(): Flow<List<UserEntity>> = userDao.observeUsers()
    suspend fun addUser(name: String): Long = userDao.insert(UserEntity(name = name))
    suspend fun updateUser(user: UserEntity) = userDao.update(user)
    suspend fun deleteUser(user: UserEntity) = userDao.delete(user)

    // Account Types
    fun observeAccountTypes(userId: Long): Flow<List<AccountTypeEntity>> = accountTypeDao.observeAccountTypes(userId)
    suspend fun addAccountType(userId: Long, name: String): Long = accountTypeDao.insert(AccountTypeEntity(userId = userId, name = name))
    suspend fun updateAccountType(type: AccountTypeEntity) = accountTypeDao.update(type)
    suspend fun deleteAccountType(type: AccountTypeEntity) = accountTypeDao.delete(type)

    // Entries
    fun observeEntries(accountTypeId: Long): Flow<List<EntryEntity>> = entryDao.observeEntries(accountTypeId)
    fun observeValue(accountTypeId: Long, key: String): Flow<String?> = entryDao.observeValue(accountTypeId, key)
    suspend fun addEntry(accountTypeId: Long, key: String, value: String): Long = entryDao.insert(EntryEntity(accountTypeId = accountTypeId, key = key, value = value))
    suspend fun updateEntry(entry: EntryEntity) = entryDao.update(entry)
    suspend fun deleteEntry(entry: EntryEntity) = entryDao.delete(entry)

    companion object {
        @Volatile
        private var INSTANCE: VaultRepository? = null

        fun get(context: Context): VaultRepository = INSTANCE ?: synchronized(this) {
            val db = AppDatabase.getInstance(context)
            INSTANCE ?: VaultRepository(db.userDao(), db.accountTypeDao(), db.entryDao()).also { INSTANCE = it }
        }
    }
}


