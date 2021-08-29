package com.tribo_mkt.evaluation.data

import com.tribo_mkt.evaluation.data.model.*
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface APIService {

    @GET("users")
    suspend fun getAllUsers(): Response<List<User>>

    @GET("posts")
    suspend fun getAllUserPosts(@Query("userId") userId: String): Response<List<Post>>

    @GET("comments")
    suspend fun getAllUserPostsComments(@Query("userId") userId: String): Response<List<Comment>>

    @GET("comments")
    suspend fun getAllPostComments(@Query("postId") postId: String): Response<List<Comment>>

    @GET("albums")
    suspend fun getAllUserAlbums(@Query("userId") userId: String): Response<List<Album>>

    @GET("photos")
    suspend fun getAllUserPhotos(@Query("albumId") albumId: String): Response<List<Photo>>

}