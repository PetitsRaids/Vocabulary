package com.example.words.support

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

import com.example.words.R
import com.example.words.WordsViewModel
import com.example.words.data.Word

class WordsAdapter(
    private val mContext: Context,
    private val viewModel: WordsViewModel,
    private val useCardView: Boolean
) : ListAdapter<Word, WordsAdapter.ViewHolder>(object : DiffUtil.ItemCallback<Word>() {
    override fun areItemsTheSame(oldItem: Word, newItem: Word): Boolean {
        println("oldItem.is = ${oldItem.id}, newItem.is = ${newItem.id}")
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Word, newItem: Word): Boolean {
        println("${oldItem.eng} and ${newItem.eng}")
        return oldItem == newItem
    }

}) {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var wordsId: TextView = view.findViewById(R.id.words_id)
        var english: TextView = view.findViewById(R.id.eng_words)
        var chinese: TextView = view.findViewById(R.id.cn_meaning)
        var showMeaning: Switch = view.findViewById(R.id.switch1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = if (useCardView) {
            LayoutInflater.from(mContext).inflate(R.layout.item_words_card, parent, false)
        } else {
            LayoutInflater.from(mContext).inflate(R.layout.item_words, parent, false)
        }
        val holder = ViewHolder(view)
        holder.itemView.setOnClickListener {
            val uri =
                Uri.parse("https://m.youdao.com/dict?le=eng&q=" + holder.english.text.toString())
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = uri
            mContext.startActivity(intent)
        }
        holder.showMeaning.setOnCheckedChangeListener { _, isChecked ->
            val word = holder.itemView.getTag(R.id.word_tag) as Word
            if (isChecked) {
                word.isShowMeaning = true
                holder.chinese.visibility = View.VISIBLE
            } else {
                word.isShowMeaning = false
                holder.chinese.visibility = View.GONE
            }
            viewModel.updateWord(word)
        }
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val word = getItem(position)
        holder.itemView.setTag(R.id.word_tag, word)
        holder.english.text = word.eng
        holder.chinese.text = word.meaning
        holder.wordsId.text = (position + 1).toString()
        if (word.isShowMeaning) {
            holder.chinese.visibility = View.VISIBLE
            holder.showMeaning.isChecked = true
        } else {
            holder.chinese.visibility = View.GONE
            holder.showMeaning.isChecked = false
        }
    }

    override fun onViewAttachedToWindow(holder: ViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.wordsId.text = (holder.layoutPosition + 1).toString()
    }

}
