package com.petits_raids.words.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "word")
data class Word(var eng: String?, var meaning: String?) {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var isShowMeaning: Boolean = false
        get() {
            return field
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        val word = other as Word?

        return if (eng != word!!.eng) false else meaning == word.meaning
    }

    override fun hashCode(): Int {
        var result = eng!!.hashCode()
        result = 31 * result + meaning!!.hashCode()
        return result
    }
}
