package com.example.kinopoisk_test.presentation.fragments

import androidx.lifecycle.ViewModel
import com.example.kinopoisk_test.domain.usecases.DatabaseUseCases
import com.example.kinopoisk_test.domain.usecases.RestUseCases
import com.example.kinopoisk_test.presentation.model.formatters.TopFilmInfoFormatter
import com.example.kinopoisk_test.presentation.model.formatters.TopFilmPreviewFormatter
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

abstract class BaseViewModel : ViewModel() {

    protected val topFilmInfoFormatter = TopFilmInfoFormatter()
    protected val topFilmPreviewFormatter = TopFilmPreviewFormatter()

    protected val restUseCases = RestUseCases()
    protected val databaseUseCases = DatabaseUseCases()

    protected val _isError = MutableSharedFlow<Boolean>()
    val isError = _isError.asSharedFlow()

    protected val _isLoading = MutableSharedFlow<Boolean>()
    val isLoading = _isLoading.asSharedFlow()

}