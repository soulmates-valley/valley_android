package com.soulmates.valley.ui.noti

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.soulmates.valley.base.BaseViewModel
import com.soulmates.valley.data.model.noti.NotiHistory
import com.soulmates.valley.data.repository.noti.NotiRepository
import com.soulmates.valley.util.extension.with
import timber.log.Timber

class NotiViewModel(private val notiRepository: NotiRepository) : BaseViewModel(){

    private val _notiHistoryList = MutableLiveData<ArrayList<NotiHistory>>()
    val notiHistoryList: LiveData<ArrayList<NotiHistory>> get() = _notiHistoryList

    fun getNotiHistory(){
        addDisposable(disposable = notiRepository.getNotiHistory()
            .with()
            .subscribe({
                if(it.code==200) _notiHistoryList.postValue(it.data)
            }){
                Timber.tag("getNotiHistory 통신 실패 error : ").d(it)
            })
    }
}