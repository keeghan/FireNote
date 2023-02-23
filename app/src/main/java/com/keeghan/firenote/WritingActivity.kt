package com.keeghan.firenote

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
    private lateinit var viewModel: MainViewModel
    private var noteColor = Constants.COLOR_TRANSPARENT
    private var isNoteUpdate = false
    private var pinStatus = false

    //variables to check if noteClicked was changed (to update Edited Time)
    private var noteColorCheck = Constants.COLOR_TRANSPARENT
    private var noteTitleCheck = ""
    private var noteMessageCheck = ""
    private var pinStatusCheck = false

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

            //set initial values for check
            noteColorCheck = note.color
            noteTitleCheck = note.title
            noteMessageCheck = note.message
            pinStatusCheck = note.pinStatus


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
    }//End of onCreate

    //setWritingActivity content
    private fun setNoteContent() {
        val note = intent.extras?.get(Constants.NOTE_CLICKED) as Note
        binding.noteMessage.setText(note.message)
        binding.noteTitle.setText(note.title)

        //  convert database string to zoneDateTime object and convert it back to text
        val dateTime = ZonedDateTime.parse(
            note.dateTimeString, DateTimeFormatter.ofPattern(
                Constants.NOTE_TIME_PATTERN
            )
        )


        //Check last time note was edited and format edited time
        val timeNow = ZonedDateTime.now()
        if (timeNow.isBefore(dateTime.plusDays(1))) {
            displayTime(dateTime, Constants.EDITED_PATTERN_NOW)
        }
        //todo: solve problems
        if (timeNow.isAfter(dateTime.plusDays(2))) {
            displayTime(dateTime, Constants.EDITED_PATTERN_MONTH)
        }
        if (timeNow.isAfter(dateTime.plusMonths(1))) {
            displayTime(dateTime, Constants.EDITED_PATTERN_MONTH)
        }
        if (timeNow.isAfter(dateTime.plusYears(1))) {
            displayTime(dateTime, Constants.EDITED_PATTERN_YEAR)
        }
    }


    //display edited time
    private fun displayTime(dateTime: ZonedDateTime, pattern: String) {
        val editedTime = "Edited " + dateTime.format(
            DateTimeFormatter.ofPattern(pattern)
        )
        binding.dateEdited.text = editedTime
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


    //Inspect whether this is an update even
    //or newNote send even save note
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
        note.color = noteColor
        note.title = binding.noteTitle.text.toString()
        note.message = binding.noteMessage.text.toString()
        note.pinStatus = pinStatus

        //Check if user made changes to the note to determine if
        //note needs to be update or left alone
        val dateTime: String
        if (noteColorCheck == note.color && noteMessageCheck == note.message
            && noteTitleCheck == note.title && pinStatusCheck == note.pinStatus
        ) {
            //maintain date Time
        } else {
            dateTime = ZonedDateTime.now(ZoneId.systemDefault()).format(
                DateTimeFormatter.ofPattern(
                    Constants.NOTE_TIME_PATTERN
                )
            )
            note.dateTimeString = dateTime
        }
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