package com.praneet.vault.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users ORDER BY name COLLATE NOCASE ASC")
    fun observeUsers(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(user: UserEntity): Long

    @Update
    suspend fun update(user: UserEntity)

    @Delete
    suspend fun delete(user: UserEntity)
}

@Dao
interface AccountTypeDao {
    @Query("SELECT * FROM account_types WHERE user_id = :userId ORDER BY name ASC")
    fun observeAccountTypes(userId: Long): Flow<List<AccountTypeEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(type: AccountTypeEntity): Long

    @Update
    suspend fun update(type: AccountTypeEntity)

    @Delete
    suspend fun delete(type: AccountTypeEntity)
}

@Dao
interface EntryDao {
    @Query("SELECT * FROM entries WHERE account_type_id = :accountTypeId ORDER BY `key` ASC")
    fun observeEntries(accountTypeId: Long): Flow<List<EntryEntity>>

    @Query("SELECT value FROM entries WHERE account_type_id = :accountTypeId AND `key` = :key LIMIT 1")
    fun observeValue(accountTypeId: Long, key: String): Flow<String?>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(entry: EntryEntity): Long

    @Update
    suspend fun update(entry: EntryEntity)

    @Delete
    suspend fun delete(entry: EntryEntity)
}


