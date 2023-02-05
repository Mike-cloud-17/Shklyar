package com.example.kinopoisk_test.data.api

import com.example.kinopoisk_test.data.dto.topinfo.TopFilmInfoDto
import com.example.kinopoisk_test.data.dto.tops.TopFilmListDto
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface Api {

    @GET("api/v2.2/films/top")
    suspend fun getTopFilmList(
        @Header("X-API-KEY") apiKey: String = "e30ffed0-76ab-4dd6-b41f-4c9da2b2735b",
        @Query("type") type: String = "TOP_250_BEST_FILMS",
        @Query("page") page: Int
    ) : TopFilmListDto

    @GET("api/v2.2/films/{id}")
    suspend fun getTopFilmInfo(
        @Header("X-API-KEY") apiKey: String = "e30ffed0-76ab-4dd6-b41f-4c9da2b2735b",
        @Path("id") id: Int,
    ) : TopFilmInfoDto

}