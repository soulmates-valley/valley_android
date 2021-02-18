package com.soulmates.valley.data.model.search.response

import com.soulmates.valley.data.model.search.SearchTag


data class SearchTagResponse(
    val code: Int,
    val data: List<SearchTag>
)
