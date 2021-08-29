package com.tribo_mkt.evaluation.ui.albums

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.tribo_mkt.evaluation.data.MockRepository
import com.tribo_mkt.evaluation.data.model.Album
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
class AlbumsViewModelTest{

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    private lateinit var reposObserver: Observer<AlbumsViewModel.State>
    private lateinit var viewModel: AlbumsViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when view model getAllUserAlbums get success then sets repos LiveData`() {

        val objList = populateObjList()
        val resultSuccess = Response.success(objList)

        val repository = MockRepository()
        repository.getAllUserAlbumsResponse = resultSuccess

        viewModel = AlbumsViewModel(repository)
        viewModel.reposState.observeForever(reposObserver)
        viewModel.getAllUserAlbums("1")

        Mockito.verify(reposObserver).onChanged(AlbumsViewModel.State.SuccessGetAllUserAlbums(objList))//listOf()
    }

    @Test
    fun `when view model getAllUserAlbums is unsuccessfully then sets repos Error`() {

        val aResponse: Response<List<Album>> = Response.error(
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
        repository.getAllUserAlbumsResponse = aResponse

        viewModel = AlbumsViewModel(repository)
        viewModel.reposState.observeForever(reposObserver)
        viewModel.getAllUserAlbums("1")

        val error = aResponse.errorBody()?.charStream().toString()

        Mockito.verify(reposObserver).onChanged(AlbumsViewModel.State.Error(error))
    }

    private fun populateObjList(): List<Album> {
        val objList = mutableListOf<Album>()
        for (i in 1..3) {
            val obj = Album("$i", "$i", "title$i")
            objList.add(obj)
        }
        return objList
    }
}