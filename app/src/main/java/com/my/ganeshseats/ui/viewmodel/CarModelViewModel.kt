package com.my.ganeshseats.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.my.ganeshseats.Utils.NetworkResult
import com.my.ganeshseats.data.response.AddBrandResModel
import com.my.ganeshseats.data.response.AddEditSeatModel
import com.my.ganeshseats.data.response.AddModelRes
import com.my.ganeshseats.data.response.BrandModel
import com.my.ganeshseats.data.response.CarModelRes
import com.my.ganeshseats.data.response.CategoryResModel
import com.my.ganeshseats.data.response.DeleteSeatsModel
import com.my.ganeshseats.data.response.SeatResModel
import com.my.ganeshseats.data.response.StockChangeResModel
import com.my.raido.repository.UserRepository
import com.my.raido.repository.UserRoomDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CarModelViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel(){

    //********************** Fetch Brands Opertaion ******************************************
    val fetchBrandResponseLiveData: LiveData<NetworkResult<BrandModel>>
        get() = userRepository.brandResponseLiveData


    fun fetchBrands(){
        viewModelScope.launch {
            userRepository.fetchBrands()
        }
    }

    fun clearBrandRes(){
        userRepository._brandResponseLiveData.postValue(NetworkResult.Empty())
    }

//********************** Fetch Brands Opertaion ******************************************

    //********************** Add Brands Opertaion ******************************************
    val addBrandResponseLiveData: LiveData<NetworkResult<AddBrandResModel>>
        get() = userRepository.addBrandResponseLiveData


    fun addBrands(brandName: String, brandImage: File?){
        viewModelScope.launch {
            userRepository.addBrands(brandName = brandName, brandImage = brandImage)
        }
    }

    fun clearAddBrandRes(){
        userRepository._addBrandResponseLiveData.postValue(NetworkResult.Empty())
    }

//********************** Add Brands Opertaion ******************************************

//********************** Update Brands Opertaion ******************************************
    val updateBrandResponseLiveData: LiveData<NetworkResult<AddBrandResModel>>
        get() = userRepository.updateBrandResponseLiveData


    fun updateBrands(brandId: String, brandName: String, brandImage: File?){
        viewModelScope.launch {
            userRepository.updateBrands(brandId = brandId, brandName = brandName, brandImage = brandImage)
        }
    }

    fun clearUpdateBrandRes(){
        userRepository._updateBrandResponseLiveData.postValue(NetworkResult.Empty())
    }

//********************** Update Brands Opertaion ******************************************


    //********************** Fetch Brand Models Opertaion ******************************************
    val fetchBrandModelResponseLiveData: LiveData<NetworkResult<CarModelRes>>
        get() = userRepository.brandModelResponseLiveData

    fun fetchBrandModels(brandId: String){
        Log.d("TAG", "fetchBrandModels: brandId => $brandId")
        viewModelScope.launch {
            userRepository.fetchBrandModels(brandId = brandId)
        }
    }

    fun clearBrandModelRes(){
        userRepository._brandModelResponseLiveData.postValue(NetworkResult.Empty())
    }

//********************** Fetch Brand Models Opertaion ******************************************

    //********************** Add Brand Models Opertaion ******************************************
    val addBrandModelResponseLiveData: LiveData<NetworkResult<AddModelRes>>
        get() = userRepository.addModelResponseLiveData

    fun addBrandModels(brandId: String, modelName: String, modelImage: File?){
        viewModelScope.launch {
            userRepository.addModel(brandId = brandId, modelName = modelName, modelImage= modelImage)
        }
    }

    fun clearAddModelRes(){
        userRepository._addModelResponseLiveData.postValue(NetworkResult.Empty())
    }

//********************** Add Brand Models Opertaion ******************************************

//********************** Update Brand Models Opertaion ******************************************
    val updateBrandModelResponseLiveData: LiveData<NetworkResult<AddModelRes>>
        get() = userRepository.updateModelResponseLiveData

    fun updateBrandModels(brandId: String,modelId: String, status: String, modelName: String, modelImage: File?){
        viewModelScope.launch {
            userRepository.updateModel(brandId = brandId,modelId = modelId, status= status, modelName = modelName, modelImage= modelImage)
        }
    }

    fun clearUpdateModelRes(){
        userRepository._updateModelResponseLiveData.postValue(NetworkResult.Empty())
    }

//********************** Update Brand Models Opertaion ******************************************

//********************** Fetch Category Models Opertaion ******************************************
    val fetchCategoryModelResponseLiveData: LiveData<NetworkResult<CategoryResModel>>
        get() = userRepository.categoryResponseLiveData

    fun fetchCategory(){
        viewModelScope.launch {
            userRepository.fetchCategories()
        }
    }

    fun clearCategoryRes(){
        userRepository._categoryResponseLiveData.postValue(NetworkResult.Empty())
    }

//********************** Fetch Category Models Opertaion ******************************************

//********************** Change Stock Status Opertaion ******************************************
    val changeStockDataResponseLiveData: LiveData<NetworkResult<StockChangeResModel>>
        get() = userRepository.stockStatusResponseLiveData

    fun updateStockStatus(seatId: String, stockStatus: String){
        viewModelScope.launch {
            userRepository.changeStockStatus(seatId = seatId, stockStatus = stockStatus)
        }
    }

    fun clearStockStatusRes(){
        userRepository._stockStatusResponseLiveData.postValue(NetworkResult.Empty())
    }

//********************** Change Stock Status Opertaion ******************************************

//********************** Fetch Seat Data Opertaion ******************************************
    val fetchSeatDataResponseLiveData: LiveData<NetworkResult<SeatResModel>>
        get() = userRepository.seatDetailResponseLiveData

    fun fetchSeatData(modelId: String){
        viewModelScope.launch {
            userRepository.fetchSeatData(modelId)
        }
    }

    fun clearSeatRes(){
        userRepository._seatDetailResponseLiveData.postValue(NetworkResult.Empty())
    }

//********************** Fetch Seat Data Opertaion ******************************************


//********************** Add Seat Data Opertaion ******************************************
    val addSeatDataResponseLiveData: LiveData<NetworkResult<AddEditSeatModel>>
        get() = userRepository.addSeatResponseLiveData

    fun addSeatData(seatName: String, carBrandId: String, carModelId: String, categoryId: String, seatImage: File?, manufactureId: String, manufacturedQuantity: String){
        viewModelScope.launch {
            userRepository.addSeats(
                seatName = seatName,
                carModelId = carModelId,
                carBrandId = carBrandId,
                categoryId = categoryId,
                manufactureId = manufactureId,
                manufacturedQuantity = manufacturedQuantity,
                seatImage = seatImage
            )
        }
    }

    fun clearAddSeatRes(){
        userRepository._addSeatResponseLiveData.postValue(NetworkResult.Empty())
    }

//********************** Add Seat Data Opertaion ******************************************

//********************** Update Seat Data Opertaion ******************************************
    val updateSeatDataResponseLiveData: LiveData<NetworkResult<AddEditSeatModel>>
        get() = userRepository.updateSeatResponseLiveData

    fun updateSeatData(seatId: String, status: String, seatName: String, carBrandId: String, carModelId: String, categoryId: String, seatImage: File?, manufactureId: String, manufacturedQuantity: String){
        viewModelScope.launch {
            userRepository.updateSeats(
                seatId = seatId,
                status = status,
                seatName = seatName,
                carModelId = carModelId,
                carBrandId = carBrandId,
                categoryId = categoryId,
                manufactureId = manufactureId,
                manufacturedQuantity = manufacturedQuantity,
                seatImage = seatImage
            )
        }
    }

    fun clearUpdateSeatRes(){
        userRepository._updateSeatResponseLiveData.postValue(NetworkResult.Empty())
    }

//********************** Update Seat Data Opertaion ******************************************


//********************** Delete Seat Data Opertaion ******************************************
    val deleteSeatDataResponseLiveData: LiveData<NetworkResult<DeleteSeatsModel>>
        get() = userRepository.deleteSeatResponseLiveData

    fun deleteSeatData(seatId: ArrayList<String>){
        viewModelScope.launch {
            userRepository.deleteSeats(
                seatIds = seatId
            )
        }
    }

    fun clearDeleteSeatRes(){
        userRepository._deleteSeatResponseLiveData.postValue(NetworkResult.Empty())
    }

//********************** Delete Seat Data Opertaion ******************************************


}