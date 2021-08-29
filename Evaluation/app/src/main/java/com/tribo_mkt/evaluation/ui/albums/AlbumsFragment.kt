package com.tribo_mkt.evaluation.ui.albums

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.tribo_mkt.evaluation.R
import com.tribo_mkt.evaluation.databinding.AlbumItemRowBinding
import com.tribo_mkt.evaluation.databinding.FragmentAlbumsBinding
import com.tribo_mkt.evaluation.data.model.Album
import com.tribo_mkt.evaluation.ui.MainActivity
import com.tribo_mkt.evaluation.ui.MainActivity.Companion.hasInternetAvailable
import com.tribo_mkt.evaluation.utils.Util
import com.tribo_mkt.evaluation.utils.hideRefreshingBarIfIsOn
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.viewbinding.BindableItem
import org.koin.androidx.viewmodel.ext.android.viewModel

class AlbumsFragment : Fragment() {

    private val albumsViewModel: AlbumsViewModel by viewModel()
    private val progressBar by lazy { (activity as MainActivity).progressBar }
    private val args: AlbumsFragmentArgs by navArgs()
    private var _binding: FragmentAlbumsBinding? = null
    private val binding get() = _binding!!
    private lateinit var linearLayoutManager: LinearLayoutManager
    private var groupAdapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAlbumsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        albumsViewModel.userId = args.userId ?: Util.DEFAULT_ID
        albumsViewModel.userName = args.userName
        linearLayoutManager = LinearLayoutManager(requireContext())
        setupRecyclerView()
        if(hasInternetAvailable) {
            viewModelObserver()
        }

        binding.swipeContainerAlbums.setOnRefreshListener {
            if(hasInternetAvailable){
                viewModelObserver()
            }else{
                binding.swipeContainerAlbums.isRefreshing = false
            }
        }
    }

    private fun viewModelObserver() {
        val userId = albumsViewModel.userId
        if (userId != Util.DEFAULT_ID) {
            albumsViewModel.getAllUserAlbums(userId)
            albumsViewModel.repos.observe(viewLifecycleOwner) { state ->
                when (state) {
                    is AlbumsViewModel.State.Loading -> progressBar.show()
                    is AlbumsViewModel.State.SuccessGetAllUserAlbums -> populateAdapter(state.list)
                    is AlbumsViewModel.State.Error -> setError(state.error)
                    else -> progressBar.dismiss()
                }
            }
        }
        hideRefreshingBarIfIsOn(binding.swipeContainerAlbums)
    }

    private fun populateAdapter(list: List<Album>){
        if (list.isNotEmpty()) {
            groupAdapter.apply { addAll(list.toAlbumItem()) }
            if (albumsViewModel.listState != 0) {
                linearLayoutManager.scrollToPositionWithOffset(
                    albumsViewModel.listState, 0
                )
            }
        }
        progressBar.dismiss()
    }

    private fun List<Album>.toAlbumItem() : List<AlbumItem>{
        return this.map { AlbumItem(it) { album : Album, position: Int -> albumItemClicked(album, position) } }
    }

    inner class AlbumItem(private val album: Album, private val clickListener: (Album, Int) -> Unit
    ): BindableItem<AlbumItemRowBinding>(){

        override fun bind(viewBinding: AlbumItemRowBinding, position: Int) {
            viewBinding.txvAlbumTitle.text = album.title
            viewBinding.layoutAlbum.setOnClickListener {
                clickListener(album, position)
            }
        }
        override fun getLayout(): Int = R.layout.album_item_row
        override fun initializeViewBinding(view: View): AlbumItemRowBinding {
            return AlbumItemRowBinding.bind(view)
        }
    }

    private fun albumItemClicked(albumItem: Album, position: Int){
        if(hasInternetAvailable){
            albumsViewModel.listState = position
            val fragmentTitle = "${getString(R.string.title_photos)} ${albumsViewModel.userName}"
            val direction = AlbumsFragmentDirections.actionNavAlbumsToNavPhotos(albumId = albumItem.id, title = fragmentTitle)
            findNavController().navigate(direction)
        }
    }

    private fun setupRecyclerView(){
        binding.recyclerAlbums.apply {
            setHasFixedSize(true)
            layoutManager = linearLayoutManager
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