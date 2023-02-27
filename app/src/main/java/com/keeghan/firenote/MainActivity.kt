package com.keeghan.firenote

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.keeghan.firenote.Constants.Companion.INTENT_FLAG_ADD_NOTE
import com.keeghan.firenote.Constants.Companion.NOTE_CLICKED
import com.keeghan.firenote.databinding.ActivityMainBinding
import com.keeghan.firenote.model.Note
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlin.system.exitProcess

@SuppressLint("NotifyDataSetChanged")
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: NoteAdapter
    private lateinit var pinnedAdapter: NoteAdapter
    private lateinit var noteList: ArrayList<Note>
    private lateinit var pinnedList: ArrayList<Note>
    private var actionMode: ActionMode? = null
    private var longClickNote: Note? = null  //stores the current note which has been longClicked
    private lateinit var colorDialogFragment: ColorPickerFragment
    private lateinit var viewModel: MainViewModel

    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        noteList = ArrayList()
        pinnedList = ArrayList()

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        colorDialogFragment = ColorPickerFragment.newInstance()

        setClickListener() //implement longClick and onClickListener of adapters

        //Reading notes for database in Arraylist To display in RecyclerView
        val eventListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                noteList.clear()
                pinnedList.clear()
                readNotes(dataSnapshot)
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        viewModel.noteRef.addValueEventListener(eventListener)

        binding.fabAddNote.setOnClickListener {
            val intent = Intent(applicationContext, WritingActivity::class.java)
            intent.putExtra(INTENT_FLAG_ADD_NOTE, false)
            startActivity(intent)
            if (actionMode != null) {
                closeActionMode()
            }
        }
        attachItemTouchHelper()
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
        auth = FirebaseAuth.getInstance()
    } //OnCreate

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            //showing dialog and then closing the application..
            showDialog()
        }
    }

    private fun showDialog() {
        MaterialAlertDialogBuilder(this).apply {
            setTitle("Close application?")
            setPositiveButton("Yes") { _, _ ->
                finish()
            }
            setNegativeButton("Log Out") { _, _ ->
                auth.signOut()
                val intent = Intent(this@MainActivity, SignInActivity::class.java)
                startActivity(intent)
                finish()
            }
            show()
        }
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
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder,
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when (direction) {
                    ItemTouchHelper.RIGHT, ItemTouchHelper.LEFT -> {
                        //delete note from realtime database calling delete note from viewModel
                        //   val tempNote: Note = noteList[viewHolder.absoluteAdapterPosition]
//                            val tempNote: Note = if (viewHolder == binding.noteRecycler) {
//                                noteList[viewHolder.absoluteAdapterPosition]
//                            } else {
//                                pinnedList[viewHolder.absoluteAdapterPosition]
//                            }
                        val tempNote: Note = noteList[viewHolder.absoluteAdapterPosition]
                        viewModel.deleteNote(tempNote)
                        adapter.notifyItemChanged(viewHolder.absoluteAdapterPosition)
                        makeSnackBar(tempNote)
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
                isCurrentlyActive: Boolean,
            ) {
                RecyclerViewSwipeDecorator.Builder(
                    c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive
                ).addActionIcon(R.drawable.ic_baseline_delete_outline_24).create().decorate()
                super.onChildDraw(
                    c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive
                )
            }
        }) //end of item touch helper

        val itemTouchHelper2 = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder,
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when (direction) {
                    ItemTouchHelper.RIGHT, ItemTouchHelper.LEFT -> {
                        //delete note from realtime database calling delete note from viewModel
                        val tempNote: Note = pinnedList[viewHolder.absoluteAdapterPosition]
                        viewModel.deleteNote(tempNote)
                        adapter.notifyItemChanged(viewHolder.absoluteAdapterPosition)
                        makeSnackBar(tempNote)
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
                isCurrentlyActive: Boolean,
            ) {
                RecyclerViewSwipeDecorator.Builder(
                    c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive
                ).addActionIcon(R.drawable.ic_baseline_delete_outline_24).create().decorate()
                super.onChildDraw(
                    c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive
                )
            }
        }) //end of item touch helper

        itemTouchHelper2.attachToRecyclerView(binding.pinnedRecycler)
        itemTouchHelper.attachToRecyclerView(binding.noteRecycler)
        //  itemTouchHelper.attachToRecyclerView(binding.pinnedRecycler)
    }


    //todo: add users to note info
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

            //separate pinned from others
            if (note.pinStatus) pinnedList.add(note)
            else noteList.add(note)
        }

        binding.noteRecycler.adapter = adapter
        binding.noteRecycler.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        adapter.setNoteList(noteList)

        if (pinnedList.isNotEmpty()) {
            binding.pinnedRecycler.visibility = View.VISIBLE
            binding.pinnedTitle.visibility = View.VISIBLE
            binding.othersTitle.visibility = View.VISIBLE
            binding.pinnedRecycler.adapter = pinnedAdapter
            binding.pinnedRecycler.layoutManager =
                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            pinnedAdapter.setNoteList(pinnedList)
        } else {
            binding.pinnedRecycler.visibility = View.GONE
            binding.pinnedTitle.visibility = View.GONE
            binding.othersTitle.visibility = View.GONE
        }

        //TODO: display message if there's no note

    }


    fun recyclerOnClick(list: ArrayList<Note>, position: Int) {
        val note: Note = list[position]
        val intent = Intent(applicationContext, WritingActivity::class.java).apply {
            putExtra(INTENT_FLAG_ADD_NOTE, true)
            putExtra(NOTE_CLICKED, note)
        }
        startActivity(intent)
        if (actionMode != null) {
            closeActionMode()
        }
    }


    //ActionMode Implementation for when note is longClicked
    private val actionModeCallback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            mode?.menuInflater?.inflate(R.menu.menu_long_click, menu)
            if (longClickNote!!.pinStatus) {
                menu?.findItem(R.id.action_pin)?.setIcon(R.drawable.ic_outline_push_pin_24)
            } else {
                menu?.findItem(R.id.action_pin)?.setIcon(R.drawable.ic_push_pin)
            }
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            return true
        }

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            when (item?.itemId) {
                R.id.action_pin -> {
                    longClickNote!!.pinStatus = !longClickNote!!.pinStatus
                    if (longClickNote!!.pinStatus) {
                        item.setIcon(R.drawable.ic_outline_push_pin_24)
                    } else {
                        item.setIcon(R.drawable.ic_push_pin)
                    }
                    viewModel.updateNote(longClickNote!!)
                    adapter.notifyDataSetChanged()
                    pinnedAdapter.notifyDataSetChanged()
                    mode?.finish()

                }
                R.id.action_choose_color -> {
                    colorDialogFragment.show(supportFragmentManager, "customColorPicker")
                }
                R.id.action_delete -> {
                    viewModel.deleteNote(longClickNote!!)
                    adapter.notifyDataSetChanged()
                    makeSnackBar(longClickNote!!)
                    mode?.finish()

                }
                R.id.action_send -> { //share note functionality using ShareSheet
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, longClickNote!!.message)
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, null)
                    startActivity(shareIntent)
                    mode?.finish()

                }
                R.id.action_make_a_copy -> {
                    //make a copy of an existing note
                    val duplicateNote = longClickNote!!.copy()
                    viewModel.saveNote(duplicateNote)
                    mode?.finish()

                }
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

    fun makeSnackBar(note: Note) {
        Snackbar.make(
            binding.mainCoordinator, "Undo note delete", Snackbar.LENGTH_SHORT
        ).setAction("Undo") {
            viewModel.database.getReference("note").child(note.id).setValue(note)
            Toast.makeText(applicationContext, "Note Restored", Toast.LENGTH_SHORT).show()
        }.show()
    }

    /*Adapter Declarations with ----
    * method to set the click listener for the
    * pinnedRecycler and the mainRecycler*/
    private fun setClickListener() {
        //otherList
        adapter = NoteAdapter(this, object : NoteAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                recyclerOnClick(noteList, position)
            }

        }, object : NoteAdapter.OnItemLongClickListener {
            override fun onItemLongClick(position: Int) {
                longClickNote = noteList[position]
                actionMode = startSupportActionMode(actionModeCallback)
            }
        })


        //pinnedLIst
        pinnedAdapter = NoteAdapter(this, object : NoteAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                recyclerOnClick(pinnedList, position)
            }

        }, object : NoteAdapter.OnItemLongClickListener {
            override fun onItemLongClick(position: Int) {
                longClickNote = pinnedList[position]
                actionMode = startSupportActionMode(actionModeCallback)
            }
        })
    }
}
