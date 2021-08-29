package com.tribo_mkt.evaluation.ui.posts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tribo_mkt.evaluation.data.model.Comment
import com.tribo_mkt.evaluation.data.model.Post
import com.tribo_mkt.evaluation.data.Repository
import com.tribo_mkt.evaluation.utils.Util.Companion.DEFAULT_ID
import com.tribo_mkt.evaluation.utils.Util.Companion.DEFAULT_TITLE
import com.tribo_mkt.evaluation.utils.Util.Companion.ERROR_GET_DATA
import kotlinx.coroutines.launch

class PostsViewModel(private val repository: Repository) : ViewModel() {

    var listState: Int = 0
    var userId: String = DEFAULT_ID
    var userName: String = DEFAULT_TITLE
    val reposState = MutableLiveData<State>()
    val repos: LiveData<State> = reposState

    fun getAllUserPosts(userId: String) = viewModelScope.launch {

        try {
            reposState.postValue(State.Loading)
            val call = repository.getAllUserPosts(userId)
            if (call.isSuccessful) {
                val postsList: List<Post>? = call.body()
                if (postsList != null) {
                    reposState.postValue(State.SuccessGetAllUserPosts(postsList))
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

    fun getAllUserPostsComments(userId: String) = viewModelScope.launch {

        reposState.postValue(State.Loading)
        val call = repository.getAllUserPostsComments(userId)
        if (call.isSuccessful) {
            val commentsList: List<Comment>? = call.body()
            if (commentsList != null) {
                reposState.postValue(State.SuccessGetAllUserComments(commentsList))
            }else {
                reposState.postValue(State.Error(ERROR_GET_DATA))
            }
        } else {
            val error = call.errorBody()?.charStream().toString()
            reposState.postValue(State.Error(error))
        }
    }

    sealed class State {
        object Loading : State()
        data class SuccessGetAllUserPosts(val list: List<Post>) : State()
        data class SuccessGetAllUserComments(val list: List<Comment>) : State()
        data class Error(val error: String) : State()
    }
}

