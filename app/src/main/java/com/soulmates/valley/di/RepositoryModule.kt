package com.soulmates.valley.di

import com.soulmates.valley.data.repository.chat.ChatRepository
import com.soulmates.valley.data.repository.feed.FeedRepository
import com.soulmates.valley.data.repository.friend.FriendRepository
import com.soulmates.valley.data.repository.noti.NotiRepository
import com.soulmates.valley.data.repository.post.PostRepository
import com.soulmates.valley.data.repository.profile.ProfileRepository
import com.soulmates.valley.data.repository.search.SearchRepository
import com.soulmates.valley.data.repository.sign.SignInRepository
import com.soulmates.valley.data.repository.sign.SignUpRepository
import org.koin.dsl.module

val repositoryModule = module {
    factory { SignUpRepository(get()) }
    factory { SignInRepository(get()) }

    factory { FeedRepository(get(), get(), get()) }
    factory { PostRepository(get(), get()) }

    factory { SearchRepository(get(), get(), get()) }
    factory { FriendRepository(get(), get(), get()) }

    factory { ProfileRepository(get(), get()) }

    factory { ChatRepository(get()) }
    factory { NotiRepository(get()) }
}