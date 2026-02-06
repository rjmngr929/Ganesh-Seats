package com.my.ganeshseats.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.my.ganeshseats.R
import com.my.ganeshseats.data.response.CarDetailModel
import com.my.ganeshseats.data.response.CarModelRes
import com.my.ganeshseats.databinding.CarModelsItemviewBinding


class CarModelsRecyclerviewAdapter(private var carModelList : MutableList<CarDetailModel>, private var myContext: Context, private val onItemClick: (CarDetailModel) -> Unit, private val onItemEditClick: (CarDetailModel) -> Unit) : RecyclerView.Adapter<CarModelsRecyclerviewAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(val binding : CarModelsItemviewBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding =  CarModelsItemviewBinding.inflate(LayoutInflater.from(parent.context) , parent , false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, @SuppressLint("RecyclerView") position: Int) {

        with(holder){

            Log.d("TAG", "onBindViewHolder: recyclerview data length => ${carModelList.size}")

            with(carModelList[position]){

                binding.carModelName.text = this.modelName

                Log.d("TAG", "onBindViewHolder: model name is ${this.modelName}")

                binding.availableCoversText.text = String.format("Available: %s", this.seatCoverAvailable)

                Glide.with(myContext)
                    .load(this.modelImage)
                    .circleCrop()
                    .placeholder(R.drawable.ic_baseline_photo_library_24)
                    .error(R.drawable.error_img)
                    .into(binding.carModelImage)

                binding.root.setOnClickListener {
                    onItemClick(this)
                }

                binding.editModelBtn.setOnClickListener {
                    onItemEditClick(this)
                }

            }
        }

    }

    override fun getItemCount(): Int {
        Log.d("TAG", "getItemCount: item length for that => ${carModelList.size}")
        return carModelList.size
    }

    // Add this function to update the dataset
//    fun updateItems(newItems: MutableList<CarDetailModel>) {
//        carModelList = newItems
//        notifyDataSetChanged() // Notify adapter of data change
//    }


}