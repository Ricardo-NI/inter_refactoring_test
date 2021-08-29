package com.tribo_mkt.evaluation.ui.details_photo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.squareup.picasso.Picasso
import com.tribo_mkt.evaluation.R
import com.tribo_mkt.evaluation.databinding.FragmentDetailsPhotoBinding

class DetailsPhotoFragment : Fragment() {

    private val args: DetailsPhotoFragmentArgs by navArgs()
    private var _binding: FragmentDetailsPhotoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailsPhotoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val photoUrl = args.photoUrl
        photoUrl?.let{
            Picasso.get()
                .load(it)
                .placeholder(R.drawable.ic_baseline_downloading)
                .error(R.drawable.ic_image_not_supported)
                .noFade()
                .into(binding.imgDetail)
        }
        binding.txvDetailPhotoTitle.text = args.title
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}