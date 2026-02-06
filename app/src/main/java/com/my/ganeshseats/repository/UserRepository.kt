package com.my.raido.repository

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.my.ganeshseats.Helper.NetworkHelper
import com.my.ganeshseats.Utils.Helper
import com.my.ganeshseats.Utils.NetworkResult
import com.my.ganeshseats.api.UserAPI
import com.my.ganeshseats.data.response.AddBrandResModel
import com.my.ganeshseats.data.response.AddEditSeatModel
import com.my.ganeshseats.data.response.AddModelRes
import com.my.ganeshseats.data.response.BrandData
import com.my.ganeshseats.data.response.BrandModel
import com.my.ganeshseats.data.response.CarModelRes
import com.my.ganeshseats.data.response.CategoryResModel
import com.my.ganeshseats.data.response.ChangedStockData
import com.my.ganeshseats.data.response.DeleteSeatsModel
import com.my.ganeshseats.data.response.LoginModel
import com.my.ganeshseats.data.response.SeatResModel
import com.my.ganeshseats.data.response.StockChangeResModel
import com.my.ganeshseats.di.exception.NetworkExceptionHandler
import okhttp3.MultipartBody
import org.json.JSONObject
import retrofit2.Response
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class UserRepository @Inject constructor(private val userApi: UserAPI, private val networkHelper: NetworkHelper, private val exceptionHandler: NetworkExceptionHandler) {

    //********************** Fetch Brands ******************************************
    val _brandResponseLiveData = MutableLiveData<NetworkResult<BrandModel>>()
    val brandResponseLiveData: LiveData<NetworkResult<BrandModel>>
        get() = _brandResponseLiveData

    suspend fun fetchBrands() {
        _brandResponseLiveData.postValue(NetworkResult.Loading())
        if(networkHelper.isNetworkConnected()) {
            try {
                val response = userApi.fetchBrands()
                if (response.isSuccessful) {
                    handleBrandResponse(response)
                } else {
                    try {
                        val jsonString = response.errorBody()?.toString()
                        Log.d("TAG", "fetchbrands: error create by ${jsonString}")
                        if(!jsonString.isNullOrEmpty()){
                            val msg = JSONObject(jsonString).getString("message")
                            Log.d("TAG", "fetchbrands: error create by ${msg}")
                            _brandResponseLiveData.postValue( NetworkResult.Error(message = msg))
                        }else{
                            _brandResponseLiveData.postValue( NetworkResult.Error(message = "Something went wrong, Please try again later."))
                        }

                    }catch (e: Exception){
                        Log.d("TAG", "fetchbrands: data => upper ${e}")
                        _brandResponseLiveData.postValue(
                            NetworkResult.Error(response.errorBody()?.string()
                                ?.let { JSONObject(it) }?.getString("message")))
                    }
                }

            } catch (e: Exception) {
                Log.d("TAG", "fetchBrands: error is $e")
                _brandResponseLiveData.postValue(
                    NetworkResult.Error(
                        exceptionHandler.handleException(
                            e
                        )
                    )
                )
            }
        }else{
            _brandResponseLiveData.postValue(
                NetworkResult.Error("No internet connection" )
            )
        }
    }

    private fun handleBrandResponse(response: Response<BrandModel>) {
        if (response.isSuccessful && response.body() != null) {
            _brandResponseLiveData.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null){
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            _brandResponseLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))
        }
        else{
            _brandResponseLiveData.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }
//********************** Fetch Brands  ******************************************


