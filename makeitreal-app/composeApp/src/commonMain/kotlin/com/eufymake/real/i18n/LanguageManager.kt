package com.eufymake.real.i18n

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf

object LanguageManager {
    var currentLanguage by mutableStateOf(AppLanguage.ZH)
        private set

    val strings: AppStrings
        get() = when (currentLanguage) {
            AppLanguage.ZH -> ZhStrings
            AppLanguage.EN -> EnStrings
        }

    fun setLanguage(language: AppLanguage) {
        currentLanguage = language
    }

    fun toggleLanguage() {
        currentLanguage = if (currentLanguage == AppLanguage.ZH) AppLanguage.EN else AppLanguage.ZH
    }
}

val LocalStrings = staticCompositionLocalOf<AppStrings> { ZhStrings }
