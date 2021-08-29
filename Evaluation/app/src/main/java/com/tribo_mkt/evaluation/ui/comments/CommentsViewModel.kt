package com.tribo_mkt.evaluation.ui.comments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tribo_mkt.evaluation.data.model.Comment
import com.tribo_mkt.evaluation.data.Repository
import com.tribo_mkt.evaluation.utils.Util.Companion.DEFAULT_ID
import com.tribo_mkt.evaluation.utils.Util.Companion.ERROR_GET_DATA
import kotlinx.coroutines.launch

class CommentsViewModel(private val repository: Repository) : ViewModel() {

    var postId: String = DEFAULT_ID
    val reposState = MutableLiveData<State>()
    val repos: LiveData<State> = reposState

    fun getAllUserPostsComments(postId: String) = viewModelScope.launch {

        try {
            reposState.postValue(State.Loading)
            val call = repository.getAllPostComments(postId)
            if (call.isSuccessful) {
                val commentsList: List<Comment>? = call.body()
                if (commentsList != null) {
                    reposState.postValue(State.SuccessGetAllUserComments(commentsList))
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
        data class SuccessGetAllUserComments(val list: List<Comment>) : State()
        data class Error(val error: String) : State()
    }
}
