package com.keeghan.firenote

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.keeghan.firenote.model.Note
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

//ViewModel to handle writing to the realtime database
class MainViewModel(application: Application) : AndroidViewModel(application) {

    //fix get note here
    private var database = Firebase.database
    private val auth = FirebaseAuth.getInstance()
    private val userId = auth.currentUser?.uid
    val noteRef = database.reference.child(userId!!)
    private val myRef = database.getReference(userId!!)


    //called by the mainActivity to update note or just note color
    fun updateNote(newNote: Note, color: String = "") {
        val noteObject = mapOf<String, Any>(
            "color" to if (color == "") newNote.color else color,
            "dateTimeString" to newNote.dateTimeString.uppercase(Locale.getDefault()),  //change dateTime to uppercase to avoid errors
            "id" to newNote.id,
            "message" to newNote.message,
            "pinStatus" to newNote.pinStatus,
            "title" to newNote.title
        )
        myRef.child(newNote.id).updateChildren(noteObject).addOnFailureListener {}
    }


    fun saveNote(note: Note) {
        //Convert ZoneDateTime as String
        note.dateTimeString = ZonedDateTime.now(ZoneId.systemDefault()).format(
            DateTimeFormatter.ofPattern(
                Constants.NOTE_TIME_PATTERN
            )
        ).uppercase(Locale.getDefault())
        //format ZoneDateTime as Id
        note.id = "note_" + ZonedDateTime.now(ZoneId.systemDefault()).format(
            DateTimeFormatter.ofPattern(
                Constants.NOTE_ID_PATTERN
            )
        )

        noteRef.child(note.id).setValue(note).addOnSuccessListener {}.addOnFailureListener {
            Toast.makeText(getApplication(), "Note not Saved", Toast.LENGTH_SHORT).show()
        }
    }

    fun undoDeleteNote(note: Note) {
        noteRef.child(note.id).setValue(note)
    }

    fun deleteNote(tempNote: Note) {
        database.reference.child(userId!!).child(tempNote.id).setValue(null)
    }

}