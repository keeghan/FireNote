package com.keeghan.firenote

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayoutStates
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.keeghan.firenote.databinding.ActivityWritingBinding
import com.keeghan.firenote.databinding.ColorMenuLayoutBinding
import com.keeghan.firenote.model.Note
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class WritingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWritingBinding

    private lateinit var dialog: BottomSheetDialog
    private lateinit var colorDialogBinding: ColorMenuLayoutBinding
    private var noteColor = Constants.COLOR_TRANSPARENT
    private var isNoteUpdate = false
    private lateinit var viewModel: MainViewModel
    private var pinStatus = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //handle if activity is created from clicking note
        isNoteUpdate = intent.getBooleanExtra(Constants.INTENT_FLAG_ADD_NOTE, false)
        binding = ActivityWritingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        binding.dateEdited.text =
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("'Edited' MMM-dd"))

        //change background of ui if it is updating activity
        if (isNoteUpdate) {
            val note = intent.extras?.get(Constants.NOTE_CLICKED) as Note
            noteColor = note.color
            pinStatus = note.pinStatus
            if (pinStatus) {
                binding.pinBtn.setImageResource(R.drawable.ic_push_pin)
            } else {
                binding.pinBtn.setImageResource(R.drawable.ic_outline_push_pin_24)
            }
            binding.mainContainer.setBackgroundColor(Color.parseColor(noteColor))

            //set ui color
            if ((resources.configuration.uiMode and
                        Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_NO
            ) {
                //lightMode ColoredNote
                if (noteColor != Constants.COLOR_TRANSPARENT) {
                    setUIColor(R.color.white)
                } else { //lightMode TransparentNote
                    setUIColor(R.color.default_text_color)
                }
            } else {
                setUIColor(R.color.white)
            }
        }


        binding.pinBtn.setOnClickListener {
            pinStatus = !pinStatus
            if (pinStatus) {
                binding.pinBtn.setImageResource(R.drawable.ic_push_pin)
            } else {
                binding.pinBtn.setImageResource(R.drawable.ic_outline_push_pin_24)
            }
        }

        binding.backBtn.setOnClickListener {
            if (isNoteUpdate) {
                updateNote()
            } else {
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
            note.pinStatus = pinStatus

            viewModel.saveNote(note)
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
        val dateTime = ZonedDateTime.now(ZoneId.systemDefault()).format(
            DateTimeFormatter.ofPattern(
                Constants.NOTE_TIME_PATTERN
            )
        )

        val note = intent.extras?.get(Constants.NOTE_CLICKED) as Note
        note.color = noteColor
        note.title = binding.noteTitle.text.toString()
        note.message = binding.noteMessage.text.toString()
        note.pinStatus = pinStatus
        note.dateTimeString = dateTime

        viewModel.updateNote(note)
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
            binding.mainContainer.setBackgroundColor(Color.parseColor(noteColor))
            colorDialogBinding.mainContainer.setBackgroundColor(Color.parseColor(noteColor))

            if ((resources.configuration.uiMode and
                        Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_NO
            ) {
                setUIColor(R.color.default_text_color)
            }
            dialog.dismissWithAnimation
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
        colorDialogBinding.mainContainer.setBackgroundColor(Color.parseColor(color))
        setUIColor(R.color.white)
        dialog.dismissWithAnimation
    }

    //change all  icons and button elements to white after setting color
    private fun setUIColor(id: Int) {
        DrawableCompat.setTint(
            binding.backBtn.drawable,
            ContextCompat.getColor(applicationContext, id)
        )
        DrawableCompat.setTint(
            binding.moreOptions.drawable,
            ContextCompat.getColor(applicationContext, id)
        )
        DrawableCompat.setTint(
            binding.colorSelector.drawable,
            ContextCompat.getColor(applicationContext, id)
        )
        DrawableCompat.setTint(
            binding.pinBtn.drawable,
            ContextCompat.getColor(applicationContext, id)
        )
        binding.dateEdited.setTextColor(ContextCompat.getColor(applicationContext, id))
        binding.noteTitle.setTextColor(ContextCompat.getColor(applicationContext, id))
        binding.noteMessage.setTextColor(ContextCompat.getColor(applicationContext, id))
    }
}