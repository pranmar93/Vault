package com.praneet.vault.di

import android.content.Context
import com.praneet.vault.data.AppDatabase
import com.praneet.vault.data.VaultRepository
import com.praneet.vault.data.UserDao
import com.praneet.vault.data.AccountTypeDao
import com.praneet.vault.data.EntryDao
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
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        AppDatabase.getInstance(context)

    @Provides
    fun provideUserDao(db: AppDatabase) = db.userDao()

    @Provides
    fun provideAccountTypeDao(db: AppDatabase) = db.accountTypeDao()

    @Provides
    fun provideEntryDao(db: AppDatabase) = db.entryDao()

    @Provides
    @Singleton
    fun provideRepository(
        userDao: UserDao,
        accountTypeDao: AccountTypeDao,
        entryDao: EntryDao
    ): VaultRepository = VaultRepository(userDao, accountTypeDao, entryDao)
}


