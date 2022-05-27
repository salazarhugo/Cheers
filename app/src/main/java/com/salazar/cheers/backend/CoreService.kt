package com.salazar.cheers.backend

import com.google.android.gms.common.internal.safeparcel.SafeParcelable
import com.salazar.cheers.data.entities.Story
import com.salazar.cheers.internal.Event
import com.salazar.cheers.internal.Post
import com.salazar.cheers.internal.User
import okhttp3.ResponseBody
import retrofit2.http.*


interface CoreService {

    @GET("users/search/{query}")
    suspend fun searchUsers(
        @Path("query") query: String,
    ): List<User>

    @GET("events")
    suspend fun getEvents(
        @Query("page") skip: Int,
        @Query("pageSize") pageSize: Int,
    ): List<Event>

    @GET("event/feed")
    suspend fun getEventFeed(
        @Query("page") skip: Int,
        @Query("pageSize") pageSize: Int,
    ): List<Event>

    @GET("posts/feed")
    suspend fun postFeed(
        @Query("page") skip: Int,
        @Query("pageSize") pageSize: Int,
    ): List<Post>

    @GET("stories/feed")
    suspend fun storyFeed(
        @Query("page") skip: Int,
        @Query("pageSize") pageSize: Int,
    ): List<Story>

    @POST("posts/create")
    suspend fun createPost(
        @Body() post: Post,
    )

    @POST("stories/create")
    suspend fun createStory(
        @Body() story: Story,
    )

    @POST("users/create")
    suspend fun createUser(
        @Body() user: User,
    ): User

    @POST("event/create")
    suspend fun createEvent(
        @Body() event: Event,
    )

    @POST("event/update")
    suspend fun updateEvent(
        @Body() event: Event,
    )

    @POST("users/tokens/{token}")
    suspend fun addRegistrationToken(
        @Path("token") token: String,
    )

    @GET("followers/list")
    suspend fun followersList(
        @Query("userIdOrUsername") userIdOrUsername: String,
    ): List<User>

    @GET("following/list")
    suspend fun followingList(
        @Query("userIdOrUsername") userIdOrUsername: String,
    ): List<User>

    @GET("users/{userIdOrUsername}")
    suspend fun getUser(
        @Path("userIdOrUsername") userIdOrUsername: String,
    ): User

    @POST("users/{userId}/block")
    suspend fun blockUser(
        @Path("userId") userId: String,
    )

    @POST("stories/{storyId}/seen")
    suspend fun seenStory(
        @Path("storyId") storyId: String,
    )

    @POST("posts/{postId}/delete")
    suspend fun deletePost(
        @Path("postId") postId: String,
    )

    @POST("stories/{storyId}/delete")
    suspend fun deleteStory(
        @Path("storyId") storyId: String,
    )

    @POST("event/delete")
    suspend fun deleteEvent(
        @Query("eventId") eventId: String,
    )

    @POST("follow")
    suspend fun followUser(
        @Query("username") username: String,
    )

    @POST("unfollow")
    suspend fun unfollowUser(
        @Query("username") username: String,
    )

    @POST("posts/{postId}/like")
    suspend fun likePost(
        @Path("postId") postId: String,
    )

    @POST("posts/{postId}/unlike")
    suspend fun unlikePost(
        @Path("postId") postId: String,
    )

    @POST("event/interest")
    suspend fun interestEvent(
        @Query("eventId") eventId: String,
    )

    @POST("event/uninterest")
    suspend fun uninterestEvent(
        @Query("eventId") eventId: String,
    )

    @GET("locations")
    suspend fun getLocations(): ResponseBody

    @POST("updateLocation")
    suspend fun updateLocation(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
    )

    companion object {
        const val BASE_URL = "https://rest-api-r3a2dr4u4a-nw.a.run.app"
    }
}