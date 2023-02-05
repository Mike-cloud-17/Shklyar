package com.example.kinopoisk_test.presentation.fragments.info

import androidx.lifecycle.viewModelScope
import com.example.kinopoisk_test.common.logger.MyLogger
import com.example.kinopoisk_test.common.status.RequestStatus
import com.example.kinopoisk_test.presentation.fragments.BaseViewModel
import com.example.kinopoisk_test.presentation.model.TopFilmInfoVo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

class TopFilmInfoViewModel : BaseViewModel() {

    private val _filmInfo = MutableStateFlow(TopFilmInfoVo.empty())
    val filmList = _filmInfo.asStateFlow()


    fun getTopFilmInfo(id: Int) {
        restUseCases.getTopFilmInfo(id)
            .flowOn(Dispatchers.IO)
            .catch {
                MyLogger.log("getTopFilmList() error", it)
                _isError.emit(true)
            }
            .onEach { requestStatus ->
                when(requestStatus) {
                    is RequestStatus.Failure -> {
                        MyLogger.log("getTopFilmInfo() error", requestStatus.e)
                        _isError.emit(true)
                    }
                    is RequestStatus.Loading -> {
                        _isLoading.emit(true)
                    }
                    is RequestStatus.Success -> {
                        requestStatus.successData.also { data ->
                            _filmInfo.emit(topFilmInfoFormatter.format(data))
                        }
                    }
                }
            }
            .launchIn(viewModelScope)
    }

}