//********************** Add Brands ******************************************
    val _addBrandResponseLiveData = MutableLiveData<NetworkResult<AddBrandResModel>>()
    val addBrandResponseLiveData: LiveData<NetworkResult<AddBrandResModel>>
        get() = _addBrandResponseLiveData

    suspend fun addBrands(brandName: String, brandImage: File? = null) {
        _addBrandResponseLiveData.postValue(NetworkResult.Loading())
        if(networkHelper.isNetworkConnected()) {
            try {
                val response = if(brandImage != null) userApi.addBrand(
                    brandName = Helper.getMultiPartFormRequestBody(brandName),
                    brandImage =  Helper.prepareFilePart(
                        "image",
                        brandImage
                    )
                )
                else
                    userApi.addBrand(
                        brandName = Helper.getMultiPartFormRequestBody(brandName)
                    )
                if (response.isSuccessful) {
                    handleAddBrandResponse(response)
                } else {
                    try {
                        val jsonString = response.errorBody()?.toString()
                        Log.d("TAG", "addBrands: error create by ${jsonString}")
                        if(!jsonString.isNullOrEmpty()){
                            val msg = JSONObject(jsonString).getString("message")
                            Log.d("TAG", "addBrands: error create by ${msg}")
                            _addBrandResponseLiveData.postValue( NetworkResult.Error(message = msg))
                        }else{
                            _addBrandResponseLiveData.postValue( NetworkResult.Error(message = "Something went wrong, Please try again later."))
                        }

                    }catch (e: Exception){
                        Log.d("TAG", "addBrands: data => upper ${e}")
                        _addBrandResponseLiveData.postValue(
                            NetworkResult.Error(response.errorBody()?.string()
                                ?.let { JSONObject(it) }?.getString("message")))
                    }
                }

            } catch (e: Exception) {
                Log.d("TAG", "addBrands: error is $e")
                _addBrandResponseLiveData.postValue(
                    NetworkResult.Error(
                        exceptionHandler.handleException(
                            e
                        )
                    )
                )
            }
        }else{
            _addBrandResponseLiveData.postValue(
                NetworkResult.Error("No internet connection" )
            )
        }
    }

    private fun handleAddBrandResponse(response: Response<AddBrandResModel>) {
        if (response.isSuccessful && response.body() != null) {
            _addBrandResponseLiveData.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null){
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            _addBrandResponseLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))
        }
        else{
            _addBrandResponseLiveData.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }
//********************** Add Brands  ******************************************

    //********************** Update Brands ******************************************
    val _updateBrandResponseLiveData = MutableLiveData<NetworkResult<AddBrandResModel>>()
    val updateBrandResponseLiveData: LiveData<NetworkResult<AddBrandResModel>>
        get() = _updateBrandResponseLiveData

    suspend fun updateBrands(brandId: String, brandName: String, brandImage: File? = null) {
        _updateBrandResponseLiveData.postValue(NetworkResult.Loading())
        if(networkHelper.isNetworkConnected()) {
            try {
                val response = if(brandImage != null){
                    userApi.updateBrand(
                        brandId = brandId,
                        brandName = Helper.getMultiPartFormRequestBody(brandName),
                        brandImage =  Helper.prepareFilePart(
                            "image",
                            brandImage
                        )
                    )
                       } else {
                            userApi.updateBrand(
                                brandId = brandId,
                                brandName = Helper.getMultiPartFormRequestBody(brandName)
                            )
                        }
                if (response.isSuccessful) {
                    handleUpdateBrandResponse(response)
                } else {
                    try {
                        val jsonString = response.errorBody()?.toString()
                        Log.d("TAG", "updateBrands: error create by ${jsonString}")
                        if(!jsonString.isNullOrEmpty()){
                            val msg = JSONObject(jsonString).getString("message")
                            Log.d("TAG", "updateBrands: error create by ${msg}")
                            _updateBrandResponseLiveData.postValue( NetworkResult.Error(message = msg))
                        }else{
                            _updateBrandResponseLiveData.postValue( NetworkResult.Error(message = "Something went wrong, Please try again later."))
                        }

                    }catch (e: Exception){
                        Log.d("TAG", "updateBrands: data => upper ${e}")
                        _updateBrandResponseLiveData.postValue(
                            NetworkResult.Error(response.errorBody()?.string()
                                ?.let { JSONObject(it) }?.getString("message")))
                    }
                }

            } catch (e: Exception) {
                Log.d("TAG", "updateBrands: error is $e")
                _updateBrandResponseLiveData.postValue(
                    NetworkResult.Error(
                        exceptionHandler.handleException(
                            e
                        )
                    )
                )
            }
        }else{
            _updateBrandResponseLiveData.postValue(
                NetworkResult.Error("No internet connection" )
            )
        }
    }

    private fun handleUpdateBrandResponse(response: Response<AddBrandResModel>) {
        if (response.isSuccessful && response.body() != null) {
            _updateBrandResponseLiveData.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null){
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            _updateBrandResponseLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))
        }
        else{
            _updateBrandResponseLiveData.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }
