package com.my.ganeshseats.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.my.ganeshseats.R
import com.my.ganeshseats.Utils.AlertDialogUtility
import com.my.ganeshseats.Utils.NetworkResult
import com.my.ganeshseats.Utils.TokenManager
import com.my.ganeshseats.Utils.getLoadingDialog
import com.my.ganeshseats.Utils.hideLoader
import com.my.ganeshseats.Utils.showLoader
import com.my.ganeshseats.constants.Constants
import com.my.ganeshseats.data.Database.DataModel.User
import com.my.ganeshseats.databinding.ActivityLoginBinding
import com.my.ganeshseats.prefs.SharedPrefManager
import com.my.ganeshseats.ui.viewmodel.AuthViewModel
import com.my.ganeshseats.ui.viewmodel.UserDataViewModel
import com.my.raido.Validations.EmailRule
import com.my.raido.Validations.EmptyTextRule
import com.my.raido.Validations.validateRule
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.getValue

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    companion object{
        private const val TAG = "Login Activity"
    }

    private lateinit var binding: ActivityLoginBinding

    @Inject
    lateinit var tokenManager: TokenManager

    @Inject
    lateinit var alertDialogService: AlertDialogUtility

    @Inject
    lateinit var sharedPrefManager: SharedPrefManager

    private val authViewModel: AuthViewModel by viewModels()

    private val userDataViewModel: UserDataViewModel by viewModels()

    private lateinit var loader: AlertDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        loader = getLoadingDialog(this)

        binding.emailText.validateRule(
            rules = listOf(
                EmptyTextRule(this),
                EmailRule("Please enter valid email")
            )
        )

        binding.passwordText.validateRule(
            rules = listOf(
                EmptyTextRule(this)
            )
        )


        binding.loginBtn.setOnClickListener {
           val email = binding.emailTextField.editText?.text
           val password = binding.passwordTextField.editText?.text
            if(email.isNullOrEmpty()){
                binding.emailTextField.error = "Please enter email."
            }else if(password.isNullOrEmpty()){
                binding.passwordTextField.error = "Please enter password."
            }else{
                binding.emailTextField.error = null
                binding.passwordTextField.error = null
                authViewModel.loginApi(email = email.toString(), password = password.toString())
            }

        }



    }

    override fun onResume() {
        super.onResume()
        loginListener()
    }

    private fun loginListener() {
        authViewModel.loginResponseLiveData.observe(this, Observer {
            when (it) {
                is NetworkResult.Success -> {
                    hideLoader(this, loader)

                    tokenManager.saveToken(it.data?.loginResponse?.authToken.toString())

                    val userData = it.data?.loginResponse?.userData!!

                    sharedPrefManager.putInt(Constants.USER_ID, userData.userId)



                    userDataViewModel.insertUser(
                        User(
                            userId = userData.userId,
                            userName = userData.userName,
                            userEmail = userData.userEmail
                        )
                    )
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()

                }
                is NetworkResult.Error -> {
                    hideLoader(this, loader)
                    alertDialogService.alertDialogAnim(this, it.message.toString(), R.raw.failed)

                }
                is NetworkResult.Loading ->{
                    showLoader(this, loader)
                }
                is NetworkResult.Empty -> {
                    hideLoader(this, loader)
                }
            }
        })
    }




}