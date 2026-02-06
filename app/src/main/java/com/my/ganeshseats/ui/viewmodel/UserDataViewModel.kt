package com.my.ganeshseats.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.my.ganeshseats.Utils.NetworkResult
import com.my.ganeshseats.Utils.TokenManager
import com.my.ganeshseats.data.Database.DataModel.User
import com.my.ganeshseats.data.response.BrandModel
import com.my.ganeshseats.data.response.CarModelRes
import com.my.ganeshseats.data.response.LoginModel
import com.my.raido.repository.UserRepository
import com.my.raido.repository.UserRoomDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserDataViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val userRoomDataRepository: UserRoomDataRepository,
    private val tokenManager: TokenManager,
): ViewModel(){



//    val getAllUser = userRoomDataRepository.getAllUser().asLiveData()

    val allUsers: LiveData<List<User>> = userRoomDataRepository.getAllUser()
        .catch { exception ->
            // Handle any exceptions here
            Log.e("UserViewModel", "Error loading users: ${exception.message}")
        }
        .asLiveData()

    fun insertUser(userData: User){
        viewModelScope.launch (Dispatchers.IO){
            userRoomDataRepository.insertUser(userData = userData)
        }
    }

    fun updateUser(userData: User){
        viewModelScope.launch (Dispatchers.IO){
            userRoomDataRepository.updateUser(userData = userData)
        }
    }

    fun logoutUser(){
        viewModelScope.launch (Dispatchers.IO){
            userRoomDataRepository.nukeTable()
            tokenManager.removeToken()
        }
    }

    fun deleteUser(userData: User){
        viewModelScope.launch (Dispatchers.IO){
            userRoomDataRepository.deleteUser(userData = userData)
        }
    }

//    ********************************************************************************************************








}