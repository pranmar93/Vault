package com.praneet.vault.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.praneet.vault.data.dao.AccountTypeDao
import com.praneet.vault.data.dao.EntryDao
import com.praneet.vault.data.dao.UserDao
import com.praneet.vault.data.entity.AccountTypeEntity
import com.praneet.vault.data.entity.EntryEntity
import com.praneet.vault.data.entity.UserEntity

@Database(
    entities = [UserEntity::class, AccountTypeEntity::class, EntryEntity::class],
    version = 1,
    exportSchema = true
)
abstract class VaultDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun accountTypeDao(): AccountTypeDao
    abstract fun entryDao(): EntryDao

    companion object {
        @Volatile
        private var INSTANCE: VaultDatabase? = null

        fun getInstance(context: Context): VaultDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                VaultDatabase::class.java,
                "vault.db"
            ).fallbackToDestructiveMigration().build().also { INSTANCE = it }
        }
    }
}