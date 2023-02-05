package com.example.kinopoisk_test.domain.repository

import com.example.kinopoisk_test.domain.model.TopFilmInfo
import com.example.kinopoisk_test.domain.model.TopFilmPreview
import com.example.kinopoisk_test.domain.model.TopFilmPreviewFavourite

interface DatabaseRepository {

    suspend fun getTopFilmInfo(kinopoiskId: Int): TopFilmInfo

    suspend fun inputTopFilmInfo(topFilmInfo: TopFilmInfo)


    suspend fun getTopFilmPreviewList(): List<TopFilmPreview>

    suspend fun inputTopFilmTopFilmPreview(topFilmPreview: TopFilmPreview)

    suspend fun deleteTopFilmPreview()


    suspend fun getFavouriteList(): List<TopFilmPreviewFavourite>

    suspend fun inputToFavouriteList(topFilmPreviewFavourite: TopFilmPreviewFavourite)

}