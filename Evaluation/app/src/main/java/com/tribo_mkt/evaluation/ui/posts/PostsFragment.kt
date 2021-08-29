package com.tribo_mkt.evaluation.ui.posts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.tribo_mkt.evaluation.R
import com.tribo_mkt.evaluation.databinding.FragmentPostsBinding
import com.tribo_mkt.evaluation.databinding.PostItemRowBinding
import com.tribo_mkt.evaluation.data.model.Comment
import com.tribo_mkt.evaluation.data.model.Post
import com.tribo_mkt.evaluation.ui.MainActivity
import com.tribo_mkt.evaluation.ui.MainActivity.Companion.hasInternetAvailable
import com.tribo_mkt.evaluation.utils.Util.Companion.DEFAULT_ID
import com.tribo_mkt.evaluation.utils.hideRefreshingBarIfIsOn
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.viewbinding.BindableItem
import org.koin.androidx.viewmodel.ext.android.viewModel

class PostsFragment : Fragment() {

    private val postsViewModel: PostsViewModel by viewModel()
    private val progressBar by lazy { (activity as MainActivity).progressBar }
    private val args: PostsFragmentArgs by navArgs()
    private var postsList = mutableListOf<Post>()
    private var commentsList = mutableListOf<Comment>()
    private var _binding: FragmentPostsBinding? = null
    private val binding get() = _binding!!
    private lateinit var linearLayoutManager: LinearLayoutManager
    private var groupAdapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPostsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postsViewModel.userId = args.userId ?: DEFAULT_ID
        postsViewModel.userName = args.userName
        linearLayoutManager = LinearLayoutManager(requireContext())
        setupRecyclerView()
        if(hasInternetAvailable) {
            viewModelObserver()
        }

        binding.swipeContainerPosts.setOnRefreshListener {
            if(hasInternetAvailable) {
                viewModelObserver()
            }else{
                binding.swipeContainerPosts.isRefreshing = false
            }
        }
    }

    private fun viewModelObserver() {
        val userId = postsViewModel.userId
        if (userId != DEFAULT_ID) {
            postsViewModel.getAllUserPosts(userId)
            postsViewModel.repos.observe(viewLifecycleOwner) { state ->
                when (state) {
                    is PostsViewModel.State.Loading -> progressBar.show()
                    is PostsViewModel.State.SuccessGetAllUserPosts -> viewModelObserverComments(state.list, userId)
                    is PostsViewModel.State.Error -> setError(state.error)
                    else -> progressBar.dismiss()
                }
            }
        }
        hideRefreshingBarIfIsOn(binding.swipeContainerPosts)
    }

    private fun viewModelObserverComments(list: List<Post>, userId: String) {
        postsList.clear()
        postsList.addAll(list)
        postsViewModel.getAllUserPostsComments(userId)
        postsViewModel.repos.observe(viewLifecycleOwner) { state ->
            when (state) {
                is PostsViewModel.State.Loading -> progressBar.show()
                is PostsViewModel.State.SuccessGetAllUserComments -> setupPostsList(state.list)
                is PostsViewModel.State.Error -> setError(state.error)
                else -> progressBar.dismiss()
            }
        }
    }

    private fun setupPostsList(list: List<Comment>) {
        commentsList.clear()
        commentsList.addAll(list)
        if (!postsList.isNullOrEmpty()) {
            if (!commentsList.isNullOrEmpty()) {
                for (item in postsList) {
                    item.comments = commentsList.filter{comment -> comment.postId == item.id}.size
                }
            }
            groupAdapter.apply { addAll(postsList.toPostItem()) }
            if (postsViewModel.listState != 0) {
                linearLayoutManager.scrollToPositionWithOffset(postsViewModel.listState, 0)
            }
        }
        progressBar.dismiss()
    }

    private fun List<Post>.toPostItem(): List<PostItem> {
        return this.map {
            PostItem(it) { post: Post, position: Int -> postItemClicked(post, position) }
        }
    }

    inner class PostItem( private val post: Post, private val clickListener:
        (Post, Int) -> Unit) : BindableItem<PostItemRowBinding>() {

        override fun bind(viewBinding: PostItemRowBinding, position: Int) {

            viewBinding.txvTitle.text = post.title
            viewBinding.txvContent.text = post.body
            viewBinding.txvCommentsCount.visibility = if (post.comments != 0) View.VISIBLE else View.GONE
            if (post.comments != 0) {
                val commentsText = "${getString(R.string.text_comments_number)} ${post.comments}"
                viewBinding.txvCommentsCount.text = commentsText
            }
            viewBinding.layoutPost.setOnClickListener {
                clickListener(post, position)
            }
        }
        override fun getLayout(): Int = R.layout.post_item_row
        override fun initializeViewBinding(view: View):
                PostItemRowBinding = PostItemRowBinding.bind(view)
    }

    private fun postItemClicked(postItem: Post, position: Int) {
        if (hasInternetAvailable) {
            postsViewModel.listState = position
            val fragmentTitle = "${getString(R.string.tilte_comments)} ${postsViewModel.userName}"
            val direction = PostsFragmentDirections.actionNavPostsToNavComments(
                postId = postItem.id, title = fragmentTitle
            )
            findNavController().navigate(direction)
        }
    }

    private fun setupRecyclerView(){
        binding.recyclerPosts.apply {
            setHasFixedSize(true)
            layoutManager = linearLayoutManager
            adapter = groupAdapter
        }
    }

    private fun setError(error: String) {
        progressBar.dismiss()
        (activity as MainActivity).setError(error)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}