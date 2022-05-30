package com.keeghan.firenote

import android.content.Context
import android.content.res.Configuration
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.keeghan.firenote.databinding.NoteItemBinding
import com.keeghan.firenote.model.Note


class NoteAdapter(
    private val context: Context,
    private val listener: OnItemClickListener,
    private val longClickListener: OnItemLongClickListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private var noteList: ArrayList<Note> = ArrayList()

    inner class NoteViewHolder(viewBinding: NoteItemBinding) :
        RecyclerView.ViewHolder(viewBinding.root), View.OnClickListener, View.OnLongClickListener {
        private val binding = viewBinding

        init {
            binding.root.setOnClickListener(this)
            binding.root.setOnLongClickListener(this)
        }

        //Set note Font color to white in lightMode but transparent note to dark text
        fun setFontColor(note: Note) {
            val nightModeFlags: Int = context.resources.configuration.uiMode and
                    Configuration.UI_MODE_NIGHT_MASK
            if (nightModeFlags == Configuration.UI_MODE_NIGHT_NO) {
                binding.noteBody.setTextColor(ContextCompat.getColor(context, R.color.white))
            }
            if (note.color == Constants.COLOR_TRANSPARENT && nightModeFlags == Configuration.UI_MODE_NIGHT_NO) {
                binding.noteBody.setTextColor(
                    ContextCompat.getColor(
                        context,
                        androidx.media.R.color.secondary_text_default_material_light
                    )
                )
            }
        }

        fun setNote(note: Note) {
            binding.noteBody.text = note.message
        }

        override fun onClick(p0: View?) {
            if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                listener.onItemClick(bindingAdapterPosition)
            }
        }

        override fun onLongClick(p0: View?): Boolean {
            if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                longClickListener.onItemLongClick(bindingAdapterPosition)
                return true
            }
            return false
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
            Constants.COLOR_RED -> {
                holder.itemView.setBackgroundResource(R.drawable.recycler_background_red)

            }
            Constants.COLOR_BLUE -> {
                holder.itemView.setBackgroundResource(R.drawable.recycler_background_blue)
            }
            Constants.COLOR_ORANGE -> {
                holder.itemView.setBackgroundResource(R.drawable.recycler_background_orange)
            }
            Constants.COLOR_VIOLET -> {
                holder.itemView.setBackgroundResource(R.drawable.recycler_background_violet)
            }
            Constants.COLOR_GREEN -> {
                holder.itemView.setBackgroundResource(R.drawable.recycler_background_green)
            }
            Constants.COLOR_BROWN -> {
                holder.itemView.setBackgroundResource(R.drawable.recycler_background_brown)
            }
            Constants.COLOR_DARK_GREY -> {
                holder.itemView.setBackgroundResource(R.drawable.recycler_background_dark_grey)
            }
            Constants.COLOR_TRANSPARENT -> {
                holder.itemView.setBackgroundResource(R.drawable.recycler_background_transparent)
            }
        }

        holder.setFontColor(noteList[position])

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

    interface OnItemLongClickListener {
        fun onItemLongClick(position: Int)
    }
}