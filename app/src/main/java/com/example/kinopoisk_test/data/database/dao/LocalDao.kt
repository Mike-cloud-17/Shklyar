package com.example.kinopoisk_test.data.database.dao

import androidx.room.*
import com.example.kinopoisk_test.domain.model.TopFilmInfo
import com.example.kinopoisk_test.domain.model.TopFilmPreview
import com.example.kinopoisk_test.domain.model.TopFilmPreviewFavourite

@Dao
interface LocalDao {

    @Query("SELECT * FROM films_info WHERE kinopoiskId = :kinopoiskId")
    suspend fun getTopFilmInfo(kinopoiskId: Int): TopFilmInfo

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inputTopFilmInfo(topFilmInfo: TopFilmInfo)


    @Query("SELECT * FROM previews")
    suspend fun getTopFilmPreviewList(): List<TopFilmPreview>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inputTopFilmPreview(topFilmPreview: TopFilmPreview)

    @Query("DELETE FROM previews")
    suspend fun deleteTopFilmPreview()


    @Query("SELECT * FROM favourites")
    suspend fun getFavouriteList(): List<TopFilmPreviewFavourite>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inputToFavouriteList(topFilmPreviewFavourite: TopFilmPreviewFavourite)

}