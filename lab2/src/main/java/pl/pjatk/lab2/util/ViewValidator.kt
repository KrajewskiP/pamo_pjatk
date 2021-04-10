package pl.pjatk.lab2.util

import android.view.View
import android.widget.TextView

fun isValid(vararg views: View): Boolean {
    var validated = true
    for (view in views) {
        val v = view as TextView
        if (v.text.toString().isEmpty()) {
            v.error = "Field is required"
            validated = false
        } else if (v.text.toString().toFloat() <= 0) {
            v.error = "Value should be a positive number"
            validated = false
        }
    }
    return validated
}
