package com.mike_cloud_17.gifs_programm.req_classes

data class State<T>(val event: Event, val throwable: Throwable? = null, val data: T? = null)
