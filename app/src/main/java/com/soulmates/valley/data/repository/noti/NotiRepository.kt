package com.soulmates.valley.data.repository.noti

import com.soulmates.valley.base.BaseViewModel
import com.soulmates.valley.data.model.noti.NotiHistoryResponse
import com.soulmates.valley.data.remote.ValleyService
import io.reactivex.Single

class NotiRepository(private val service: ValleyService) {
    fun getNotiHistory(): Single<NotiHistoryResponse> = service.getNotiHistory().map { it }
}