//********************** Update Brands  ******************************************



    //********************** Fetch Brand Models ******************************************
    val _brandModelResponseLiveData = MutableLiveData<NetworkResult<CarModelRes>>()
    val brandModelResponseLiveData: LiveData<NetworkResult<CarModelRes>>
        get() = _brandModelResponseLiveData

    suspend fun fetchBrandModels(brandId: String) {
        _brandModelResponseLiveData.postValue(NetworkResult.Loading())
        if(networkHelper.isNetworkConnected()) {
            try {
                val response = userApi.fetchCarModels(brandId = brandId)
                if (response.isSuccessful) {
                    handleBrandModelResponse(response)
                } else {
                    try {
                        val jsonString = response.errorBody()?.toString()
                        Log.d("TAG", "fetchBrandModels: error create by ${jsonString}")
                        if(!jsonString.isNullOrEmpty()){
                            val msg = JSONObject(jsonString).getString("message")
                            Log.d("TAG", "fetchBrandModels: error create by ${msg}")
                            _brandModelResponseLiveData.postValue( NetworkResult.Error(message = msg))
                        }else{
                            _brandModelResponseLiveData.postValue( NetworkResult.Error(message = "Something went wrong, Please try again later."))
                        }

                    }catch (e: Exception){
                        Log.d("TAG", "fetchBrandModels: data => upper ${e}")
                        _brandModelResponseLiveData.postValue(
                            NetworkResult.Error(response.errorBody()?.string()
                                ?.let { JSONObject(it) }?.getString("message")))
                    }
                }

            } catch (e: Exception) {
                Log.d("TAG", "fetchBrandModels: error is $e")
                _brandModelResponseLiveData.postValue(
                    NetworkResult.Error(
                        exceptionHandler.handleException(
                            e
                        )
                    )
                )
            }
        }else{
            _brandModelResponseLiveData.postValue(
                NetworkResult.Error("No internet connection" )
            )
        }
    }

    private fun handleBrandModelResponse(response: Response<CarModelRes>) {
        if (response.isSuccessful && response.body() != null) {
            _brandModelResponseLiveData.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null){
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            _brandModelResponseLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))
        }
        else{
            _brandModelResponseLiveData.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }
//********************** Fetch Brand Models  ******************************************

    //********************** Add Models ******************************************
    val _addModelResponseLiveData = MutableLiveData<NetworkResult<AddModelRes>>()
    val addModelResponseLiveData: LiveData<NetworkResult<AddModelRes>>
        get() = _addModelResponseLiveData

    suspend fun addModel(brandId: String, modelName: String, modelImage: File? = null) {
        _addModelResponseLiveData.postValue(NetworkResult.Loading())
        if(networkHelper.isNetworkConnected()) {
            try {
                val response = if(modelImage != null) userApi.addCarModel(
                        brandId = Helper.getMultiPartFormRequestBody(brandId),
                        brandModelName = Helper.getMultiPartFormRequestBody(modelName),
                        brandModelImage =  Helper.prepareFilePart(
                            "image",
                            modelImage
                        )
                    )
                else userApi.addCarModel(
                    brandId = Helper.getMultiPartFormRequestBody(brandId),
                    brandModelName = Helper.getMultiPartFormRequestBody(modelName)
                )
                if (response.isSuccessful) {
                    handleAddModelResponse(response)
                } else {
                    try {
                        val jsonString = response.errorBody()?.toString()
                        Log.d("TAG", "addModel: error create by ${jsonString}")
                        if(!jsonString.isNullOrEmpty()){
                            val msg = JSONObject(jsonString).getString("message")
                            Log.d("TAG", "addModel: error create by ${msg}")
                            _addModelResponseLiveData.postValue( NetworkResult.Error(message = msg))
                        }else{
                            _addModelResponseLiveData.postValue( NetworkResult.Error(message = "Something went wrong, Please try again later."))
                        }

                    }catch (e: Exception){
                        Log.d("TAG", "addModel: data => upper ${e}")
                        _addModelResponseLiveData.postValue(
                            NetworkResult.Error(response.errorBody()?.string()
                                ?.let { JSONObject(it) }?.getString("message")))
                    }
                }

            } catch (e: Exception) {
                Log.d("TAG", "addModel: error is $e")
                _addModelResponseLiveData.postValue(
                    NetworkResult.Error(
                        exceptionHandler.handleException(
                            e
                        )
                    )
                )
            }
        }else{
            _addModelResponseLiveData.postValue(
                NetworkResult.Error("No internet connection" )
            )
        }
    }

    private fun handleAddModelResponse(response: Response<AddModelRes>) {
        if (response.isSuccessful && response.body() != null) {
            _addModelResponseLiveData.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null){
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            _addModelResponseLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))
        }
        else{
            _addModelResponseLiveData.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }
