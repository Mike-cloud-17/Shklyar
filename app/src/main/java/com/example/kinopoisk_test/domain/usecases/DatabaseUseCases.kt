package com.example.kinopoisk_test.domain.usecases

import com.example.kinopoisk_test.common.status.RequestStatus
import com.example.kinopoisk_test.data.repository.DatabaseRepositoryImpl
import com.example.kinopoisk_test.domain.model.TopFilmInfo
import com.example.kinopoisk_test.domain.model.TopFilmPreview
import com.example.kinopoisk_test.domain.model.TopFilmPreviewFavourite
import com.example.kinopoisk_test.domain.repository.DatabaseRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow

class DatabaseUseCases {

    private val databaseRepository: DatabaseRepository = DatabaseRepositoryImpl()

    fun getTopFilmInfo(
        kinopoiskId: Int
    ): Flow<RequestStatus<TopFilmInfo>> = channelFlow {
        try {
            send(RequestStatus.Loading())
            val response = databaseRepository.getTopFilmInfo(kinopoiskId)
            send(RequestStatus.Success(response))
        } catch (e: Throwable) {
            send(RequestStatus.Failure(e))
        }
    }

    fun inputTopFilmInfo(
        topFilmInfo: TopFilmInfo
    ): Flow<RequestStatus<Int>> = channelFlow {
        try {
            send(RequestStatus.Loading())
            databaseRepository.inputTopFilmInfo(topFilmInfo)
            send(RequestStatus.Success(1))
        } catch (e: Throwable) {
            send(RequestStatus.Failure(e))
        }
    }

    fun getTopFilmPreviewList(): Flow<RequestStatus<List<TopFilmPreview>>> = channelFlow {
        try {
            send(RequestStatus.Loading())
            val response = databaseRepository.getTopFilmPreviewList()
            send(RequestStatus.Success(response))
        } catch (e: Throwable) {
            send(RequestStatus.Failure(e))
        }
    }

    fun inputTopFilmPreview(
        topFilmPreview: TopFilmPreview
    ): Flow<RequestStatus<Int>> = channelFlow {
        try {
            send(RequestStatus.Loading())
            databaseRepository.inputTopFilmTopFilmPreview(topFilmPreview)
            send(RequestStatus.Success(1))
        } catch (e: Throwable) {
            send(RequestStatus.Failure(e))
        }
    }

    fun deleteTopFilmPreviewList(): Flow<RequestStatus<Int>> = channelFlow {
        try {
            send(RequestStatus.Loading())
            databaseRepository.deleteTopFilmPreview()
            send(RequestStatus.Success(1))
        } catch (e: Throwable) {
            send(RequestStatus.Failure(e))
        }
    }

    fun getFavouriteList(): Flow<RequestStatus<List<TopFilmPreviewFavourite>>> = channelFlow {
        try {
            send(RequestStatus.Loading())
            val response = databaseRepository.getFavouriteList()
            send(RequestStatus.Success(response))
        } catch (e: Throwable) {
            send(RequestStatus.Failure(e))
        }
    }

    fun inputToFavouriteList(
        topFilmPreviewFavourite: TopFilmPreviewFavourite
    ): Flow<RequestStatus<Int>> = channelFlow {
        try {
            send(RequestStatus.Loading())
            databaseRepository.inputToFavouriteList(topFilmPreviewFavourite)
            send(RequestStatus.Success(1))
        } catch (e: Throwable) {
            send(RequestStatus.Failure(e))
        }
    }

}