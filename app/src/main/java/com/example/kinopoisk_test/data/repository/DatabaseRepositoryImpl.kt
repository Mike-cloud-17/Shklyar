package com.example.kinopoisk_test.data.repository

import com.example.kinopoisk_test.data.database.db.LocalDatabase
import com.example.kinopoisk_test.domain.model.TopFilmInfo
import com.example.kinopoisk_test.domain.model.TopFilmPreview
import com.example.kinopoisk_test.domain.model.TopFilmPreviewFavourite
import com.example.kinopoisk_test.domain.repository.DatabaseRepository

class DatabaseRepositoryImpl : DatabaseRepository {

    override suspend fun getTopFilmInfo(kinopoiskId: Int): TopFilmInfo =
        LocalDatabase.database.getDao().getTopFilmInfo(kinopoiskId)

    override suspend fun inputTopFilmInfo(topFilmInfo: TopFilmInfo) {
        LocalDatabase.database.getDao().inputTopFilmInfo(topFilmInfo)
    }

    override suspend fun getTopFilmPreviewList(): List<TopFilmPreview> =
        LocalDatabase.database.getDao().getTopFilmPreviewList()


    override suspend fun inputTopFilmTopFilmPreview(topFilmPreview: TopFilmPreview) {
        LocalDatabase.database.getDao().inputTopFilmPreview(topFilmPreview)
    }

    override suspend fun deleteTopFilmPreview() {
        LocalDatabase.database.getDao().deleteTopFilmPreview()
    }


    override suspend fun getFavouriteList(): List<TopFilmPreviewFavourite> =
        LocalDatabase.database.getDao().getFavouriteList()

    override suspend fun inputToFavouriteList(topFilmPreviewFavourite: TopFilmPreviewFavourite) {
        LocalDatabase.database.getDao().inputToFavouriteList(topFilmPreviewFavourite)
    }

}