//********************** Add Models  ******************************************


//********************** Update Models ******************************************
    val _updateModelResponseLiveData = MutableLiveData<NetworkResult<AddModelRes>>()
    val updateModelResponseLiveData: LiveData<NetworkResult<AddModelRes>>
        get() = _updateModelResponseLiveData

    suspend fun updateModel(brandId: String, modelId: String, status: String, modelName: String, modelImage: File? = null) {
        _updateModelResponseLiveData.postValue(NetworkResult.Loading())
        if(networkHelper.isNetworkConnected()) {
            try {
                val response = if(modelImage != null){
                    userApi.updateCarModel(
                        modelId = modelId,
                        brandId = Helper.getMultiPartFormRequestBody(brandId),
                        status = Helper.getMultiPartFormRequestBody(status),
                        brandModelName = Helper.getMultiPartFormRequestBody(modelName),
                        brandModelImage =  Helper.prepareFilePart(
                            "image",
                            modelImage
                        )
                    )
                } else {
                    userApi.updateCarModel(
                        modelId = modelId,
                        brandId = Helper.getMultiPartFormRequestBody(brandId),
                        status = Helper.getMultiPartFormRequestBody(status),
                        brandModelName = Helper.getMultiPartFormRequestBody(modelName)
                    )
                }


                if (response.isSuccessful) {
                    handleUpdateModelResponse(response)
                } else {
                    try {
                        val jsonString = response.errorBody()?.toString()
                        Log.d("TAG", "updateModel: error create by ${jsonString}")
                        if(!jsonString.isNullOrEmpty()){
                            val msg = JSONObject(jsonString).getString("message")
                            Log.d("TAG", "updateModel: error create by ${msg}")
                            _updateModelResponseLiveData.postValue( NetworkResult.Error(message = msg))
                        }else{
                            _updateModelResponseLiveData.postValue( NetworkResult.Error(message = "Something went wrong, Please try again later."))
                        }

                    }catch (e: Exception){
                        Log.d("TAG", "updateModel: data => upper ${e}")
                        _updateModelResponseLiveData.postValue(
                            NetworkResult.Error(response.errorBody()?.string()
                                ?.let { JSONObject(it) }?.getString("message")))
                    }
                }

            } catch (e: Exception) {
                Log.d("TAG", "updateModel: error is $e")
                _updateModelResponseLiveData.postValue(
                    NetworkResult.Error(
                        exceptionHandler.handleException(
                            e
                        )
                    )
                )
            }
        }else{
            _updateModelResponseLiveData.postValue(
                NetworkResult.Error("No internet connection" )
            )
        }
    }

    private fun handleUpdateModelResponse(response: Response<AddModelRes>) {
        if (response.isSuccessful && response.body() != null) {
            _updateModelResponseLiveData.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null){
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            _updateModelResponseLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))
        }
        else{
            _updateModelResponseLiveData.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }
//********************** Update Models  ******************************************

