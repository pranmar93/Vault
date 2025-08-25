package com.praneet.vault.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["name"], unique = true)]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "name") val name: String
)

@Entity(
    tableName = "account_types",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["user_id", "name"], unique = true)]
)
data class AccountTypeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "user_id") val userId: Long,
    @ColumnInfo(name = "name") val name: String
)

@Entity(
    tableName = "entries",
    foreignKeys = [
        ForeignKey(
            entity = AccountTypeEntity::class,
            parentColumns = ["id"],
            childColumns = ["account_type_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["account_type_id", "key"], unique = true)]
)
data class EntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "account_type_id") val accountTypeId: Long,
    @ColumnInfo(name = "key") val key: String,
    @ColumnInfo(name = "value") val value: String
)


