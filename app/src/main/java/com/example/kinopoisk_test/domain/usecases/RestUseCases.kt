package com.example.kinopoisk_test.domain.usecases

import com.example.kinopoisk_test.common.status.RequestStatus
import com.example.kinopoisk_test.data.repository.RestRepositoryImpl
import com.example.kinopoisk_test.domain.model.TopFilmInfo
import com.example.kinopoisk_test.domain.model.TopFilmList
import com.example.kinopoisk_test.domain.repository.RestRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow

class RestUseCases {

    private val restRepository: RestRepository = RestRepositoryImpl()
    private val databaseUseCases: DatabaseUseCases = DatabaseUseCases()

    private var isFirstRequest = true

    fun getTopFilmList(
        page: Int,
        isRefresh: Boolean = false
    ): Flow<RequestStatus<TopFilmList>> = flow {
        try {
            emit(RequestStatus.Loading())
            val response = restRepository.getTopFilmList(page)
            if (isFirstRequest || isRefresh) {
                databaseUseCases.deleteTopFilmPreviewList().firstOrNull()
                isFirstRequest = false
            }
            response.films.map { film ->
                databaseUseCases.inputTopFilmPreview(film)
            }
            emit(RequestStatus.Success(response))
        } catch (e: Throwable) {
            try {
                databaseUseCases.getTopFilmPreviewList()
                    .firstOrNull()?.let {
                        when (it) {
                            is RequestStatus.Failure -> {
                                emit(RequestStatus.Failure(it.e))
                            }
                            is RequestStatus.Loading -> {
                                emit(RequestStatus.Loading())
                            }
                            is RequestStatus.Success -> {
                                emit(
                                    RequestStatus.Success(
                                        TopFilmList(
                                            it.successData.size / 20,
                                            it.successData,
                                            true
                                        )
                                    )
                                )
                            }
                        }
                    } ?: emit(RequestStatus.Failure(e))
            } catch (er: Throwable) {
                emit(RequestStatus.Success(TopFilmList(1, emptyList(), true)))
            }
        }
    }

    fun getTopFilmInfo(
        id: Int
    ): Flow<RequestStatus<TopFilmInfo>> = flow {
        try {
            emit(RequestStatus.Loading())
            val response = restRepository.getTopFilmInfo(id)
            databaseUseCases.inputTopFilmInfo(response)
            emit(RequestStatus.Success(response))
        } catch (e: Throwable) {
            try {
                databaseUseCases.getTopFilmInfo(id)
                    .firstOrNull()?.let {
                        emit(it)
                    } ?: emit(RequestStatus.Failure(e))
            } catch (er: Throwable) {
                emit(RequestStatus.Failure(er))
            }
        }
    }

}