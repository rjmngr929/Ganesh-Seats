package com.my.ganeshseats.ui.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.my.ganeshseats.Utils.NetworkResult
import com.my.ganeshseats.data.response.LoginModel
import com.my.ganeshseats.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
): ViewModel(){

//********************** OTP Send Opertaion ******************************************
    val loginResponseLiveData: LiveData<NetworkResult<LoginModel>>
        get() = authRepository.loginResponseLiveData


    fun loginApi(email: String, password: String){
        viewModelScope.launch {
            authRepository.loginUser(email = email, password = password)
        }
    }

    fun clearLoginRes(){
        authRepository._loginResponseLiveData.postValue(NetworkResult.Empty())
    }

//********************** OTP Send Opertaion ******************************************




}