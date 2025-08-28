package com.praneet.vault.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.praneet.vault.data.entity.AccountTypeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountTypeDao {
    @Query("SELECT * FROM account_types WHERE user_id = :userId ORDER BY name COLLATE NOCASE ASC")
    fun observeAccountTypes(userId: Long): Flow<List<AccountTypeEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(type: AccountTypeEntity): Long

    @Update
    suspend fun update(type: AccountTypeEntity)

    @Delete
    suspend fun delete(type: AccountTypeEntity)
}