//********************** Category Response Models ******************************************
    val _categoryResponseLiveData = MutableLiveData<NetworkResult<CategoryResModel>>()
    val categoryResponseLiveData: LiveData<NetworkResult<CategoryResModel>>
        get() = _categoryResponseLiveData

    suspend fun fetchCategories() {
        _categoryResponseLiveData.postValue(NetworkResult.Loading())
        if(networkHelper.isNetworkConnected()) {
            try {
                val response = userApi.fetchCategory()
                if (response.isSuccessful) {
                    handleCategoryResponse(response)
                } else {
                    try {
                        val jsonString = response.errorBody()?.toString()
                        Log.d("TAG", "fetchCategories: error create by ${jsonString}")
                        if(!jsonString.isNullOrEmpty()){
                            val msg = JSONObject(jsonString).getString("message")
                            Log.d("TAG", "fetchCategories: error create by ${msg}")
                            _categoryResponseLiveData.postValue( NetworkResult.Error(message = msg))
                        }else{
                            _categoryResponseLiveData.postValue( NetworkResult.Error(message = "Something went wrong, Please try again later."))
                        }

                    }catch (e: Exception){
                        Log.d("TAG", "fetchCategories: data => upper ${e}")
                        _categoryResponseLiveData.postValue(
                            NetworkResult.Error(response.errorBody()?.string()
                                ?.let { JSONObject(it) }?.getString("message")))
                    }
                }

            } catch (e: Exception) {
                Log.d("TAG", "fetchCategories: error is $e")
                _categoryResponseLiveData.postValue(
                    NetworkResult.Error(
                        exceptionHandler.handleException(
                            e
                        )
                    )
                )
            }
        }else{
            _categoryResponseLiveData.postValue(
                NetworkResult.Error("No internet connection" )
            )
        }
    }

    private fun handleCategoryResponse(response: Response<CategoryResModel>) {
        if (response.isSuccessful && response.body() != null) {
            _categoryResponseLiveData.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null){
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            _categoryResponseLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))
        }
        else{
            _categoryResponseLiveData.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }
//********************** Category Response Models  ******************************************


//********************** Seat Details ******************************************
    val _seatDetailResponseLiveData = MutableLiveData<NetworkResult<SeatResModel>>()
    val seatDetailResponseLiveData: LiveData<NetworkResult<SeatResModel>>
        get() = _seatDetailResponseLiveData

    suspend fun fetchSeatData(modelId: String) {
        _seatDetailResponseLiveData.postValue(NetworkResult.Loading())
        if(networkHelper.isNetworkConnected()) {
            try {
                Log.d("TAG", "fetchSeatData: api called ")
                val response = userApi.fetchSeats(modelId)
                if (response.isSuccessful) {
                    handleSeatDetailResponse(response)
                } else {
                    try {
                        val jsonString = response.errorBody()?.toString()
                        Log.d("TAG", "fetchSeatData: error create by ${jsonString}")
                        if(!jsonString.isNullOrEmpty()){
                            val msg = JSONObject(jsonString).getString("message")
                            Log.d("TAG", "fetchSeatData: error create by ${msg}")
                            _seatDetailResponseLiveData.postValue( NetworkResult.Error(message = msg))
                        }else{
                            _seatDetailResponseLiveData.postValue( NetworkResult.Error(message = "Something went wrong, Please try again later."))
                        }

                    }catch (e: Exception){
                        Log.d("TAG", "fetchSeatData: data => upper ${e}")
                        _seatDetailResponseLiveData.postValue(
                            NetworkResult.Error(response.errorBody()?.string()
                                ?.let { JSONObject(it) }?.getString("message")))
                    }
                }

            } catch (e: Exception) {
                Log.d("TAG", "fetchSeatData: error is $e")
                _seatDetailResponseLiveData.postValue(
                    NetworkResult.Error(
                        exceptionHandler.handleException(
                            e
                        )
                    )
                )
            }
        }else{
            _seatDetailResponseLiveData.postValue(
                NetworkResult.Error("No internet connection" )
            )
        }
    }

    private fun handleSeatDetailResponse(response: Response<SeatResModel>) {
        if (response.isSuccessful && response.body() != null) {
            _seatDetailResponseLiveData.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null){
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            _seatDetailResponseLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))
        }
        else{
            _seatDetailResponseLiveData.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }
