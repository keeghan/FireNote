package com.keeghan.firenote

class Constants {
    companion object {

        const val COLOR_RED = "#880000"
        const val COLOR_BLUE = "#FF01579B"
        const val COLOR_ORANGE = "#FFE65100"
        const val COLOR_VIOLET = "#8f4f8f"
        const val COLOR_GREEN = "#38761d"
        const val COLOR_BROWN = "#310c0c"
        const val COLOR_DARK_GREY = "#4c4c4c"
        const val COLOR_TRANSPARENT = "#00FFFFFF"
        const val NOTE_ID_PATTERN = "yyyyMMddHHmmssSS"
        const val NOTE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss SSSS z"

        //   const val NOTE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss SSSS a z"
        const val EDITED_PATTERN_DAY = "'yesterday ' hh:mm a"
        const val EDITED_PATTERN_NOW = "hh:mm a"
        const val EDITED_PATTERN_WEEK = "E hh:mm a"
        const val EDITED_PATTERN_YEAR = "dd MMM yyyy"
        const val EDITED_PATTERN_MONTH = "dd MMM, hh:mm"
        const val EDITED_PATTERN_WEEKS = "dd, hh:mm"


        const val INTENT_FLAG_ADD_NOTE = "MainActivity.IntentFlag.UpdateNote"
        const val NOTE_CLICKED = "MainActivity.NOTE_CLICKED"
    }
}