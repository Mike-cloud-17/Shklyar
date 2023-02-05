package com.example.kinopoisk_test.presentation.fragments.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.kinopoisk_test.common.logger.MyLogger
import com.example.kinopoisk_test.common.status.RequestStatus
import com.example.kinopoisk_test.domain.model.TopFilmList
import com.example.kinopoisk_test.domain.model.TopFilmPreview
import com.example.kinopoisk_test.domain.model.TopFilmPreviewFavourite
import com.example.kinopoisk_test.presentation.fragments.BaseViewModel
import com.example.kinopoisk_test.presentation.model.TopFilmPreviewVo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

class TopFilmPreviewListViewModel : BaseViewModel() {

    private var page: Int = 1
    private var pageCount: Int = 1
    val isLastPage: Boolean
        get() = page == pageCount

    private var isLastFromDb = false

    private val _filmList = MutableStateFlow<List<TopFilmPreviewVo>>(emptyList())
    val filmList = _filmList.asSharedFlow()


    private val filmHashMap = HashMap<Int, TopFilmPreviewFavourite>()


    fun getTopFilmList(isRefresh: Boolean = false) {
        if (page <= pageCount) {
            restUseCases.getTopFilmList(page, isRefresh)
                .combine(databaseUseCases.getFavouriteList()) { listFromNet, listFromFavourite ->
                    handleFavouriteCombine(listFromNet, listFromFavourite)
                }
                .flowOn(Dispatchers.IO)
                .onEach { requestStatus ->
                    when(requestStatus) {
                        is RequestStatus.Failure -> {
                            MyLogger.log("getTopFilmList() error", requestStatus.e)
                            _filmList.value = listOf()
                            filmHashMap.clear()
                            _isError.emit(true)
                        }
                        is RequestStatus.Loading -> {
                            _isLoading.emit(true)
                        }
                        is RequestStatus.Success -> {
                            requestStatus.successData.also { data ->
                                page++
                                if(isLastFromDb != data.isFromDatabase) {
                                    page = 1
                                    _filmList.value = listOf()
                                    filmHashMap.clear()
                                    pageCount = data.pagesCount
                                    isLastFromDb = data.isFromDatabase
                                    getTopFilmList()
                                    return@onEach
                                }

                                pageCount = data.pagesCount
                                isLastFromDb = data.isFromDatabase

                                val value = arrayListOf<TopFilmPreviewVo>().apply {
                                    addAll(_filmList.value)
                                    addAll(topFilmPreviewFormatter.format(data.films))
                                }
                                _filmList.value = value

                                topFilmPreviewFormatter.formatToFavourite(data.films).map {
                                    filmHashMap[it.filmId] = it
                                }
                            }
                        }
                    }
                }
                .catch {
                    MyLogger.log("getTopFilmList() error", it)
                    _filmList.value = listOf()
                    filmHashMap.clear()
                    _isError.emit(true)
                }
                .launchIn(viewModelScope)
        }
    }

    private fun handleFavouriteCombine(
        listFromNet: RequestStatus<TopFilmList>,
        listFromFavourite: RequestStatus<List<TopFilmPreviewFavourite>>
    ): RequestStatus<TopFilmList> {
        return when(listFromNet) {
            is RequestStatus.Failure, is RequestStatus.Loading -> {
                listFromNet
            }
            is RequestStatus.Success -> {
                when(listFromFavourite) {
                    is RequestStatus.Failure, is RequestStatus.Loading -> {
                        listFromNet
                    }
                    is RequestStatus.Success -> {
                        val favouritesMap = listFromFavourite.successData.associateBy {
                            it.filmId
                        }
                        RequestStatus.Success(TopFilmList(
                            listFromNet.successData.pagesCount,
                            listFromNet.successData.films.map { preview ->
                                TopFilmPreview(
                                    preview.filmId,
                                    preview.nameRu,
                                    preview.posterUrlPreview,
                                    preview.year,
                                    preview.genre,
                                    favouritesMap.containsKey(preview.filmId)
                                )
                            }
                        ))
                    }
                }
            }
        }
    }

    fun getFavouriteList() {
        databaseUseCases.getFavouriteList()
            .flowOn(Dispatchers.IO)
            .onEach { requestStatus ->
                when(requestStatus) {
                    is RequestStatus.Failure -> {
                        MyLogger.log("getFavouriteList() error", requestStatus.e)
                        _filmList.value = listOf()
                        filmHashMap.clear()
                        _isError.emit(true)
                    }
                    is RequestStatus.Loading -> {
                        _isLoading.emit(true)
                    }
                    is RequestStatus.Success -> {
                        requestStatus.successData.also { data ->
                            isLastFromDb = true
                            page = pageCount
                            _filmList.value = topFilmPreviewFormatter.format(data)
                        }
                    }
                }
            }
            .catch {
                MyLogger.log("getTopFilmList() error", it)
                _filmList.value = listOf()
                filmHashMap.clear()
                _isError.emit(true)
            }
            .launchIn(viewModelScope)
    }

    fun addToFavouriteList(filmId: Int) {
        filmHashMap[filmId]?.let { film ->
            databaseUseCases.inputToFavouriteList(film)
                .flowOn(Dispatchers.IO)
                .onEach { requestStatus ->
                    when(requestStatus) {
                        is RequestStatus.Failure -> {
                            MyLogger.log("addToFavouriteList() error", requestStatus.e)
                            _isError.emit(true)
                        }
                        is RequestStatus.Loading -> {
                            _isLoading.emit(true)
                        }
                        is RequestStatus.Success -> {
                            _isLoading.emit(false)
                        }
                    }
                }
                .catch {
                    MyLogger.log("addToFavouriteList() error", it)
                    _isError.emit(true)
                }
                .launchIn(viewModelScope)
        } ?: run {
            _isError.tryEmit(false)
        }
    }

}