//********************** Seat Details  ******************************************

    //********************** Change Stock Status ******************************************
    val _stockStatusResponseLiveData = MutableLiveData<NetworkResult<StockChangeResModel>>()
    val stockStatusResponseLiveData: LiveData<NetworkResult<StockChangeResModel>>
        get() = _stockStatusResponseLiveData

    suspend fun changeStockStatus(seatId: String, stockStatus: String) {
        _stockStatusResponseLiveData.postValue(NetworkResult.Loading())
        if(networkHelper.isNetworkConnected()) {
            try {
                val response = userApi.changeStockStatus(
                    seatId = seatId,
                    stockStatus = stockStatus,
                )
                if (response.isSuccessful) {
                    handleChangeStockResponse(response)
                } else {
                    try {
                        val jsonString = response.errorBody()?.toString()
                        Log.d("TAG", "changeStockStatus: error create by ${jsonString}")
                        if(!jsonString.isNullOrEmpty()){
                            val msg = JSONObject(jsonString).getString("message")
                            Log.d("TAG", "changeStockStatus: error create by ${msg}")
                            _stockStatusResponseLiveData.postValue( NetworkResult.Error(message = msg))
                        }else{
                            _stockStatusResponseLiveData.postValue( NetworkResult.Error(message = "Something went wrong, Please try again later."))
                        }

                    }catch (e: Exception){
                        Log.d("TAG", "changeStockStatus: data => upper ${e}")
                        _stockStatusResponseLiveData.postValue(
                            NetworkResult.Error(response.errorBody()?.string()
                                ?.let { JSONObject(it) }?.getString("message")))
                    }
                }

            } catch (e: Exception) {
                Log.d("TAG", "changeStockStatus: error is $e")
                _stockStatusResponseLiveData.postValue(
                    NetworkResult.Error(
                        exceptionHandler.handleException(
                            e
                        )
                    )
                )
            }
        }else{
            _stockStatusResponseLiveData.postValue(
                NetworkResult.Error("No internet connection" )
            )
        }
    }

    private fun handleChangeStockResponse(response: Response<StockChangeResModel>) {
        if (response.isSuccessful && response.body() != null) {
            _stockStatusResponseLiveData.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null){
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            _stockStatusResponseLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))
        }
        else{
            _stockStatusResponseLiveData.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }
    //********************** Change Stock Status  ******************************************

