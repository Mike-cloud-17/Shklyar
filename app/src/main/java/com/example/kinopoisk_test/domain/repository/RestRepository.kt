package com.example.kinopoisk_test.domain.repository

import com.example.kinopoisk_test.domain.model.TopFilmInfo
import com.example.kinopoisk_test.domain.model.TopFilmList

interface RestRepository {

    suspend fun getTopFilmList(page: Int): TopFilmList

    suspend fun getTopFilmInfo(id: Int): TopFilmInfo
}