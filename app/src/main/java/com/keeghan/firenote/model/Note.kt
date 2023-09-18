package com.keeghan.firenote.model

import java.io.Serializable

/**
 * Note that represents a written note
 * @param id represents that unique id of the note
 * created uniquely with userId and time note was created
 * @param title typed by the user for the note
 * @param message is the main body of the note
 * @param color a hex color code of the color of the note
 * @param pinStatus shows whether a note is pinned or not
 * @param dateTimeString is a string representation of a DataTime object
 */
data class Note(
    var id: String = "",
    var title: String = "",
    var message: String = "",
    var color: String = "transparent",
    var pinStatus: Boolean = false,
    var dateTimeString: String = ""
) : Serializable
