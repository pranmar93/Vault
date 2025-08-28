package com.praneet.vault.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

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