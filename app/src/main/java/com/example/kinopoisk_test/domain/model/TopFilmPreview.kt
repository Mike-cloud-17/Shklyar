package com.example.kinopoisk_test.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "previews")
data class TopFilmPreview(
    @PrimaryKey val filmId: Int,
    val nameRu: String,
    val posterUrlPreview: String,
    val year: String,
    val genre: String,
    val isFavourite: Boolean = false
)
