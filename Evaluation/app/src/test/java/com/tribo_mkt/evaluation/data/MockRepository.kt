package com.tribo_mkt.evaluation.data

import com.tribo_mkt.evaluation.data.model.*
import retrofit2.Response

class MockRepository : Repository {

    lateinit var getAllUsersResponse: Response<List<User>>
    lateinit var getAllUserPostsResponse: Response<List<Post>>
    lateinit var getAllUserPostsCommentsResponse: Response<List<Comment>>
    lateinit var getAllPostCommentsResponse: Response<List<Comment>>
    lateinit var getAllUserAlbumsResponse: Response<List<Album>>
    lateinit var getAllUserPhotosResponse: Response<List<Photo>>

    override suspend fun getAllUsers(): Response<List<User>> = getAllUsersResponse

    override suspend fun getAllUserPosts(value: String): Response<List<Post>> = getAllUserPostsResponse

    override suspend fun getAllUserPostsComments(value: String): Response<List<Comment>> = getAllUserPostsCommentsResponse

    override suspend fun getAllPostComments(value: String): Response<List<Comment>> = getAllPostCommentsResponse

    override suspend fun getAllUserAlbums(value: String): Response<List<Album>> = getAllUserAlbumsResponse

    override suspend fun getAllUserPhotos(value: String): Response<List<Photo>> = getAllUserPhotosResponse
}