package com.truspirit.idcard.model


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class EmployeeCard(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val empNumber: String,
    val mobile: String,
    val filePath: String,
    val createdAt: Long
)
