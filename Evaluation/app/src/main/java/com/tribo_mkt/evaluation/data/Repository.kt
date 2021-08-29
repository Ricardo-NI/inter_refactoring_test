package com.tribo_mkt.evaluation.data

import com.tribo_mkt.evaluation.data.model.*
import retrofit2.Response

interface Repository {

    suspend fun getAllUsers(): Response<List<User>>

    suspend fun getAllUserPosts(value: String): Response<List<Post>>

    suspend fun getAllUserPostsComments(value: String): Response<List<Comment>>

    suspend fun getAllPostComments(value: String): Response<List<Comment>>

    suspend fun getAllUserAlbums(value: String): Response<List<Album>>

    suspend fun getAllUserPhotos(value: String): Response<List<Photo>>
}