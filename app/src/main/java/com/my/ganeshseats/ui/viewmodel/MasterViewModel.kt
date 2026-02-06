package com.my.ganeshseats.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.my.ganeshseats.Utils.EventData
import com.my.ganeshseats.data.response.BrandData
import com.my.ganeshseats.data.response.CategoryDataModel
import com.my.ganeshseats.data.response.ManufactureModelSelect
import com.my.ganeshseats.data.response.SeatDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class MasterViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {

    val refreshData = MutableLiveData<Boolean>(false)

    // LiveData to store user information
    private val _selectedBrandData = MutableLiveData<BrandData?>()
    val selectedBrandData: LiveData<BrandData?> get() = _selectedBrandData

    // Function to update user data
    fun setBrandData(data: BrandData?) {
        _selectedBrandData.value = data
    }



//    ******************** Brand Data ****************************
    private val _brandDetailData = MutableLiveData<ArrayList<BrandData>>()
    val brandDetailData: LiveData<ArrayList<BrandData>> get() = _brandDetailData

    // Function to update user data
    fun setBrandDetailData(data: ArrayList<BrandData>) {
        _brandDetailData.value = ArrayList(data)
    }
//    ******************** Brand Data ****************************

//    ******************** Seat Array Data ****************************
    private val _seatArrayData = MutableLiveData<ArrayList<SeatDetail>>()
    val seatArrayData: LiveData<ArrayList<SeatDetail>> get() = _seatArrayData

    // Function to update user data
    fun setSeatArrayData(data: ArrayList<SeatDetail>) {
        _seatArrayData.value = data
    }
//    ******************** Seat Array Data ****************************

    //    ******************** Seat Data ****************************
    private val _selectedSeatId = MutableLiveData<Int>(0)
    val selectedSeatId: LiveData<Int> get() = _selectedSeatId

    // Function to update user data
    fun setSelectedSeatId(seatId: Int) {
        _selectedSeatId.value = seatId
    }
//    ******************** Seat Data ****************************

//    ******************** Seat Data ****************************
private val _seatDetailData = MutableLiveData<SeatDetail>()
    val seatDetailData: LiveData<SeatDetail> get() = _seatDetailData

    // Function to update user data
    fun setSeatDetailData(data: SeatDetail) {
        _seatDetailData.value = data
    }
//    ******************** Seat Data ****************************


//    ************** Category Data Model ***************
private val _categoryData = MutableLiveData<ArrayList<CategoryDataModel>?>()
    val categoryData: LiveData<ArrayList<CategoryDataModel>?> get() = _categoryData

    // Function to update user data
    fun setCategoryData(data: ArrayList<CategoryDataModel>) {
        _categoryData.value = data
    }

    private val _categoryList = MutableLiveData<ArrayList<String>?>()
    val categoryList: LiveData<ArrayList<String>?> get() = _categoryList

    // Function to update user data
    fun setCategoryList(data: ArrayList<String>) {
        _categoryList.value = data
    }
//    ************** Category Data Model ***************

//    ************** Manufacture Data Model ***************
    private val _manufactureData = MutableLiveData<ArrayList<ManufactureModelSelect>?>()
    val manufactureData: LiveData<ArrayList<ManufactureModelSelect>?> get() = _manufactureData

    // Function to update user data
    fun setManufactureData(data: ArrayList<ManufactureModelSelect>) {
        _manufactureData.value = data
    }

    private val _manufactureList = MutableLiveData<ArrayList<String>?>()
    val manufactureList: LiveData<ArrayList<String>?> get() = _manufactureList

    // Function to update user data
    fun setManufactureList(data: ArrayList<String>) {
        _manufactureList.value = data
    }
//    ************** Manufacture Data Model ***************

//    ********************* Selected Category **********************************
    // LiveData to store user information
    private val _selectedCategory = MutableLiveData<String>("")
    val selectedCategory: LiveData<String> get() = _selectedCategory

    // Function to update user data
    fun setSelectedCategory(data: String) {
        _selectedCategory.value = data
    }
//    ********************* Selected Category **********************************

//    ********************* Set Stock/Not **********************************
    // LiveData to store user information
    private val _updatedSeatData = MutableLiveData< SeatDetail?>( null)
    val updatedSeatData: LiveData< SeatDetail?> get() = _updatedSeatData

    // Function to update user data
    fun setUpdatedSeatData(data: SeatDetail?) {
        _updatedSeatData.value = data
    }
//    ********************* Set Stock/Not **********************************


}
