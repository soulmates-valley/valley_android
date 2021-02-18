package com.soulmates.valley.di

import com.soulmates.valley.data.repository.post.comment.CommentDataSourceFactory
import com.soulmates.valley.data.repository.feed.home.HomeFeedDataSourceFactory
import com.soulmates.valley.data.repository.feed.recommend.RecommendFeedDataSourceFactory
import com.soulmates.valley.data.repository.friend.follower.FollowerDataSourceFactory
import com.soulmates.valley.data.repository.friend.following.FollowingDataSourceFactory
import com.soulmates.valley.data.repository.profile.profilePost.ProfilePostDataSourceFactory
import com.soulmates.valley.data.repository.search.post.SearchPostDataSourceFactory
import com.soulmates.valley.data.repository.search.tag.SearchTagDetailDataSourceFactory
import org.koin.dsl.module

val dataSourceModule = module {
    factory { HomeFeedDataSourceFactory(get()) }
    factory { RecommendFeedDataSourceFactory(get()) }

    factory { SearchPostDataSourceFactory(get()) }
    factory { SearchTagDetailDataSourceFactory(get()) }

    factory { FollowingDataSourceFactory(get()) }
    factory { FollowerDataSourceFactory(get()) }

    factory { ProfilePostDataSourceFactory(get()) }
    factory { CommentDataSourceFactory(get()) }
}