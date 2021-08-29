package com.tribo_mkt.evaluation.data

import com.tribo_mkt.evaluation.data.model.*
import retrofit2.Response

class APIRepository(private val apiService: APIService) : Repository {

    override suspend fun getAllUsers(): Response<List<User>> = apiService.getAllUsers()

    override suspend fun getAllUserPosts(value: String): Response<List<Post>> = apiService.getAllUserPosts(value)

    override suspend fun getAllUserPostsComments(value: String): Response<List<Comment>> = apiService.getAllUserPostsComments(value)

    override suspend fun getAllPostComments(value: String): Response<List<Comment>> = apiService.getAllPostComments(value)

    override suspend fun getAllUserAlbums(value: String): Response<List<Album>> = apiService.getAllUserAlbums(value)

    override suspend fun getAllUserPhotos(value: String): Response<List<Photo>> = apiService.getAllUserPhotos(value)

}