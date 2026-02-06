package com.my.ganeshseats.Utils

import android.content.Context
import android.content.SharedPreferences
import com.my.ganeshseats.constants.Constants.PREFS_TOKEN_FILE
import com.my.ganeshseats.constants.Constants.USER_TOKEN
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class TokenManager @Inject constructor(@ApplicationContext context: Context) {
    private var prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_TOKEN_FILE, Context.MODE_PRIVATE)

    private var onTokenAvailable: (() -> Unit)? = null

    fun saveToken(token: String) {
        val editor = prefs.edit()
        editor.putString(USER_TOKEN, token)
        editor.apply()

        onTokenAvailable?.invoke()
        onTokenAvailable = null // Only notify once
    }

    fun getToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }

    fun removeToken(){
        val editor = prefs.edit()
        editor.putString(USER_TOKEN, null)
        editor.apply()
    }


}