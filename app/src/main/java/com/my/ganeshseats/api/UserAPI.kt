package com.my.ganeshseats.api

import com.my.ganeshseats.data.response.AddBrandResModel
import com.my.ganeshseats.data.response.AddEditSeatModel
import com.my.ganeshseats.data.response.AddModelRes
import com.my.ganeshseats.data.response.BrandModel
import com.my.ganeshseats.data.response.CarModelRes
import com.my.ganeshseats.data.response.CategoryResModel
import com.my.ganeshseats.data.response.DeleteSeatsModel
import com.my.ganeshseats.data.response.ResponseModel
import com.my.ganeshseats.data.response.SeatResModel
import com.my.ganeshseats.data.response.StockChangeResModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface UserAPI {

    @GET("mobile/car-brands")
    suspend fun fetchBrands() : Response<BrandModel>

    @Multipart
    @POST("mobile/car-brands")
    suspend fun addBrand(
        @Part("name") brandName: RequestBody?,
        @Part brandImage: MultipartBody.Part?,
    ): Response<AddBrandResModel>

    @Multipart
    @POST("mobile/car-brands")
    suspend fun addBrand(
        @Part("name") brandName: RequestBody?,
    ): Response<AddBrandResModel>

    @Multipart
    @POST("mobile/car-brands/{brandId}")
    suspend fun updateBrand(
        @Path("brandId") brandId: String,
        @Part("name") brandName: RequestBody?,
        @Part brandImage: MultipartBody.Part?,
    ): Response<AddBrandResModel>

    @Multipart
    @POST("mobile/car-brands/{brandId}")
    suspend fun updateBrand(
        @Path("brandId") brandId: String,
        @Part("name") brandName: RequestBody?,
    ): Response<AddBrandResModel>

    @GET("mobile/car-models/{brandId}")
    suspend fun fetchCarModels( @Path("brandId") brandId: String) : Response<CarModelRes>


    @Multipart
    @POST("mobile/car-models")
    suspend fun addCarModel(
        @Part("car_brand_id") brandId: RequestBody?,
        @Part("name") brandModelName: RequestBody?,
        @Part brandModelImage: MultipartBody.Part?
    ): Response<AddModelRes>

    @Multipart
    @POST("mobile/car-models")
    suspend fun addCarModel(
        @Part("car_brand_id") brandId: RequestBody?,
        @Part("name") brandModelName: RequestBody?
    ): Response<AddModelRes>

    @Multipart
    @POST("mobile/car-models/{modelId}")
    suspend fun updateCarModel(
        @Path("modelId") modelId: String,
        @Part("car_brand_id") brandId: RequestBody?,
        @Part("name") brandModelName: RequestBody?,
        @Part brandModelImage: MultipartBody.Part?,
        @Part("status") status: RequestBody?,
    ): Response<AddModelRes>

    @Multipart
    @POST("mobile/car-models/{modelId}")
    suspend fun updateCarModel(
        @Path("modelId") modelId: String,
        @Part("car_brand_id") brandId: RequestBody?,
        @Part("name") brandModelName: RequestBody?,
        @Part("status") status: RequestBody?,
    ): Response<AddModelRes>

    @GET("mobile/seat-cover-categories")
    suspend fun fetchCategory() : Response<CategoryResModel>

    @GET("mobile/seat-covers/{modelId}")
    suspend fun fetchSeats( @Path("modelId") modelId: String) : Response<SeatResModel>

    @GET("mobile/seat-covers/{seatId}/{stockStatus}")
    suspend fun changeStockStatus( @Path("seatId") seatId: String, @Path("stockStatus") stockStatus: String) : Response<StockChangeResModel>

    @Multipart
    @POST("mobile/seat-covers")
    suspend fun addSeats(
        @Part("name") seatName: RequestBody?,
        @Part("car_brand_id") carBrandId: RequestBody?,
        @Part("car_model_id") carModelId: RequestBody?,
        @Part("category_id") categoryId: RequestBody?,
        @Part("manufacturer_id") manufactureId: RequestBody?,
        @Part("manufactured_quantity") manufacturedQuantity: RequestBody?,
        @Part seatImage: MultipartBody.Part?
    ): Response<AddEditSeatModel>

    @Multipart
    @POST("mobile/seat-covers")
    suspend fun addSeats(
        @Part("name") seatName: RequestBody?,
        @Part("car_brand_id") carBrandId: RequestBody?,
        @Part("car_model_id") carModelId: RequestBody?,
        @Part("category_id") categoryId: RequestBody?,
        @Part("manufacturer_id") manufactureId: RequestBody?,
        @Part("manufactured_quantity") manufacturedQuantity: RequestBody?
    ): Response<AddEditSeatModel>

    @Multipart
    @POST("mobile/seat-covers/{seatId}")
    suspend fun updateSeats(
        @Path("seatId") seatId: String,
        @Part("name") seatName: RequestBody?,
        @Part("car_brand_id") carBrandId: RequestBody?,
        @Part("car_model_id") carModelId: RequestBody?,
        @Part("category_id") categoryId: RequestBody?,
        @Part("status") status: RequestBody?,
        @Part("manufacturer_id") manufactureId: RequestBody?,
        @Part("manufactured_quantity") manufacturedQuantity: RequestBody?,
        @Part seatImage: MultipartBody.Part?
    ): Response<AddEditSeatModel>

    @Multipart
    @POST("mobile/seat-covers/{seatId}")
    suspend fun updateSeats(
        @Path("seatId") seatId: String,
        @Part("name") seatName: RequestBody?,
        @Part("car_brand_id") carBrandId: RequestBody?,
        @Part("car_model_id") carModelId: RequestBody?,
        @Part("category_id") categoryId: RequestBody?,
        @Part("status") status: RequestBody?,
        @Part("manufacturer_id") manufactureId: RequestBody?,
        @Part("manufactured_quantity") manufacturedQuantity: RequestBody?,
    ): Response<AddEditSeatModel>


    @Multipart
    @POST("mobile/seat-covers/bulk-delete")
    suspend fun deleteSeats(
        @Part part: List<MultipartBody.Part>
    ): Response<DeleteSeatsModel>


}