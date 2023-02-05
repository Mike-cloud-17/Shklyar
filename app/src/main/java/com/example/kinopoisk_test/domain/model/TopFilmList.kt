package com.example.kinopoisk_test.domain.model

data class TopFilmList(
    val pagesCount: Int,
    val films: List<TopFilmPreview>,
    var isFromDatabase: Boolean = false
)
