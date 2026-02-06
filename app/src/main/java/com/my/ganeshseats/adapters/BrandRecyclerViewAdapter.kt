package com.my.ganeshseats.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.my.ganeshseats.R
import com.my.ganeshseats.data.response.BrandData
import com.my.ganeshseats.data.response.CarDetailModel
import com.my.ganeshseats.databinding.BrandItemBinding
import com.my.ganeshseats.databinding.CarModelsItemviewBinding


class BrandRecyclerViewAdapter(
    private var brandList : MutableList<BrandData>,
    private var myContext: Context,
    private val onItemClick: (BrandData) -> Unit,
    private val onItemEditClick: (BrandData) -> Unit) : RecyclerView.Adapter<BrandRecyclerViewAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(val binding : BrandItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding =  BrandItemBinding.inflate(LayoutInflater.from(parent.context) , parent , false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, @SuppressLint("RecyclerView") position: Int) {

        with(holder){

            Log.d("TAG", "onBindViewHolder: recyclerview data length => ${brandList.size}")

            with(brandList[position]){

                binding.brandNameText.text = this.brandName

                Glide.with(myContext)
                    .load(this.brandImage)
                    .placeholder(R.drawable.ic_baseline_photo_library_24)
                    .error(R.drawable.error_img)
                    .into(binding.brandImage)

                itemView.rootView.setOnClickListener {
                    onItemClick(this)
                }

                binding.editBrandBtn.setOnClickListener {
                    onItemEditClick(this)
                }

            }
        }

    }

    override fun getItemCount(): Int {
        Log.d("TAG", "getItemCount: item length for that => ${brandList.size}")
        return brandList.size
    }

    // Add this function to update the dataset
//    fun updateItems(newItems: MutableList<CarDetailModel>) {
//        carModelList = newItems
//        notifyDataSetChanged() // Notify adapter of data change
//    }


}