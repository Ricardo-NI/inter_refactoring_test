package com.tribo_mkt.evaluation.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tribo_mkt.evaluation.data.Repository
import com.tribo_mkt.evaluation.data.model.User
import com.tribo_mkt.evaluation.utils.Util.Companion.ERROR_GET_DATA
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: Repository) : ViewModel() {

    var listState: Int = 0//guardar o estado do RecyclerView para retornar na mesma posição no retorno para o Fragment.

    val reposState = MutableLiveData<State>()
    val repos: LiveData<State> = reposState

    fun getAllUsers() = viewModelScope.launch {

        try {
            reposState.postValue(State.Loading)
            val call = repository.getAllUsers()
            if (call.isSuccessful) {
                val usersList: List<User>? = call.body()
                if (usersList != null) {
                    reposState.postValue(State.SuccessGetAllUsers(usersList))
                } else {
                    reposState.postValue(State.Error(ERROR_GET_DATA))
                }
            } else {
                val error = call.errorBody()?.charStream().toString()
                reposState.postValue(State.Error(error))
            }
        }catch (e: Exception){
            reposState.postValue(State.Error(e.message.toString()))
        }

    }

    sealed class State {
        object Loading : State()
        data class SuccessGetAllUsers(val list: List<User>) : State()
        data class Error(val error: String) : State()
    }
}
