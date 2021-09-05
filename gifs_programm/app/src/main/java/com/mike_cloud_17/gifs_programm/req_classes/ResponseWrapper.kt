package com.mike_cloud_17.gifs_programm.req_classes

import com.mike_cloud_17.gifs_programm.req_classes.Gif

data class ResponseWrapper(val result: Collection<Gif>, val totalCount: Int)