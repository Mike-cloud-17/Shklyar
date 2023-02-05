package com.example.kinopoisk_test.common.logger

import android.util.Log

object MyLogger {

    fun log(message: String, e: Throwable? = null) {
        Log.d("MY_LOG", message, e)
    }

}