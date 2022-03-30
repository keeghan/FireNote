package com.keeghan.firenote

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.keeghan.firenote.databinding.NoteItemBinding
import com.keeghan.firenote.model.Note

class NoteAdapter() :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var noteList: ArrayList<Note> = ArrayList()

    inner class NoteViewHolder(viewBinding: NoteItemBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {
        private val binding = viewBinding

        fun setNote(note: Note) {
            binding.noteBody.text = note.message
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return NoteViewHolder(
            NoteItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    //set background color
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as NoteViewHolder).setNote(noteList[position])
       // (holder as NoteViewHolder).itemView.setBackgroundColor(Color.parseColor(noteList[position].color))
    }

    fun setNoteList(noList: ArrayList<Note>) {
        noteList.clear()
        noteList.addAll(noList)
        notifyDataSetChanged()
    }

    override fun getItemCount() = noteList.size
}