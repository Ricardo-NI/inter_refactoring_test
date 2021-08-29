package com.tribo_mkt.evaluation.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.tribo_mkt.evaluation.R
import com.tribo_mkt.evaluation.databinding.FragmentHomeBinding
import com.tribo_mkt.evaluation.databinding.UserItemRowBinding
import com.tribo_mkt.evaluation.data.model.User
import com.tribo_mkt.evaluation.ui.MainActivity
import com.tribo_mkt.evaluation.ui.MainActivity.Companion.hasInternetAvailable
import com.tribo_mkt.evaluation.utils.hideRefreshingBarIfIsOn
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.viewbinding.BindableItem
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class HomeFragment : Fragment() {

    private val homeViewModel: HomeViewModel by viewModel()
    private val progressBar by lazy { (activity as MainActivity).progressBar }
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var linearLayoutManager: LinearLayoutManager
    private var groupAdapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        linearLayoutManager = LinearLayoutManager(requireContext())
        setupRecyclerView()
        if(hasInternetAvailable){
            viewModelObserver()
        }

        //atualizar os dados no movimento de swipe da lista para baixo.
        binding.swipeContainerHome.setOnRefreshListener {
            if(hasInternetAvailable){
                viewModelObserver()
            }else{
                binding.swipeContainerHome.isRefreshing = false
            }
        }
    }

    fun viewModelObserver() {
            homeViewModel.getAllUsers()
            homeViewModel.repos.observe(viewLifecycleOwner) { state ->
                when (state) {
                    is HomeViewModel.State.Loading -> progressBar.show()
                    is HomeViewModel.State.SuccessGetAllUsers -> populateAdapter(state.list)
                    is HomeViewModel.State.Error -> setError(state.error)
                    else -> progressBar.dismiss()
                }
            }
        hideRefreshingBarIfIsOn(binding.swipeContainerHome)
    }

    private fun populateAdapter(list: List<User>) {
        if (list.isNotEmpty()) {
            groupAdapter.apply { addAll(list.toUserItem()) }
            if (homeViewModel.listState != 0) {
                linearLayoutManager.scrollToPositionWithOffset(homeViewModel.listState, 0)
            }
        }
        progressBar.dismiss()
    }

    private fun List<User>.toUserItem(): List<UserItem> {
        return this.map {
            UserItem(it) { user: User, postsButtonClicked: Boolean, position: Int ->
                userItemClicked(user, postsButtonClicked, position)
            }
        }
    }

    inner class UserItem(private val user: User, private val clickListener:
            (User, Boolean, Int) -> Unit) : BindableItem<UserItemRowBinding>() {

        override fun bind(viewBinding: UserItemRowBinding, position: Int) {

            val name = user.name.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
            }

            viewBinding.txvSymbol.text = name.substring(0, 2)
            viewBinding.txvName.text = name
            viewBinding.txvUsername.text = user.username
            viewBinding.txvEmail.text = user.email
            viewBinding.txvPhone.text = user.phone
            viewBinding.btnAlbums.setOnClickListener {
                clickListener(user, false, position)
            }
            viewBinding.btnPosts.setOnClickListener {
                clickListener(user, true, position)
            }
        }
        override fun getLayout(): Int = R.layout.user_item_row
        override fun initializeViewBinding(view: View):
                UserItemRowBinding = UserItemRowBinding.bind(view)
    }

    private fun userItemClicked(user: User, postsButtonClicked: Boolean, position: Int) {
        if (hasInternetAvailable) {
            homeViewModel.listState = position
            val direction = if (postsButtonClicked) {

                val fragmentTitle = "${getString(R.string.title_posts)} ${user.name}"
                HomeFragmentDirections.actionNavHomeToNavPosts(
                    userId = user.id, title = fragmentTitle, userName = user.name
                )
            } else {
                val fragmentTitle = "${getString(R.string.title_albums)} ${user.name}"
                HomeFragmentDirections.actionNavHomeToNavAlbums(
                    userId = user.id, title = fragmentTitle, userName = user.name
                )
            }
            findNavController().navigate(direction)
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerUsers.apply {
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