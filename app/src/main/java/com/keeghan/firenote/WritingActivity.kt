package com.keeghan.firenote

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.keeghan.firenote.databinding.ActivityWritingBinding
import com.keeghan.firenote.databinding.ColorMenuLayoutBinding
import com.keeghan.firenote.model.Note
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class WritingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWritingBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var dialog: BottomSheetDialog
    private lateinit var colorDialogBinding: ColorMenuLayoutBinding
    private var noteColor = Constants.COLOR_TRANSPARENT
    private var isNoteUpdate = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isNoteUpdate = intent.getBooleanExtra(Constants.INTENT_FLAG_ADD_NOTE, false)

        database = Firebase.database
        binding = ActivityWritingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.dateEdited.text = LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE)

        //change background of ui if it is updating activity
        if (isNoteUpdate) {
            val note = intent.extras?.get(Constants.NOTE_CLICKED) as Note
            noteColor = note.color
            binding.mainContainer.setBackgroundColor(Color.parseColor(noteColor))
            setToWhite()
        }

        binding.backBtn.setOnClickListener {
            if (isNoteUpdate) {
                updateNote()
                Log.e("====================", "update note executed")
            } else {
                Log.e("=====================", "sendNote executed")
                sendNote()
            }
            finish()
        }

        binding.colorSelector.setOnClickListener {
            showBottomSheetDialog()
        }

        if (isNoteUpdate) {
            setNoteContent()
        }
    }

    //setWritingActivity content
    private fun setNoteContent() {
        val note = intent.extras?.get(Constants.NOTE_CLICKED) as Note
        binding.noteMessage.setText(note.message)
        binding.noteTitle.setText(note.title)
        binding.dateEdited.text

        //convert database sting to zoneDateTime object and convert it back to text
        val dateTime = ZonedDateTime.parse(
            note.dateTimeString, DateTimeFormatter.ofPattern(
                Constants.NOTE_TIME_PATTERN
            )
        )

        val time = "Edited: " + dateTime.format(
            DateTimeFormatter.ofPattern(
                Constants.NOTE_TIME_EDITED_PATTERN
            )
        )
        binding.dateEdited.text = time
    }

    //validate Note, create note and upload a New Note to Realtime Database
    private fun sendNote() {
        if (binding.noteTitle.text.toString().isEmpty() and binding.noteMessage.text.toString()
                .isEmpty()
        ) {
            Toast.makeText(applicationContext, "Empty note", Toast.LENGTH_SHORT).show()
        } else {
            val note = Note()
            note.color = noteColor
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
        if (isNoteUpdate) {
            updateNote()
        } else {
            sendNote()
        }
        finish()
    }


    private fun updateNote() {
        val note = intent.extras?.get(Constants.NOTE_CLICKED) as Note
        val dateTime = ZonedDateTime.now(ZoneId.systemDefault()).format(
            DateTimeFormatter.ofPattern(
                Constants.NOTE_TIME_PATTERN
            )
        )

        val noteObject = mapOf<String, Any>(
            "color" to noteColor,
            "dateTimeString" to dateTime,
            "id" to note.id,
            "message" to binding.noteMessage.text.toString(),
            "pinStatus" to false,
            "title" to binding.noteTitle.text.toString()
        )

        val myRef = database.getReference("note").child(note.id)
        myRef.updateChildren(noteObject).addOnSuccessListener {
            Toast.makeText(applicationContext, "Note Updated", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(applicationContext, it.message, Toast.LENGTH_SHORT).show()
        }
    }


    private fun showBottomSheetDialog() {
        dialog = BottomSheetDialog(this)
        colorDialogBinding = ColorMenuLayoutBinding.inflate(layoutInflater, null, false)
        colorDialogBinding.mainContainer.setBackgroundColor(Color.parseColor(noteColor))
        dialog.setContentView(colorDialogBinding.root)

        dialog.show()


        //set onclickListener for all the button in the  bottomSheetDialog
        colorDialogBinding.transparentBtn.setOnClickListener {
            noteColor = Constants.COLOR_TRANSPARENT
            changeNoteColor(noteColor)
        }
        colorDialogBinding.redBtn.setOnClickListener {
            noteColor = Constants.COLOR_RED
            changeNoteColor(noteColor)
        }
        colorDialogBinding.orangeBtn.setOnClickListener {
            noteColor = Constants.COLOR_ORANGE
            changeNoteColor(noteColor)
        }
        colorDialogBinding.blueBtn.setOnClickListener {
            noteColor = Constants.COLOR_BLUE
            changeNoteColor(noteColor)
        }
        colorDialogBinding.greenBtn.setOnClickListener {
            noteColor = Constants.COLOR_GREEN
            changeNoteColor(noteColor)
        }
        colorDialogBinding.brownBtn.setOnClickListener {
            noteColor = Constants.COLOR_BROWN
            changeNoteColor(noteColor)
        }
        colorDialogBinding.violetBtn.setOnClickListener {
            noteColor = Constants.COLOR_VIOLET
            changeNoteColor(noteColor)
        }
        colorDialogBinding.darkGreyBtn.setOnClickListener {
            noteColor = Constants.COLOR_DARK_GREY
            changeNoteColor(noteColor)
        }
    }

    //Method executed when a color is chosen
    private fun changeNoteColor(color: String) {
        binding.mainContainer.setBackgroundColor(Color.parseColor(color))
        //   binding.mainContainer.setBackgroundColor(ContextCompat.getColor(applicationContext, color))
        colorDialogBinding.mainContainer.setBackgroundColor(Color.parseColor(color))
        setToWhite()
        dialog.dismissWithAnimation
    }

    //change all  icons and button elements to white after setting color
    private fun setToWhite() {
        DrawableCompat.setTint(
            binding.backBtn.drawable,
            ContextCompat.getColor(applicationContext, R.color.white)
        )
        DrawableCompat.setTint(
            binding.moreOptions.drawable,
            ContextCompat.getColor(applicationContext, R.color.white)
        )
        DrawableCompat.setTint(
            binding.colorSelector.drawable,
            ContextCompat.getColor(applicationContext, R.color.white)
        )
        DrawableCompat.setTint(
            binding.pinBtn.drawable,
            ContextCompat.getColor(applicationContext, R.color.white)
        )
        binding.dateEdited.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
        binding.noteTitle.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
        binding.noteMessage.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
    }
}