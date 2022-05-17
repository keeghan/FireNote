package com.keeghan.firenote

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.keeghan.firenote.model.Note
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class MainViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private var database = Firebase.database
    }

    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct ViewModel")
        }
    }


    fun updateNote(newNote: Note) {
        val dateTime = ZonedDateTime.now(ZoneId.systemDefault()).format(
            DateTimeFormatter.ofPattern(
                Constants.NOTE_TIME_PATTERN
            )
        )
        val noteObject = mapOf<String, Any>(
            "color" to newNote.color,
            "dateTimeString" to dateTime,
            "id" to newNote.id,
            "message" to newNote.message,
            "pinStatus" to false,
            "title" to newNote.title
        )
        val myRef = database.getReference("note").child(newNote.id)
        myRef.updateChildren(noteObject)
    }

    fun updateNote(newNote: Note, color: String) {
        val dateTime = ZonedDateTime.now(ZoneId.systemDefault()).format(
            DateTimeFormatter.ofPattern(
                Constants.NOTE_TIME_PATTERN
            )
        )
        val noteObject = mapOf<String, Any>(
            "color" to color,
            "dateTimeString" to dateTime,
            "id" to newNote.id,
            "message" to newNote.message,
            "pinStatus" to false,
            "title" to newNote.title
        )
        val myRef = database.getReference("note").child(newNote.id)
        myRef.updateChildren(noteObject).addOnFailureListener {
            Toast.makeText(getApplication(), it.message, Toast.LENGTH_SHORT).show()
        }
    }
}