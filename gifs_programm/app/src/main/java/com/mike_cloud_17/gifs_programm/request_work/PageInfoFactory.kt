package com.mike_cloud_17.gifs_programm.request_work

import com.mike_cloud_17.gifs_programm.R
import com.mike_cloud_17.gifs_programm.req_classes.PageInfo
import com.mike_cloud_17.gifs_programm.req_classes.PageSection

class PageInfoFactory{
    companion object {

        val PAGES: Set<PageInfo> = setOf(
            PageInfo(R.string.tab_random, PageSection.RANDOM),
            PageInfo(R.string.tab_top, PageSection.TOP),
            PageInfo(R.string.tab_latest, PageSection.LATEST)
        )
    }
}