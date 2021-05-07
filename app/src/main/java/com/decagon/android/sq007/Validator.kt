package com.decagon.android.sq007

import java.util.regex.Matcher
import java.util.regex.Pattern

object Validator {

    // This function validates if a phone number is nigerian phone number or not
    // It does so using a regular expression.
    fun phoneNumberValidation(text: String): Boolean {
        var p: Pattern = Pattern.compile("^(0|234)((70)|([89][01]))[0-9]{8}$")
        var m: Matcher = p.matcher(text)
        return m.matches()
    }

    fun userNameValidation(text: String): Boolean {
        return text.isNotEmpty()
    }
}
