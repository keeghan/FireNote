package com.keeghan.firenote

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.StaggeredGridLayoutManager
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
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity(), NoteAdapter.OnItemClickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var adapter: NoteAdapter
    private lateinit var noteList: ArrayList<Note>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        database = Firebase.database
        noteList = ArrayList()
        adapter = NoteAdapter(this)

        val rootRef = FirebaseDatabase.getInstance().reference
        val noteRef = rootRef.child("note")


        //Reading notes for database in Arraylist To display in RecyclerView
        val eventListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                noteList.clear()
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
                //  GridLayoutManager(applicationContext, 2, GridLayoutManager.VERTICAL, false)
                adapter.setNoteList(noteList)
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }
        noteRef.addValueEventListener(eventListener)

        binding.fabAddNote.setOnClickListener {
            val intent = Intent(applicationContext, WritingActivity::class.java)
            intent.putExtra(INTENT_FLAG_ADD_NOTE, false)
            startActivity(intent)
        }
    }

    private fun convertDate(date: Date): String {
        return SimpleDateFormat("MMMMddhh-ssSSS", Locale.getDefault()).format(date)
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


    //handles clicking of items
    override fun onItemClick(position: Int) {
        val note: Note = noteList[position]
        val intent = Intent(applicationContext, WritingActivity::class.java).apply {
            putExtra(INTENT_FLAG_ADD_NOTE, true)
            putExtra(NOTE_CLICKED, note)
        }
        Toast.makeText(applicationContext, note.title, Toast.LENGTH_SHORT).show()
        startActivity(intent)
    }
}