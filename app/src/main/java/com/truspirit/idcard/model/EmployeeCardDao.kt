package com.truspirit.idcard.model


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EmployeeCardDao {
    @Insert
    suspend fun insert(card: EmployeeCard)

    @Query("SELECT * FROM EmployeeCard ORDER BY createdAt DESC")
    fun allCards(): Flow<List<EmployeeCard>>
}
