package com.soulmates.valley.di

import com.soulmates.valley.ui.chat.ChatViewModel
import com.soulmates.valley.ui.friend.FriendViewModel
import com.soulmates.valley.ui.feed.FeedViewModel
import com.soulmates.valley.ui.noti.NotiViewModel
import com.soulmates.valley.ui.post.PostViewModel
import com.soulmates.valley.ui.profile.ProfileViewModel
import com.soulmates.valley.ui.search.SearchViewModel
import com.soulmates.valley.ui.signIn.SignInViewModel
import com.soulmates.valley.ui.signUp.SignUpViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { SignUpViewModel(get()) }
    viewModel { SignInViewModel(get(), get()) }
    viewModel { FeedViewModel(get(), get()) }
    viewModel { PostViewModel(get()) }
    viewModel { ChatViewModel(get(), get()) }
    viewModel { FriendViewModel(get()) }
    viewModel { SearchViewModel(get(), get(), get()) }
    viewModel { ProfileViewModel(get(), get(), get(), get()) }
    viewModel { NotiViewModel(get()) }
}