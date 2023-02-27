package com.keeghan.firenote

import android.app.Application
import android.content.Intent
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.keeghan.firenote.model.Note
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

//ViewModel to handle writing to the realtime database
class MainViewModel(application: Application) : AndroidViewModel(application) {

    //fix get note here
    var database = Firebase.database
    private val auth = FirebaseAuth.getInstance()
    private val userId = auth.currentUser?.uid
    val noteRef = database.reference.child(userId!!)
    private val myRef = database.getReference(userId!!)


    //called by the writingActivity to update
    fun updateNote(newNote: Note) {
        val noteObject = mapOf<String, Any>(
            "color" to newNote.color,
            "dateTimeString" to newNote.dateTimeString.uppercase(Locale.getDefault()),    //change dateTime to uppercase to avoid errors
            "id" to newNote.id,
            "message" to newNote.message,
            "pinStatus" to newNote.pinStatus,
            "title" to newNote.title
        )
        myRef.child(newNote.id).updateChildren(noteObject)
    }

    //called by the mainActivity to update color
    fun updateNote(newNote: Note, color: String) {
        val noteObject = mapOf<String, Any>(
            "color" to color,
            "dateTimeString" to newNote.dateTimeString.uppercase(Locale.getDefault()),
            "id" to newNote.id,
            "message" to newNote.message,
            "pinStatus" to newNote.pinStatus,
            "title" to newNote.title
        )
        myRef.child(newNote.id).updateChildren(noteObject).addOnFailureListener {
            Toast.makeText(getApplication(), it.message, Toast.LENGTH_SHORT).show()
        }
    }


    fun saveNote(note: Note) {
        //Convert ZoneDateTime as String
        note.dateTimeString = ZonedDateTime.now(ZoneId.systemDefault()).format(
            DateTimeFormatter.ofPattern(
                Constants.NOTE_TIME_PATTERN
            )
        ).uppercase(Locale.getDefault())
        //format ZoneDateTime as Id
        note.id =
            "note_" + ZonedDateTime.now(ZoneId.systemDefault()).format(
                DateTimeFormatter.ofPattern(
                    Constants.NOTE_ID_PATTERN
                )
            )

        noteRef.child(note.id).setValue(note).addOnSuccessListener {
        }
            .addOnFailureListener {
                Toast.makeText(getApplication(), "Note not Saved", Toast.LENGTH_SHORT).show()
            }
    }

    fun deleteNote(tempNote: Note) {
        database.reference.child(userId!!).child(tempNote.id).setValue(null)
    }

}