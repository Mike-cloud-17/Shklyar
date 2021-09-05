package com.mike_cloud_17.gifs_programm.request_work

interface EventObserver<T> {
    fun onSuccess(data: T)
    fun onLoading()
    fun onError(throwable: Throwable? = null)
}