package pl.pjatk.lab3.model

import java.util.*

data class Country(
    var Country: String,
    var Slug: String
) {
    override fun toString(): String {
        return Country
    }
}

data class ResultData(
    val Confirmed: Int,
    val Date: Date
)
