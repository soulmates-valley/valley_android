package com.soulmates.valley.data.model.search

import com.google.gson.annotations.SerializedName

data class SearchTag(
    val key: String,
    @SerializedName("doc_count")
    val postCount: Int
)