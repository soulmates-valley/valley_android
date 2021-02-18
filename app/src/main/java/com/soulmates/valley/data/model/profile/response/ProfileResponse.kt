package com.soulmates.valley.data.model.profile.response

import com.soulmates.valley.data.model.profile.Profile


data class ProfileResponse(
    val code: Int,
    val data: Profile?
)