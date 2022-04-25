package com.keeghan.firenote

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.keeghan.firenote.databinding.NoteItemBinding
import com.keeghan.firenote.model.Note


class NoteAdapter(private val listener: OnItemClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private var noteList: ArrayList<Note> = ArrayList()

    inner class NoteViewHolder(viewBinding: NoteItemBinding) :
        RecyclerView.ViewHolder(viewBinding.root), View.OnClickListener {
        private val binding = viewBinding

        init {
            binding.root.setOnClickListener(this)
        }

        fun setNote(note: Note) {
            binding.noteBody.text = note.message
        }

        override fun onClick(p0: View?) {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                listener.onItemClick(adapterPosition)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = NoteItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NoteViewHolder(view)
    }


    //set background color
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as NoteViewHolder).setNote(noteList[position])
        //  holder.itemView.setBackgroundColor(Color.parseColor(noteList[position].color))
        when (noteList[position].color) {
            Constants.COLOR_RED -> {holder.itemView.setBackgroundResource(R.drawable.recycler_background_red)}
            Constants.COLOR_BLUE -> {holder.itemView.setBackgroundResource(R.drawable.recycler_background_blue)}
            Constants.COLOR_ORANGE -> {holder.itemView.setBackgroundResource(R.drawable.recycler_background_orange)}
            Constants.COLOR_VIOLET -> {holder.itemView.setBackgroundResource(R.drawable.recycler_background_violet)}
            Constants.COLOR_GREEN -> {holder.itemView.setBackgroundResource(R.drawable.recycler_background_green)}
            Constants.COLOR_BROWN -> {holder.itemView.setBackgroundResource(R.drawable.recycler_background_brown)}
            Constants.COLOR_DARK_GREY -> {holder.itemView.setBackgroundResource(R.drawable.recycler_background_dark_grey)}
            Constants.COLOR_TRANSPARENT -> {holder.itemView.setBackgroundResource(R.drawable.recycler_background_transparent)}
        }
    }

    fun setNoteList(noList: ArrayList<Note>) {
        noteList.clear()
        noteList.addAll(noList)
        notifyDataSetChanged()
    }

    override fun getItemCount() = noteList.size

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

}