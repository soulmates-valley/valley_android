package com.soulmates.valley.data.remote

import com.google.gson.JsonObject
import com.soulmates.valley.data.model.DefaultResponse
import com.soulmates.valley.data.model.chat.response.ChatLogResponse
import com.soulmates.valley.data.model.chat.response.ChatRoomListResponse
import com.soulmates.valley.data.model.feed.response.FeedResponse
import com.soulmates.valley.data.model.friend.response.FollowCountResponse
import com.soulmates.valley.data.model.friend.response.FollowListResponse
import com.soulmates.valley.data.model.comment.response.CommentListResponse
import com.soulmates.valley.data.model.comment.response.CommentResponse
import com.soulmates.valley.data.model.friend.response.KnowFollowResponse
import com.soulmates.valley.data.model.friend.response.LikeFollowResponse
import com.soulmates.valley.data.model.noti.NotiHistoryResponse
import com.soulmates.valley.data.model.post.response.PostDetailResponse
import com.soulmates.valley.data.model.profile.response.ProfilePostResponse
import com.soulmates.valley.data.model.profile.response.ProfileResponse
import com.soulmates.valley.data.model.search.response.PopularKeywordReponse
import com.soulmates.valley.data.model.search.response.SearchPostResponse
import com.soulmates.valley.data.model.search.response.SearchTagResponse
import com.soulmates.valley.data.model.search.response.SearchUserResponse
import com.soulmates.valley.data.model.user.response.EmailVerifyResponse
import com.soulmates.valley.data.model.user.response.SignInResponse
import io.reactivex.Completable
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ValleyService {
    /*
    회원가입 & 로그임
     */
    @POST("/users/verifyEmail")
    fun emailVerify(
        @Header("Content-Type") contentType: String,
        @Body body: JsonObject
    ): Single<EmailVerifyResponse>

    @POST("/users/verifyNickname")
    fun nickNameVerify(
        @Header("Content-Type") contentType: String,
        @Body body: JsonObject
    ): Single<DefaultResponse>

    @Multipart
    @POST("/users/signUp")
    fun signUp(
        @PartMap map: HashMap<String, RequestBody?>,
        @Part("interest") interest: ArrayList<Int>,
        @Part profileImg: MultipartBody.Part?
    ): Single<DefaultResponse>

    @POST("/users/signIn")
    fun signIn(
        @Header("Content-Type") contentType: String,
        @Body body: JsonObject
    ): Single<SignInResponse>

    @POST("/users/newToken")
    fun getNewToken(
        @Body body: JsonObject
    ): Call<DefaultResponse>

    @GET("/profile/logout")
    fun logout(): Completable

    /*
    피드
     */
    @GET("/feed/all")
    fun getHomeFeed(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Single<FeedResponse>

    @GET("/recommend/feed")
    fun getRecommendFeed(): Single<FeedResponse>

    /*
    게시글
     */
    @Multipart
    @POST("/post")
    fun postPosting(
        @PartMap map: HashMap<String, RequestBody?>,
        @Part("hashTag") hashTag: ArrayList<String>?,
        @Part images: List<MultipartBody.Part>?
    ): Single<DefaultResponse>

    @GET("/post")
    fun getPostDetail(
        @Header("Content-Type") contentType: String,
        @Query("postId") postId: Int
    ): Single<PostDetailResponse>

    @POST("/like")
    fun postLike(
        @Query("postId") postId: Int
    ): Single<DefaultResponse>

    @DELETE("/like")
    fun deleteLike(
        @Query("postId") postId: Int
    ): Single<DefaultResponse>

    /*
    댓글
     */
    @POST("/comment")
    fun postComment(
        @Header("Content-Type") contentType: String,
        @Body body: JsonObject
    ): Single<CommentResponse>

    @GET("/comment/all")
    fun getCommentList(
        @Query("postId") postId: Int,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Single<CommentListResponse>

    /*
    검색
     */
    @GET("/search/popular")
    fun getPopularKeyword(): Single<PopularKeywordReponse>

    @GET("/search/user")
    fun getSearchUser(
        @Header("Content-Type") contentType: String,
        @Query("q") keyword: String
    ): Single<SearchUserResponse>

    @GET("/search/feed")
    fun getSearchPost(
        @Header("Content-Type") contentType: String,
        @Query("q") keyword: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Single<SearchPostResponse>

    @GET("/search/hashTag")
    fun getSearchTag(
        @Header("Content-Type") contentType: String,
        @Query("q") keyword: String
    ): Single<SearchTagResponse>

    @GET("/search/hashTag/detail")
    fun getSearchTagDetail(
        @Header("Content-Type") contentType: String,
        @Query("q") keyword: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Single<SearchPostResponse>

    /*
    모든 친구 (팔로우 & 팔로잉)
     */
    @GET("/follow/count")
    fun getFollowCount(
        @Header("Content-Type") contentType: String,
        @Query("userId") userId: Int
    ): Single<FollowCountResponse>

    @GET("/follow/in")
    fun getFollowerList(
        @Query("userId") userId: Int,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Single<FollowListResponse>

    @GET("/follow/out")
    fun getFollowingList(
        @Query("userId") userId: Int,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Single<FollowListResponse>

    @POST("/follow/{toUserId}")
    fun postFollow(
        @Header("Content-Type") contentType: String,
        @Path("toUserId") toUserId: Int
    ): Single<DefaultResponse>

    @DELETE("/follow/{toUserId}")
    fun deleteFollow(
        @Header("Content-Type") contentType: String,
        @Path("toUserId") toUserId: Int
    ): Single<DefaultResponse>

    /*
    추천 친구
     */
    @GET("/followRecom/if")
    fun getKnowFollowList(
        @Query("userId") userId: Int
    ): Single<KnowFollowResponse>

    @GET("/followRecom/like")
    fun getLikeFollowList(
        @Query("userId") userId: Int
    ): Single<LikeFollowResponse>

    /*
    프로필
     */
    @GET("/profile")
    fun getProfileInfo(
        @Query("userId") userId: Int
    ): Single<ProfileResponse>

    @GET("/post/detail")
    fun getProfilePost(
        @Query("searchUserId") userId: Int,
        @Query("size") size: Int,
        @Query("maxPostId") lastPostId: Int
    ):Single<ProfilePostResponse>

    /*
    알림
     */
    @GET("/noti")
    fun getNotiHistory(): Single<NotiHistoryResponse>

    /*
    채팅
     */
    @GET("/chat/roomList")
    fun getChatRoomList(
        @Query("userId") userId: Int
    ): Single<ChatRoomListResponse>

    @GET("/chat/chatLog")
    fun getChatLog(
        @Query("roomName") roomName: String
    ): Single<ChatLogResponse>
}
