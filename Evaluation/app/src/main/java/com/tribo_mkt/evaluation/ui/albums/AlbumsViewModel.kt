package com.tribo_mkt.evaluation.ui.albums

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tribo_mkt.evaluation.data.model.Album
import com.tribo_mkt.evaluation.data.Repository
import com.tribo_mkt.evaluation.utils.Util
import com.tribo_mkt.evaluation.utils.Util.Companion.DEFAULT_TITLE
import com.tribo_mkt.evaluation.utils.Util.Companion.ERROR_GET_DATA
import kotlinx.coroutines.launch

class AlbumsViewModel(private val repository: Repository) : ViewModel() {

    var listState: Int = 0
    var userId: String = Util.DEFAULT_ID
    var userName: String = DEFAULT_TITLE
    val reposState = MutableLiveData<State>()
    val repos: LiveData<State> = reposState

    fun getAllUserAlbums(userId: String) = viewModelScope.launch {

        try {
            reposState.postValue(State.Loading)
            val call = repository.getAllUserAlbums(userId)
            if (call.isSuccessful) {
                val albumsList: List<Album>? = call.body()
                if (albumsList != null) {
                    reposState.postValue(State.SuccessGetAllUserAlbums(albumsList))
                } else {
                    reposState.postValue(State.Error(ERROR_GET_DATA))
                }
            } else {
                val error = call.errorBody()?.charStream().toString()
                reposState.postValue(State.Error(error))
            }
        } catch (e: Exception) {
            reposState.postValue(State.Error(e.message.toString()))
        }
    }

    sealed class State {
        object Loading : State()
        data class SuccessGetAllUserAlbums(val list: List<Album>) : State()
        data class Error(val error: String) : State()
    }
}
