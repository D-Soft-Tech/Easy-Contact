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

    fun emailValidation(email: String): Boolean {
        var p: Pattern = Pattern.compile("^[a-zA-Z]+([\\w._]|(-))*\\w+(@)[a-z]+(.)[a-z]{3}$")
        var m: Matcher = p.matcher(email)
        return m.matches()
    }

    fun genderValidation(text: String): Boolean {
        return text == "Male" || text == "Female"
    }

    fun userNameValidation(text: String): Boolean {
        var p: Pattern = Pattern.compile("^([a-zA-Z])[a-z]{2}[a-z]*$")
        var m: Matcher = p.matcher(text)
        return m.matches()
    }
}
