package com.keeghan.firenote

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.keeghan.firenote.databinding.FragmentColorPickerBinding


class ColorPickerFragment : DialogFragment() {
    private var _binding: FragmentColorPickerBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        dialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        _binding = FragmentColorPickerBinding.inflate(inflater, container, false)

        binding.transparentBtn.setOnClickListener { updateNote(Constants.COLOR_TRANSPARENT) }
        binding.redBtn.setOnClickListener { updateNote(Constants.COLOR_RED) }
        binding.blueBtn.setOnClickListener { updateNote(Constants.COLOR_BLUE) }
        binding.orangeBtn.setOnClickListener { updateNote(Constants.COLOR_ORANGE) }
        binding.violetBtn.setOnClickListener { updateNote(Constants.COLOR_VIOLET) }
        binding.greenBtn.setOnClickListener { updateNote(Constants.COLOR_GREEN) }
        binding.brownBtn.setOnClickListener { updateNote(Constants.COLOR_BROWN) }
        binding.darkGreyBtn.setOnClickListener { updateNote(Constants.COLOR_DARK_GREY) }
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            ColorPickerFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateNote(color: String) {
        (activity as MainActivity).updateNoteColor(color)
        (activity as MainActivity).closeActionMode()
        dismiss()
    }
}