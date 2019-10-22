package com.petits_raids.words.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.petits_raids.words.R
import com.petits_raids.words.WordsViewModel
import com.petits_raids.words.data.Word

/**
 * A simple [Fragment] subclass.
 */
class AddFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val addEng: TextView = requireActivity().findViewById(R.id.eng_add_word)
        val addCn: TextView = requireActivity().findViewById(R.id.cn_add_word)
        val addBtn: Button = requireActivity().findViewById(R.id.add_word_btn)
        val textWatcher: TextWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                addBtn.isClickable =
                    !(addCn.text.toString().trim() == "" && addEng.text.toString().trim() == "")
            }

        }
        addEng.addTextChangedListener(textWatcher)
        addCn.addTextChangedListener(textWatcher)
        val viewModel: WordsViewModel = ViewModelProvider.AndroidViewModelFactory
            .getInstance(requireActivity().application).create(WordsViewModel::class.java)
        addBtn.setOnClickListener {
            viewModel.insertWord(
                Word(
                    addEng.text.toString().trim(),
                    addCn.text.toString().trim()
                )
            )
            val navController: NavController = Navigation.findNavController(it)
            navController.navigateUp()
        }
    }
}
