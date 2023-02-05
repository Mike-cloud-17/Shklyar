package com.example.kinopoisk_test.app

import android.app.Application
import com.example.kinopoisk_test.data.database.db.LocalDatabase
import com.github.terrakok.cicerone.Cicerone

class App: Application() {

    private val cicerone = Cicerone.create()
    val router get() = cicerone.router
    val navigatorHolder get() = cicerone.getNavigatorHolder()

    override fun onCreate() {
        super.onCreate()

        LocalDatabase.initDatabase(this)
    }

}