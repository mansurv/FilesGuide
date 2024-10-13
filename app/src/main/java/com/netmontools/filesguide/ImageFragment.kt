package com.netmontools.filesguide

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.netmontools.filesguide.databinding.FragmentImageBinding

class ImageFragment : Fragment() {

    private var _binding: FragmentImageBinding? = null


    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentImageBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val imageView: SubsamplingScaleImageView =
            binding.root.findViewById(R.id.imageView)

        val path = requireArguments().getString("arg")

        binding.imageView.setImage(ImageSource.uri(path!!))

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}