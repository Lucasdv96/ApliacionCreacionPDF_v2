package com.example.myapplication.di

import android.content.Context
import androidx.room.Room
import com.example.myapplication.data.db.AppDatabase
import com.example.myapplication.data.repository.BudgetRepository
import com.example.myapplication.data.repository.ClientRepository
import com.example.myapplication.data.repository.SettingsRepository

interface AppContainer {
    val budgetRepository: BudgetRepository
    val clientRepository: ClientRepository
    val settingsRepository: SettingsRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    private val database: AppDatabase by lazy {
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "budget_app_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    override val budgetRepository: BudgetRepository by lazy {
        BudgetRepository(database.budgetDao())
    }

    override val clientRepository: ClientRepository by lazy {
        ClientRepository(database.clientDao())
    }

    override val settingsRepository: SettingsRepository by lazy {
        SettingsRepository(database.settingsDao())
    }
}
