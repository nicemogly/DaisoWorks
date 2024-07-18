package com.example.daisoworks.ui.test1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.daisoworks.databinding.FragmentTest1Binding
import com.example.daisoworks.ui.test1.Test1ViewModel

class TestFragment : Fragment() {

    private var _binding: FragmentTest1Binding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val test1ViewModel =
            ViewModelProvider(this).get(Test1ViewModel::class.java)

        _binding = FragmentTest1Binding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textTest1
        test1ViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}