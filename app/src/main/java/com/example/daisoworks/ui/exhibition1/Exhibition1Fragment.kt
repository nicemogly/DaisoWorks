package com.example.daisoworks.ui.exhibition1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.daisoworks.databinding.FragmentExhibition1Binding


class Exhibition1Fragment  : Fragment() {

    private var _binding: FragmentExhibition1Binding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExhibition1Binding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }
}