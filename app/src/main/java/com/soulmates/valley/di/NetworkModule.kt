package com.soulmates.valley.di

import com.soulmates.valley.data.remote.ValleyServiceImpl
import org.koin.dsl.module

val serviceModule = module {
    // http connection
    single { ValleyServiceImpl.service }
    // socket connection
    factory { ValleyServiceImpl.get() }
}