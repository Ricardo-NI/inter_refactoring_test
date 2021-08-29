package com.tribo_mkt.evaluation.ui.comments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.tribo_mkt.evaluation.R
import com.tribo_mkt.evaluation.databinding.CommentItemRowBinding
import com.tribo_mkt.evaluation.databinding.FragmentCommentsBinding
import com.tribo_mkt.evaluation.data.model.Comment
import com.tribo_mkt.evaluation.ui.MainActivity
import com.tribo_mkt.evaluation.ui.MainActivity.Companion.hasInternetAvailable
import com.tribo_mkt.evaluation.utils.Util.Companion.DEFAULT_ID
import com.tribo_mkt.evaluation.utils.hideRefreshingBarIfIsOn
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.viewbinding.BindableItem
import org.koin.androidx.viewmodel.ext.android.viewModel

class CommentsFragment : Fragment() {

    private val commentsViewModel: CommentsViewModel by viewModel()
    private val progressBar by lazy { (activity as MainActivity).progressBar }
    private var _binding: FragmentCommentsBinding? = null
    private val args: CommentsFragmentArgs by navArgs()
    private val binding get() = _binding!!
    private var groupAdapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCommentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        commentsViewModel.postId = args.postId ?: DEFAULT_ID
        setupRecyclerView()
        if(hasInternetAvailable) {
            viewModelObserver()
        }

        binding.swipeContainerComments.setOnRefreshListener {
            if(hasInternetAvailable){
                viewModelObserver()
            }else{
                binding.swipeContainerComments.isRefreshing = false
            }
        }
    }

    private fun viewModelObserver() {
        val postId = commentsViewModel.postId
        if (postId != DEFAULT_ID) {
            commentsViewModel.getAllUserPostsComments(postId)
            commentsViewModel.repos.observe(viewLifecycleOwner) { state ->
                when (state) {
                    is CommentsViewModel.State.Loading -> progressBar.show()
                    is CommentsViewModel.State.SuccessGetAllUserComments -> populateAdapter(state.list)
                    is CommentsViewModel.State.Error -> setError(state.error)
                    else -> progressBar.dismiss()
                }
            }
        }
        hideRefreshingBarIfIsOn(binding.swipeContainerComments)
    }

    private fun populateAdapter(list: List<Comment>){
        if (list.isNotEmpty()) {
            groupAdapter.apply {addAll(list.toCommentItem())}
        }
        progressBar.dismiss()
    }

    private fun List<Comment>.toCommentItem() : List<CommentItem>{
        return this.map { CommentItem(it) }
    }

    inner class CommentItem(private val comment: Comment): BindableItem<CommentItemRowBinding>(){

        override fun bind(viewBinding: CommentItemRowBinding, position: Int) {
            viewBinding.txvTitle.text = comment.name
            viewBinding.txvComment.text = comment.body
        }
        override fun getLayout(): Int = R.layout.comment_item_row
        override fun initializeViewBinding(view: View): CommentItemRowBinding {
            return CommentItemRowBinding.bind(view)
        }
    }

    private fun setupRecyclerView(){
        binding.recyclerComments.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = groupAdapter
        }
    }

    private fun setError(error: String){
        progressBar.dismiss()
        (activity as MainActivity).setError(error)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

