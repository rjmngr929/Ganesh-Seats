package com.my.ganeshseats.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.my.ganeshseats.Utils.AlertDialogUtility
import com.my.ganeshseats.Utils.TokenManager
import com.my.ganeshseats.prefs.SharedPrefManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashScreenActivity : AppCompatActivity() {

    companion object{
        private val TAG = "Splash Screen Activity"
    }

    @Inject
    lateinit var tokenManager: TokenManager

    @Inject
    lateinit var sharedPrefManager: SharedPrefManager

    @Inject
    lateinit var alertDialogService: AlertDialogUtility

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        if(tokenManager.getToken() != null){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }else{
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }


    }
}