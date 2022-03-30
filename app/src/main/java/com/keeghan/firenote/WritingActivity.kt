package com.keeghan.firenote

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.keeghan.firenote.databinding.ActivityWritingBinding
import com.keeghan.firenote.model.Note
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class WritingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWritingBinding
    private lateinit var database: FirebaseDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = Firebase.database
        binding = ActivityWritingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.dateEdited.text = LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE)

        binding.backBtn.setOnClickListener {
            sendNote()
            finish()
        }
    }

    //validate Note, create note and upload a New Note to Realtime Database
    private fun sendNote() {
        if (binding.noteTitle.text.toString().isEmpty() and binding.noteMessage.text.toString()
                .isEmpty()
        ) {
            Toast.makeText(applicationContext, "Empty note", Toast.LENGTH_SHORT).show()
        } else {
            val note = Note()
            note.color = Constants.COLOR_TRANSPARENT
            note.title = binding.noteTitle.text.toString()
            note.message = binding.noteMessage.text.toString()
            note.pinStatus = false
            //Convert ZoneDateTime as String
            note.dateTimeString = ZonedDateTime.now(ZoneId.systemDefault()).format(
                DateTimeFormatter.ofPattern(
                    Constants.NOTE_TIME_PATTERN
                )
            )
            //format ZoneDateTime as Id
            note.id =
                "note_" + ZonedDateTime.now(ZoneId.systemDefault()).format(
                    DateTimeFormatter.ofPattern(
                        Constants.NOTE_ID_PATTERN
                    )
                )
            val myRef = database.getReference("note")
            myRef.child(note.id).setValue(note).addOnSuccessListener {
                Toast.makeText(applicationContext, "Note Saved", Toast.LENGTH_SHORT).show()
            }
                .addOnFailureListener {
                    Toast.makeText(applicationContext, "Note not Saved", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        sendNote()
    }
}