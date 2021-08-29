package com.tribo_mkt.evaluation.ui.posts

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.tribo_mkt.evaluation.data.MockRepository
import com.tribo_mkt.evaluation.data.model.Comment
import com.tribo_mkt.evaluation.data.model.Post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Response

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class PostsViewModelTest{

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    private lateinit var reposObserver: Observer<PostsViewModel.State>
    private lateinit var viewModel: PostsViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when view model getAllUserPosts get success then sets repos LiveData`() {

        val objList = populateObjList()
        val resultSuccess = Response.success(objList)

        val repository = MockRepository()
        repository.getAllUserPostsResponse= resultSuccess

        viewModel = PostsViewModel(repository)
        viewModel.reposState.observeForever(reposObserver)
        viewModel.getAllUserPosts("1")

        Mockito.verify(reposObserver).onChanged(PostsViewModel.State.SuccessGetAllUserPosts(objList))//listOf()
    }

    @Test
    fun `when view model getAllUserPosts is unsuccessfully then sets repos Error`() {

        val aResponse: Response<List<Post>> = Response.error(
            403,
            ResponseBody.create(
                MediaType.parse("application/json"),
                "{\n" +
                        "  \"type\": \"error\",\n" +
                        "  \"message\": \"An error has occurred.\"\n"
                        + "}"
            )
        )

        val repository = MockRepository()
        repository.getAllUserPostsResponse = aResponse

        viewModel = PostsViewModel(repository)
        viewModel.reposState.observeForever(reposObserver)
        viewModel.getAllUserPosts("1")

        val error = aResponse.errorBody()?.charStream().toString()

        Mockito.verify(reposObserver).onChanged(PostsViewModel.State.Error(error))
    }

    @Test
    fun `when view model getAllUserPostsComments get success then sets repos LiveData`() {

        val objList = populateObjList2()
        val resultSuccess = Response.success(objList)

        val repository = MockRepository()
        repository.getAllUserPostsCommentsResponse= resultSuccess

        viewModel = PostsViewModel(repository)
        viewModel.reposState.observeForever(reposObserver)
        viewModel.getAllUserPostsComments("1")

        Mockito.verify(reposObserver).onChanged(PostsViewModel.State.SuccessGetAllUserComments(objList))//listOf()
    }

    @Test
    fun `when view model getAllUserPostsComments is unsuccessfully then sets repos Error`() {

        val aResponse: Response<List<Comment>> = Response.error(
            403,
            ResponseBody.create(
                MediaType.parse("application/json"),
                "{\n" +
                        "  \"type\": \"error\",\n" +
                        "  \"message\": \"An error has occurred.\"\n"
                        + "}"
            )
        )

        val repository = MockRepository()
        repository.getAllUserPostsCommentsResponse= aResponse

        viewModel = PostsViewModel(repository)
        viewModel.reposState.observeForever(reposObserver)
        viewModel.getAllUserPostsComments("1")

        val error = aResponse.errorBody()?.charStream().toString()

        Mockito.verify(reposObserver).onChanged(PostsViewModel.State.Error(error))
    }

    private fun populateObjList(): List<Post> {
        val objList = mutableListOf<Post>()
        for (i in 1..3) {
            val obj = Post("$i", "$i", "title$i", "body$i", i)
            objList.add(obj)
        }
        return objList
    }

    private fun populateObjList2(): List<Comment> {
        val objList = mutableListOf<Comment>()
        for (i in 1..3) {
            val obj = Comment("$i", "$i", "name$i", "email$i", "body$i")
            objList.add(obj)
        }
        return objList
    }

}