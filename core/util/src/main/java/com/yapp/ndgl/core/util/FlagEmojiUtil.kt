package com.yapp.ndgl.core.util

import java.util.Locale

object FlagEmojiUtil {
    private const val FLAG_EMOJI_OFFSET = 127397

    fun String.toFlagEmoji() = if (length == 2 && all { it in ('a'..'z') + ('A'..'Z') }) {
        uppercase(Locale.ROOT).map { character ->
            String(intArrayOf(character.code + FLAG_EMOJI_OFFSET), 0, 1)
        }.joinToString("")
    } else {
        this
    }
}
