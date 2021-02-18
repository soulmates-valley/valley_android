package com.soulmates.valley.data.model.search.response

import com.soulmates.valley.data.model.search.SearchUser

data class SearchUserResponse(
    val code: Int,
    val data: List<SearchUser>
)