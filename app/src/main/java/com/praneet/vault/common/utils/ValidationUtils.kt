package com.praneet.vault.common.utils

object ValidationUtils {
    fun isBlank(value: String): Boolean = value.isBlank()

    fun <T> existsBy(
        selector: (T) -> String,
        items: List<T>,
        candidate: String,
        ignoreCase: Boolean = false
    ): Boolean {
        return items.any { selector(it).equals(candidate, ignoreCase) }
    }
}


