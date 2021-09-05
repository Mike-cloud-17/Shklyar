package com.mike_cloud_17.gifs_programm.req_classes

import com.google.gson.annotations.SerializedName
import java.util.*

enum class PageSection(val value: String) {
    @SerializedName("random")
    RANDOM("random"),
    @SerializedName("top")
    TOP("top"),
    @SerializedName("latest")
    LATEST("latest"),
    @SerializedName("hot")
    HOT("hot");

    override fun toString(): String {
        return super.toString().toLowerCase(Locale.ROOT)
    }
}