//********************** Add Seats ******************************************
    val _addSeatResponseLiveData = MutableLiveData<NetworkResult<AddEditSeatModel>>()
    val addSeatResponseLiveData: LiveData<NetworkResult<AddEditSeatModel>>
        get() = _addSeatResponseLiveData

    suspend fun addSeats(seatName: String, carBrandId: String, carModelId: String, categoryId: String, seatImage: File? = null, manufactureId: String, manufacturedQuantity: String) {
        _addSeatResponseLiveData.postValue(NetworkResult.Loading())
        if(networkHelper.isNetworkConnected()) {
            try {
                val response = if(seatImage != null) userApi.addSeats(
                            seatName = Helper.getMultiPartFormRequestBody(seatName),
                            carBrandId = Helper.getMultiPartFormRequestBody(carBrandId),
                            carModelId = Helper.getMultiPartFormRequestBody(carModelId),
                            categoryId = Helper.getMultiPartFormRequestBody(categoryId),
                            manufactureId = Helper.getMultiPartFormRequestBody(manufactureId),
                            manufacturedQuantity = Helper.getMultiPartFormRequestBody(manufacturedQuantity),
                            seatImage =  Helper.prepareFilePart(
                                "image",
                                seatImage
                            )
                        )
                else userApi.addSeats(
                        seatName = Helper.getMultiPartFormRequestBody(seatName),
                        carBrandId = Helper.getMultiPartFormRequestBody(carBrandId),
                        carModelId = Helper.getMultiPartFormRequestBody(carModelId),
                        categoryId = Helper.getMultiPartFormRequestBody(categoryId),
                        manufactureId = Helper.getMultiPartFormRequestBody(manufactureId),
                        manufacturedQuantity = Helper.getMultiPartFormRequestBody(manufacturedQuantity)
                    )
                if (response.isSuccessful) {
                    handleAddSeatResponse(response)
                } else {
                    try {
                        val jsonString = response.errorBody()?.toString()
                        Log.d("TAG", "addSeats: error create by ${jsonString}")
                        if(!jsonString.isNullOrEmpty()){
                            val msg = JSONObject(jsonString).getString("message")
                            Log.d("TAG", "addSeats: error create by ${msg}")
                            _addSeatResponseLiveData.postValue( NetworkResult.Error(message = msg))
                        }else{
                            _addSeatResponseLiveData.postValue( NetworkResult.Error(message = "Something went wrong, Please try again later."))
                        }

                    }catch (e: Exception){
                        Log.d("TAG", "addSeats: data => upper ${e}")
                        _addSeatResponseLiveData.postValue(
                            NetworkResult.Error(response.errorBody()?.string()
                                ?.let { JSONObject(it) }?.getString("message")))
                    }
                }

            } catch (e: Exception) {
                Log.d("TAG", "addSeats: error is $e")
                _addSeatResponseLiveData.postValue(
                    NetworkResult.Error(
                        exceptionHandler.handleException(
                            e
                        )
                    )
                )
            }
        }else{
            _addSeatResponseLiveData.postValue(
                NetworkResult.Error("No internet connection" )
            )
        }
    }

    private fun handleAddSeatResponse(response: Response<AddEditSeatModel>) {
        if (response.isSuccessful && response.body() != null) {
            _addSeatResponseLiveData.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null){
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            _addSeatResponseLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))
        }
        else{
            _addSeatResponseLiveData.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }
//********************** Add Seats  ******************************************

//********************** Update Seats ******************************************
    val _updateSeatResponseLiveData = MutableLiveData<NetworkResult<AddEditSeatModel>>()
    val updateSeatResponseLiveData: LiveData<NetworkResult<AddEditSeatModel>>
        get() = _updateSeatResponseLiveData

    suspend fun updateSeats(seatId: String, status: String, seatName: String, carBrandId: String, carModelId: String, categoryId: String, seatImage: File?, manufactureId: String, manufacturedQuantity: String) {
        _updateSeatResponseLiveData.postValue(NetworkResult.Loading())
        if(networkHelper.isNetworkConnected()) {
            try {
                val response = if(seatImage != null) userApi.updateSeats(
                        seatId = seatId,
                        seatName = Helper.getMultiPartFormRequestBody(seatName),
                        carBrandId = Helper.getMultiPartFormRequestBody(carBrandId),
                        carModelId = Helper.getMultiPartFormRequestBody(carModelId),
                        categoryId = Helper.getMultiPartFormRequestBody(categoryId),
                        status = Helper.getMultiPartFormRequestBody(status),
                        manufactureId = Helper.getMultiPartFormRequestBody(manufactureId),
                        manufacturedQuantity = Helper.getMultiPartFormRequestBody(manufacturedQuantity),
                        seatImage =  Helper.prepareFilePart(
                            "image",
                            seatImage
                        )
                    )
                else
                    userApi.updateSeats(
                        seatId = seatId,
                        seatName = Helper.getMultiPartFormRequestBody(seatName),
                        carBrandId = Helper.getMultiPartFormRequestBody(carBrandId),
                        carModelId = Helper.getMultiPartFormRequestBody(carModelId),
                        categoryId = Helper.getMultiPartFormRequestBody(categoryId),
                        status = Helper.getMultiPartFormRequestBody(status),
                        manufactureId = Helper.getMultiPartFormRequestBody(manufactureId),
                        manufacturedQuantity = Helper.getMultiPartFormRequestBody(manufacturedQuantity),
                    )

                Log.d("TAG", "updateSeats: response data is $response")

                if (response.isSuccessful) {
                    handleUpdateSeatResponse(response)
                } else {
                    try {
                        val jsonString = response.errorBody()?.toString()
                        Log.d("TAG", "updateSeats: error create by ${jsonString}")
                        if(!jsonString.isNullOrEmpty()){
                            val msg = JSONObject(jsonString).getString("message")
                            Log.d("TAG", "updateSeats: error create by ${msg}")
                            _updateSeatResponseLiveData.postValue( NetworkResult.Error(message = msg))
                        }else{
                            _updateSeatResponseLiveData.postValue( NetworkResult.Error(message = "Something went wrong, Please try again later."))
                        }

                    }catch (e: Exception){
                        Log.d("TAG", "updateSeats: data => upper ${e}")
                        _updateSeatResponseLiveData.postValue(
                            NetworkResult.Error(response.errorBody()?.string()
                                ?.let { JSONObject(it) }?.getString("message")))
                    }
                }

            } catch (e: Exception) {
                Log.d("TAG", "updateSeats: error is $e")
                _updateSeatResponseLiveData.postValue(
                    NetworkResult.Error(
                        exceptionHandler.handleException(
                            e
                        )
                    )
                )
            }
        }else{
            _updateSeatResponseLiveData.postValue(
                NetworkResult.Error("No internet connection" )
            )
        }
    }

    private fun handleUpdateSeatResponse(response: Response<AddEditSeatModel>) {
        if (response.isSuccessful && response.body() != null) {
            _updateSeatResponseLiveData.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null){
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            _updateSeatResponseLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))
        }
        else{
            _updateSeatResponseLiveData.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }
