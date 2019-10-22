package com.petits_raids.words.view

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.SearchView
import androidx.appcompat.app.AlertDialog
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.*

import com.petits_raids.words.R
import com.petits_raids.words.WordsViewModel
import com.petits_raids.words.data.Word
import com.petits_raids.words.support.WordsAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

class WordsFragment : Fragment() {

    private lateinit var viewModel: WordsViewModel
    private lateinit var observer: Observer<List<Word>>
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter1: WordsAdapter
    private lateinit var adapter2: WordsAdapter
    private lateinit var sharedPreference: SharedPreferences
    private lateinit var divider: DividerItemDecoration
    private lateinit var wordList: List<Word>

    companion object {
        private const val TAG = "WordsFragment"
        private const val USE_CARD_VIEW = "user_card_view"
        private const val SHARED_NAME = "words"
    }

    private var useCardView = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedPreference = requireContext().getSharedPreferences(SHARED_NAME, Context.MODE_PRIVATE)
        useCardView = savedInstanceState?.getBoolean(USE_CARD_VIEW, false)
            ?: sharedPreference.getBoolean(USE_CARD_VIEW, false)
        return inflater.inflate(R.layout.fragment_words, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.word_menu, menu)
        val searchView: SearchView = menu.findItem(R.id.app_bar_search).actionView as SearchView
        searchView.maxWidth = 750
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.wordList.removeObservers(viewLifecycleOwner)
                viewModel.queryWord("%$newText%").observe(viewLifecycleOwner, observer)
                return true
            }
        })
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.delete_all_item ->
                AlertDialog.Builder(requireContext())
                    .setTitle(R.string.sure_delete)
                    .setMessage(R.string.sure_delete_all)
                    .setPositiveButton(R.string.delete) { _, _ ->
                        viewModel.deleteAll()
                    }
                    .show()
            R.id.switch_layout_item -> {
                useCardView = !useCardView
                if (useCardView) {
                    recyclerView.adapter = adapter2
                    recyclerView.removeItemDecoration(divider)
                    adapter2.notifyDataSetChanged()
                } else {
                    recyclerView.adapter = adapter1
                    recyclerView.addItemDecoration(divider)
                    adapter1.notifyDataSetChanged()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
        viewModel = ViewModelProvider.AndroidViewModelFactory
            .getInstance(requireActivity().application).create(WordsViewModel::class.java)
        adapter1 = WordsAdapter(requireActivity(), viewModel, false)
        adapter2 = WordsAdapter(requireActivity(), viewModel, true)
        recyclerView = requireActivity().findViewById(R.id.words_recycler_list)
        recyclerView.itemAnimator = object : DefaultItemAnimator() {
            override fun onAnimationFinished(viewHolder: RecyclerView.ViewHolder) {
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val first = layoutManager.findFirstVisibleItemPosition()
                val last = layoutManager.findLastVisibleItemPosition()
                if (first != last)
                    for (i in first..last) {
                        val wordViewHolder =
                            recyclerView.findViewHolderForAdapterPosition(i) as WordsAdapter.ViewHolder
                        wordViewHolder.wordsId.text = (i + 1).toString()
                    }
            }
        }
        recyclerView.adapter = if (useCardView) {
            adapter2
        } else {
            divider = DividerItemDecoration(requireContext(), LinearLayout.VERTICAL)
            recyclerView.addItemDecoration(divider)
            adapter1
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        object : ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(0, START or END) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val word = wordList[viewHolder.adapterPosition]
                viewModel.deleteWord(word)
                Snackbar.make(
                    requireActivity().findViewById<CoordinatorLayout>(R.id.fragment_layout),
                    R.string.delete_word,
                    Snackbar.LENGTH_SHORT
                ).setAction(R.string.cancel) { viewModel.insertWord(word) }.show()
            }

            var icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_delete_24dp)
            var background = ColorDrawable(Color.LTGRAY)
            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                super.onChildDraw(
                    c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive
                )
                val itemView = viewHolder.itemView
                val iconMargin = (itemView.height - icon!!.intrinsicHeight) / 2
                val iconLeft: Int
                val iconRight: Int
                val iconTop: Int
                val iconBottom: Int
                val backTop: Int
                val backBottom: Int
                val backLeft: Int
                val backRight: Int
                backTop = itemView.top
                backBottom = itemView.bottom
                iconTop = itemView.top + (itemView.height - icon!!.intrinsicHeight) / 2
                iconBottom = iconTop + icon!!.intrinsicHeight
                when {
                    dX > 0 -> {
                        backLeft = itemView.left
                        backRight = itemView.left + dX.toInt()
                        background.setBounds(backLeft, backTop, backRight, backBottom)
                        iconLeft = itemView.left + iconMargin
                        iconRight = iconLeft + icon!!.intrinsicWidth
                        icon!!.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                    }
                    dX < 0 -> {
                        backRight = itemView.right
                        backLeft = itemView.right + dX.toInt()
                        background.setBounds(backLeft, backTop, backRight, backBottom)
                        iconRight = itemView.right - iconMargin
                        iconLeft = iconRight - icon!!.intrinsicWidth
                        icon!!.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                    }
                    else -> {
                        background.setBounds(0, 0, 0, 0)
                        icon!!.setBounds(0, 0, 0, 0)
                    }
                }
                background.draw(c)
                icon!!.draw(c)
            }
        }) {}.attachToRecyclerView(recyclerView)
        observer = Observer { words ->
            Log.d(TAG, words.toString())
            val temp = recyclerView.adapter!!.itemCount
            if (temp != words.size) {
                adapter1.submitList(words)
                adapter2.submitList(words)
            }
            wordList = words
        }
        viewModel.wordList.observe(viewLifecycleOwner, observer)
        val fab: FloatingActionButton = requireActivity().findViewById(R.id.add_fab)
        fab.setOnClickListener {
            val navController = Navigation.findNavController(it)
            navController.navigate(R.id.action_wordsFragment_to_addFragment)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(USE_CARD_VIEW, useCardView)
        super.onSaveInstanceState(outState)
    }

    override fun onStop() {
        super.onStop()
        val editor = sharedPreference.edit()
        editor.putBoolean(USE_CARD_VIEW, useCardView)
        editor.apply()
    }
}
