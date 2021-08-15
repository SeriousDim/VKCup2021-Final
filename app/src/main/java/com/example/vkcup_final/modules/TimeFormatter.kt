package com.example.vkcup_final.modules

class TimeFormatter {

    companion object {

        fun secToText(ms: Int): String{
            var sec = ms / 1000
            var mins: Int = sec / 60
            var leftSec = sec - mins * 60
            var hours = mins / 60
            var leftMins = mins - hours * 60
            return if (hours == 0)
                "$leftMins:${addLeadZero(leftSec)}"
            else
                "$hours:${addLeadZero(leftMins)}:${addLeadZero(leftSec)}"
        }

        fun addLeadZero(num: Int): String{
            if (num < 10)
                return "0$num"
            return "$num"
        }

    }

}