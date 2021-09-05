package com.mike_cloud_17.gifs_programm.request_work

import com.mike_cloud_17.gifs_programm.req_classes.Gif
import com.mike_cloud_17.gifs_programm.req_classes.ResponseWrapper
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface Api {
    @GET("/{section}/{page}?json=true")
    fun getSectionGIFs(
        @Path("section") section: String,
        @Path("page") page: Int,
    ): Call<ResponseWrapper>

    @GET("/random?json=true")
    fun getRandomGif(): Call<Gif>
}