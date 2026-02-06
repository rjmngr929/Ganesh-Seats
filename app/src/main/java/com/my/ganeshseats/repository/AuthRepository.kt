package com.my.ganeshseats.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.my.ganeshseats.Helper.NetworkHelper
import com.my.ganeshseats.Utils.NetworkResult
import com.my.ganeshseats.api.AuthAPI
import com.my.ganeshseats.data.response.LoginModel
import com.my.ganeshseats.di.exception.NetworkExceptionHandler
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

class AuthRepository @Inject constructor(private val authAPI: AuthAPI, private val networkHelper: NetworkHelper, private val exceptionHandler: NetworkExceptionHandler) {


//********************** Login Opertaion ******************************************
    val _loginResponseLiveData = MutableLiveData<NetworkResult<LoginModel>>()
    val loginResponseLiveData: LiveData<NetworkResult<LoginModel>>
        get() = _loginResponseLiveData

    suspend fun loginUser(email: String, password: String) {
        _loginResponseLiveData.postValue(NetworkResult.Loading())
        if(networkHelper.isNetworkConnected()) {
            try {
                val response = authAPI.login(email = email, password = password)
                if (response.isSuccessful) {
                    handleLoginResponse(response)
                } else {
                    try {
                        val jsonString = response.errorBody()?.toString()
                        Log.d("TAG", "loginUser: error create by ${jsonString}")
                        if(!jsonString.isNullOrEmpty()){
                            val msg = JSONObject(jsonString).getString("message")
                            Log.d("TAG", "loginUser: error create by ${msg}")
                            _loginResponseLiveData.postValue( NetworkResult.Error(message = msg))
                        }else{
                            _loginResponseLiveData.postValue( NetworkResult.Error(message = "Something went wrong, Please try again later."))
                        }

                    }catch (e: Exception){
                        Log.d("TAG", "loginUser: data => upper ${e}")
                        _loginResponseLiveData.postValue(
                            NetworkResult.Error(response.errorBody()?.string()
                                ?.let { JSONObject(it) }?.getString("message")))
                    }
                }

            } catch (e: Exception) {
                _loginResponseLiveData.postValue(
                    NetworkResult.Error(
                        exceptionHandler.handleException(
                            e
                        )
                    )
                )
            }
        }else{
            _loginResponseLiveData.postValue(
                NetworkResult.Error("No internet connection" )
            )
        }
    }

    private fun handleLoginResponse(response: Response<LoginModel>) {
        if (response.isSuccessful && response.body() != null) {
            _loginResponseLiveData.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null){
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            _loginResponseLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))
        }
        else{
            _loginResponseLiveData.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }
//********************** Login Opertaion ******************************************




}