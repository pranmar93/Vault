package com.praneet.vault.di

import android.content.Context
import com.praneet.vault.data.db.VaultDatabase
import com.praneet.vault.repo.VaultRepository
import com.praneet.vault.data.dao.AccountTypeDao
import com.praneet.vault.data.dao.EntryDao
import com.praneet.vault.data.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): VaultDatabase =
        VaultDatabase.getInstance(context)

    @Provides
    fun provideUserDao(db: VaultDatabase) = db.userDao()

    @Provides
    fun provideAccountTypeDao(db: VaultDatabase) = db.accountTypeDao()

    @Provides
    fun provideEntryDao(db: VaultDatabase) = db.entryDao()

    @Provides
    @Singleton
    fun provideRepository(
        userDao: UserDao,
        accountTypeDao: AccountTypeDao,
        entryDao: EntryDao
    ): VaultRepository = VaultRepository(userDao, accountTypeDao, entryDao)
}


