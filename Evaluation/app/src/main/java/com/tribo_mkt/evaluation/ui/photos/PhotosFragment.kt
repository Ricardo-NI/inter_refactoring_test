package com.tribo_mkt.evaluation.ui.photos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.squareup.picasso.Picasso
import com.tribo_mkt.evaluation.R
import com.tribo_mkt.evaluation.databinding.FragmentPhotosBinding
import com.tribo_mkt.evaluation.databinding.PhotoItemRowBinding
import com.tribo_mkt.evaluation.data.model.Photo
import com.tribo_mkt.evaluation.ui.MainActivity
import com.tribo_mkt.evaluation.ui.MainActivity.Companion.hasInternetAvailable
import com.tribo_mkt.evaluation.utils.Util.Companion.DEFAULT_ID
import com.tribo_mkt.evaluation.utils.hideRefreshingBarIfIsOn
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.viewbinding.BindableItem
import org.koin.androidx.viewmodel.ext.android.viewModel

class PhotosFragment : Fragment() {

    private val photosViewModel: PhotosViewModel by viewModel()
    private val progressBar by lazy { (activity as MainActivity).progressBar }
    private val args: PhotosFragmentArgs by navArgs()
    private var _binding: FragmentPhotosBinding? = null
    private val binding get() = _binding!!
    private lateinit var linearLayoutManager: GridLayoutManager
    private var groupAdapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPhotosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        photosViewModel.albumId = args.albumId ?: DEFAULT_ID
        linearLayoutManager = GridLayoutManager(requireContext(),
            2, GridLayoutManager.VERTICAL, false
        )
        setupRecyclerView()
        if (hasInternetAvailable) {
            viewModelObserver()
        }

        binding.swipeContainerPhotos.setOnRefreshListener {
            if(hasInternetAvailable){
                viewModelObserver()
            }else{
                binding.swipeContainerPhotos.isRefreshing = false
            }
        }
    }

    private fun viewModelObserver() {
        val albumId = photosViewModel.albumId
        if (albumId != DEFAULT_ID) {
            photosViewModel.getAllUserPhotos(albumId)
            photosViewModel.repos.observe(viewLifecycleOwner) { state ->
                when (state) {
                    is PhotosViewModel.State.Loading -> progressBar.show()
                    is PhotosViewModel.State.SuccessGetAllUserPhotos -> populateAdapter(state.list)
                    is PhotosViewModel.State.Error -> setError(state.error)
                    else -> progressBar.dismiss()
                }
            }
        }
        hideRefreshingBarIfIsOn(binding.swipeContainerPhotos)
    }

    private fun populateAdapter(list: List<Photo>) {
        if (list.isNotEmpty()) {
            groupAdapter.apply {
                addAll(list.toPhotoItem())
            }
            if (photosViewModel.listState != 0) {
                linearLayoutManager.scrollToPositionWithOffset(
                    photosViewModel.listState, 0
                )
            }
        }
        progressBar.dismiss()
    }

    private fun List<Photo>.toPhotoItem(): List<PhotoItem> {
        return this.map { PhotoItem(it)
        { photo: Photo, position: Int -> photoItemClicked(photo, position) } }
    }

    inner class PhotoItem(private val photo: Photo, private val clickListener:
            (Photo, Int) -> Unit) : BindableItem<PhotoItemRowBinding>() {

        override fun bind(viewBinding: PhotoItemRowBinding, position: Int) {

            viewBinding.txvTitle.text = photo.title
            if (photo.thumbnailUrl.isNotEmpty()) {
                Picasso.get()
                    .load(photo.thumbnailUrl)
                    .placeholder(R.drawable.ic_baseline_downloading)
                    .error(R.drawable.ic_image_not_supported)
                    .noFade()
                    .into(viewBinding.imgPhoto)
            }
            viewBinding.cardviewPhoto.setOnClickListener {
                clickListener(photo, position)
            }
        }
        override fun getLayout(): Int = R.layout.photo_item_row
        override fun initializeViewBinding(view: View):
                PhotoItemRowBinding = PhotoItemRowBinding.bind(view)

    }

    private fun photoItemClicked(photoItem: Photo, position: Int) {
        photosViewModel.listState = position
        val direction = PhotosFragmentDirections.actionNavPhotosToNavDetailsPhoto(
            photoUrl = photoItem.url, title = photoItem.title
        )
        findNavController().navigate(direction)
    }

    private fun setupRecyclerView() {
        binding.recyclerPhotos.apply {
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