package com.keeghan.firenote.model

import com.keeghan.firenote.Constants
import java.io.Serializable
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

data class Note(
    var id: String = "",
    var title: String = "",
    var message: String = "",
    var color: String = "transparent",
    var pinStatus: Boolean = false,
    var dateTimeString: String = ""
) : Serializable
