package com.mike_cloud_17.gifs_programm.request_work

import com.bumptech.glide.request.RequestListener

interface RequestDrawable {
    fun onLoadFailed()
    fun onResourceReady()
}