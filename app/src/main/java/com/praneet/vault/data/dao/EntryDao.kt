package com.praneet.vault.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.praneet.vault.data.entity.EntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EntryDao {
    @Query("SELECT * FROM entries WHERE account_type_id = :accountTypeId ORDER BY `key` COLLATE NOCASE ASC")
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