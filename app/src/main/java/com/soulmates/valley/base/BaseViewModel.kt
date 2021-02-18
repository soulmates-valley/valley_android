package com.soulmates.valley.base

import androidx.lifecycle.ViewModel
import com.soulmates.valley.ValleyApplication
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

open class BaseViewModel : ViewModel(){

    val context = ValleyApplication.getGlobalApplicationContext()
    private val compositeDisposable = CompositeDisposable()

    fun addDisposable(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }
}
