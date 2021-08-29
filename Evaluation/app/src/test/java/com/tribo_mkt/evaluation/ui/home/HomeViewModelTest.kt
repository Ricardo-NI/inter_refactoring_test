package com.tribo_mkt.evaluation.ui.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.tribo_mkt.evaluation.data.MockRepository
import com.tribo_mkt.evaluation.data.model.User
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
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Response


@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class HomeViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    private lateinit var reposObserver: Observer<HomeViewModel.State>
    private lateinit var viewModel: HomeViewModel


    @Before
    fun setup() {
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when view model getAllUsers get success then sets repos LiveData`() {

        val usersList = populateUserList()
        val resultSuccess = Response.success(usersList)

        val repository = MockRepository()
        repository.getAllUsersResponse = resultSuccess

        viewModel = HomeViewModel(repository)
        viewModel.reposState.observeForever(reposObserver)
        viewModel.getAllUsers()

        verify(reposObserver).onChanged(HomeViewModel.State.SuccessGetAllUsers(usersList))//listOf()
    }

    @Test
    fun `when view model getAllUsers is unsuccessfully then sets repos Error`() {

        val aResponse: Response<List<User>> = Response.error(
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
        repository.getAllUsersResponse = aResponse

        viewModel = HomeViewModel(repository)
        viewModel.reposState.observeForever(reposObserver)
        viewModel.getAllUsers()

        val error = aResponse.errorBody()?.charStream().toString()

        verify(reposObserver).onChanged(HomeViewModel.State.Error(error))
    }

    private fun populateUserList(): List<User> {
        val users = mutableListOf<User>()
        for (i in 1..3) {
            val user = User("$i", "name$i", "username$i", "email@teste.com$i", "(55)99000000$i")
            users.add(user)
        }
        return users
    }
}




