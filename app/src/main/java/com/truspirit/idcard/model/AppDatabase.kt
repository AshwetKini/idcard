package com.truspirit.idcard.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [EmployeeCard::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun employeeCardDao(): EmployeeCardDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "idcard-db"
                ).build().also { INSTANCE = it }
            }
    }
}