//********************** Update Seats  ******************************************

//********************** Delete Seats ******************************************
    val _deleteSeatResponseLiveData = MutableLiveData<NetworkResult<DeleteSeatsModel>>()
    val deleteSeatResponseLiveData: LiveData<NetworkResult<DeleteSeatsModel>>
        get() = _deleteSeatResponseLiveData

    suspend fun deleteSeats(seatIds: ArrayList<String>) {
        _updateSeatResponseLiveData.postValue(NetworkResult.Loading())
        if(networkHelper.isNetworkConnected()) {
            try {

                val parts = seatIds.map { id ->
                    MultipartBody.Part.createFormData("ids[]", id)
                }
                Log.d("TAG", "updateSeats: response data is $parts")

                val response =  userApi.deleteSeats(
                        part = parts
                    )

                Log.d("TAG", "updateSeats: response data is $response")

                if (response.isSuccessful) {
                    handleDeleteSeatResponse(response)
                } else {
                    try {
                        val jsonString = response.errorBody()?.toString()
                        Log.d("TAG", "updateSeats: error create by ${jsonString}")
                        if(!jsonString.isNullOrEmpty()){
                            val msg = JSONObject(jsonString).getString("message")
                            Log.d("TAG", "updateSeats: error create by ${msg}")
                            _deleteSeatResponseLiveData.postValue( NetworkResult.Error(message = msg))
                        }else{
                            _deleteSeatResponseLiveData.postValue( NetworkResult.Error(message = "Something went wrong, Please try again later."))
                        }

                    }catch (e: Exception){
                        Log.d("TAG", "updateSeats: data => upper ${e}")
                        _deleteSeatResponseLiveData.postValue(
                            NetworkResult.Error(response.errorBody()?.string()
                                ?.let { JSONObject(it) }?.getString("message")))
                    }
                }

            } catch (e: Exception) {
                Log.d("TAG", "updateSeats: error is $e")
                _deleteSeatResponseLiveData.postValue(
                    NetworkResult.Error(
                        exceptionHandler.handleException(
                            e
                        )
                    )
                )
            }
        }else{
            _deleteSeatResponseLiveData.postValue(
                NetworkResult.Error("No internet connection" )
            )
        }
    }

    private fun handleDeleteSeatResponse(response: Response<DeleteSeatsModel>) {
        if (response.isSuccessful && response.body() != null) {
            _deleteSeatResponseLiveData.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null){
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            _deleteSeatResponseLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))
        }
        else{
            _deleteSeatResponseLiveData.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }
//********************** Delete Seats  ******************************************

}