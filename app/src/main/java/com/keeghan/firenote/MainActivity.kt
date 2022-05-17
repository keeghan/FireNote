package com.keeghan.firenote

import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.keeghan.firenote.Constants.Companion.INTENT_FLAG_ADD_NOTE
import com.keeghan.firenote.Constants.Companion.NOTE_CLICKED
import com.keeghan.firenote.databinding.ActivityMainBinding
import com.keeghan.firenote.model.Note
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

class MainActivity : AppCompatActivity(), NoteAdapter.OnItemClickListener,
    NoteAdapter.OnItemLongClickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var adapter: NoteAdapter
    private lateinit var noteList: ArrayList<Note>
    private var actionMode: ActionMode? = null
    private var longClickNote: Note? = null
    private lateinit var colorDialogFragment: ColorPickerFragment
    private lateinit var viewModel: MainViewModel

    override
    fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = Firebase.database
        noteList = ArrayList()
        adapter = NoteAdapter(this, this)
        val noteRef = database.reference.child("note")
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        colorDialogFragment = ColorPickerFragment.newInstance()


        //Reading notes for database in Arraylist To display in RecyclerView
        val eventListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                noteList.clear()
                readNotes(dataSnapshot)
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }
        noteRef.addValueEventListener(eventListener)

        binding.fabAddNote.setOnClickListener {
            val intent = Intent(applicationContext, WritingActivity::class.java)
            intent.putExtra(INTENT_FLAG_ADD_NOTE, false)
            startActivity(intent)
            if (actionMode != null) {
                closeActionMode()
            }
        }
        attachItemTouchHelper()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    //Method to add swipe to delete support
    private fun attachItemTouchHelper() {
        //Implementation of swiping to delete
        val itemTouchHelper = ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    when (direction) {
                        ItemTouchHelper.RIGHT, ItemTouchHelper.LEFT -> {
                            //delete note from realtime database
                            val tempNote: Note = noteList[viewHolder.absoluteAdapterPosition]
                            val noteRef = database.reference.child("note").child(tempNote.id)
                            noteRef.setValue(null)
                            adapter.notifyItemChanged(viewHolder.absoluteAdapterPosition)

                            Snackbar.make(
                                binding.mainCoordinator,
                                "Undo note delete",
                                Snackbar.LENGTH_SHORT
                            ).setAction("Undo") {
                                val myRef = database.getReference("note")
                                myRef.child(tempNote.id).setValue(tempNote)
                                Toast.makeText(
                                    applicationContext, "Note restored",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }.show()
                        }
                    }
                }

                override fun onChildDraw(
                    c: Canvas,
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    dX: Float,
                    dY: Float,
                    actionState: Int,
                    isCurrentlyActive: Boolean
                ) {
                    RecyclerViewSwipeDecorator.Builder(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    ).addActionIcon(R.drawable.ic_baseline_delete_outline_24)
                        .create()
                        .decorate()
                    super.onChildDraw(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                }
            }) //end of item touch helper
        itemTouchHelper.attachToRecyclerView(binding.noteRecycler)
    }

    //getting notes from realtime database and setting them to recycler
    fun readNotes(dataSnapshot: DataSnapshot) {
        for (ds in dataSnapshot.children) {
            val note = Note()
            note.message = ds.child("message").value.toString()
            note.pinStatus = ds.child("pinStatus").value as Boolean
            note.color = ds.child("color").value.toString()
            note.title = ds.child("title").value.toString()
            note.id = ds.child("id").value.toString()
            note.dateTimeString = ds.child("dateTimeString").value.toString()
            noteList.add(note)
        }
        binding.noteRecycler.adapter = adapter
        binding.noteRecycler.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        adapter.setNoteList(noteList)
    }

    //handles clicking and longClicking of Notes
    override fun onItemClick(position: Int) {
        val note: Note = noteList[position]
        val intent = Intent(applicationContext, WritingActivity::class.java).apply {
            putExtra(INTENT_FLAG_ADD_NOTE, true)
            putExtra(NOTE_CLICKED, note)
        }
        Toast.makeText(applicationContext, note.title, Toast.LENGTH_SHORT).show()
        startActivity(intent)
        if (actionMode != null) {
            closeActionMode()
        }
    }

    override fun onItemLongClick(position: Int) {
        longClickNote = noteList[position]
        actionMode = startSupportActionMode(actionModeCallback)!!
    }

    //ActionMode Implementation
    private val actionModeCallback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            mode?.menuInflater?.inflate(R.menu.menu_long_click, menu)

            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            return true
        }

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            when (item?.itemId) {
                R.id.action_pin -> mode?.finish()
                R.id.action_choose_color -> {
                    colorDialogFragment.show(supportFragmentManager, "customColorPicker")
                }
                R.id.action_more -> mode?.finish()
                else -> {
                    return false
                }
            }
            return false
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            actionMode = null
        }
    } //end of ActionMode

    fun updateNoteColor(color: String) {
        viewModel.updateNote(longClickNote!!, color)
    }

    fun closeActionMode() {
        actionMode?.finish()
    }
}
