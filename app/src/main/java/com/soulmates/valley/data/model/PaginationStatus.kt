package com.soulmates.valley.data.model

sealed class PaginationStatus {
    object Loading : PaginationStatus()
    object Empty : PaginationStatus()
    object NotEmpty : PaginationStatus()
    object Error: PaginationStatus()
//    data class Error(val errorStringRes: Int): PaginationStatus()
}