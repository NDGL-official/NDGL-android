package com.yapp.ndgl.data.travel.exception

class DuplicateSuggestedTemplateException(
    val reason: Reason,
    cause: Throwable? = null,
) : Exception(cause) {
    enum class Reason {
        ALREADY_PUBLISHED,
        OTHER_USER_PENDING,
        SELF_PENDING,
        ALREADY_SUBSCRIBED,
    }
}
