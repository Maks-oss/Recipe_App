package com.pi.recipeapp.utils

import android.util.Patterns

object AuthUtil {
    fun isEmailInvalid(email: String) = !Patterns.EMAIL_ADDRESS.matcher(email).matches()
    fun isPasswordInvalid(password: String) = password.length <= 8
    fun isRepeatPasswordInvalid(repeatPassword: String, password: String) = repeatPassword != password
}