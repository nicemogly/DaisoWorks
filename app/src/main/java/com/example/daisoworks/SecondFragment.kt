package com.example.daisoworks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.daisoworks.databinding.FragmentSecondBinding

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var _binding: FragmentSecondBinding? = null
    private  var Ttitle : String = ""

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)

        binding.ttt.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }



        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        sharedViewModel.getData().observe(viewLifecycleOwner, Observer {
            Ttitle = it
            (activity as AppCompatActivity).supportActionBar?.setTitle(Ttitle)
        })


        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)



        /*   (activity as AppCompatActivity).supportActionBar?.setTitle("test")*/
        /*  binding.buttonSecond.setOnClickListener {
              findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
          }*/
    }
 //

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}