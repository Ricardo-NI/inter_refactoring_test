package com.tribo_mkt.evaluation.ui.photos

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.tribo_mkt.evaluation.data.MockRepository
import com.tribo_mkt.evaluation.data.model.Photo
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
class PhotosViewModelTest{

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    private lateinit var reposObserver: Observer<PhotosViewModel.State>
    private lateinit var viewModel: PhotosViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when view model getAllUserPhotos get success then sets repos LiveData`() {

        val objList = populateObjList()
        val resultSuccess = Response.success(objList)

        val repository = MockRepository()
        repository.getAllUserPhotosResponse = resultSuccess

        viewModel = PhotosViewModel(repository)
        viewModel.reposState.observeForever(reposObserver)
        viewModel.getAllUserPhotos("1")

        Mockito.verify(reposObserver).onChanged(PhotosViewModel.State.SuccessGetAllUserPhotos(objList))//listOf()
    }

    @Test
    fun `when view model getAllUserPhotos is unsuccessfully then sets repos Error`() {

        val aResponse: Response<List<Photo>> = Response.error(
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
        repository.getAllUserPhotosResponse= aResponse

        viewModel = PhotosViewModel(repository)
        viewModel.reposState.observeForever(reposObserver)
        viewModel.getAllUserPhotos("1")

        val error = aResponse.errorBody()?.charStream().toString()

        Mockito.verify(reposObserver).onChanged(PhotosViewModel.State.Error(error))
    }

    private fun populateObjList(): List<Photo> {
        val objList = mutableListOf<Photo>()
        for (i in 1..3) {
            val obj = Photo("$i", "$i", "title$i", "url$i", "thumbnailUrl$i")
            objList.add(obj)
        }
        return objList
    }
}