package com.example.myapplication.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.myapplication.data.db.dao.BudgetDao
import com.example.myapplication.data.db.dao.BudgetItemDao
import com.example.myapplication.data.db.dao.ClientDao
import com.example.myapplication.data.db.dao.SettingsDao
import com.example.myapplication.data.db.entity.BudgetEntity
import com.example.myapplication.data.db.entity.BudgetItemEntity
import com.example.myapplication.data.db.entity.ClientEntity
import com.example.myapplication.data.db.entity.SettingsEntity

@Database(
    entities = [
        BudgetEntity::class,
        ClientEntity::class,
        BudgetItemEntity::class,
        SettingsEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun budgetDao(): BudgetDao
    abstract fun clientDao(): ClientDao
    abstract fun budgetItemDao(): BudgetItemDao
    abstract fun settingsDao(): SettingsDao
}
