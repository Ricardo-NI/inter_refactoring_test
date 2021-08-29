package com.tribo_mkt.evaluation.ui.photos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tribo_mkt.evaluation.data.model.Photo
import com.tribo_mkt.evaluation.data.Repository
import com.tribo_mkt.evaluation.utils.Util.Companion.DEFAULT_ID
import com.tribo_mkt.evaluation.utils.Util.Companion.ERROR_GET_DATA
import kotlinx.coroutines.launch

class PhotosViewModel(private val repository: Repository) : ViewModel() {

    var listState: Int = 0
    var albumId: String = DEFAULT_ID
    val reposState = MutableLiveData<State>()
    val repos: LiveData<State> = reposState

    fun getAllUserPhotos(albumId: String) = viewModelScope.launch {

        try {
            reposState.postValue(State.Loading)
            val call = repository.getAllUserPhotos(albumId)
            if (call.isSuccessful) {
                val photosList: List<Photo>? = call.body()
                if (photosList != null) {
                    reposState.postValue(State.SuccessGetAllUserPhotos(photosList))
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
        data class SuccessGetAllUserPhotos(val list: List<Photo>) : State()
        data class Error(val error: String) : State()
    }
}