package com.my.ganeshseats.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import com.my.ganeshseats.R
import com.my.ganeshseats.Utils.showToast
import com.my.ganeshseats.data.response.BrandData
import com.my.ganeshseats.data.response.BrandModel
import com.my.ganeshseats.data.response.CarDetailModel


//class BrandGridViewAdapter(
//    context: Context,
//    list: ArrayList<BrandData>,
//    private val onItemClick: (BrandData) -> Unit,
//    private val onItemSettingClick: (view : View, BrandData) -> Unit
//) : ArrayAdapter<BrandData?>(context, 0, list as List<BrandData?>) {
//
//    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
//
//        var itemView = view
//        if (itemView == null) {
//            itemView = LayoutInflater.from(context).inflate(R.layout.brand_item, parent, false)
//        }
//
//        val model: BrandData? = getItem(position)
//        val textView = itemView!!.findViewById<TextView>(R.id.text_view)
//        val imageView = itemView.findViewById<ImageView>(R.id.image_view)
//
//        textView.text = model!!.brandName
//
//        Glide.with(context)
//            .load(model.brandImage)
//            .placeholder(R.drawable.ic_baseline_photo_library_24)
//            .error(R.drawable.error_img)
//            .into(imageView)
//
//        itemView.rootView.setOnClickListener {
//          onItemClick(model)
//        }
//
//        val editBrandBtn = itemView!!.findViewById<LinearLayout>(R.id.edit_brand_btn)
//
//        editBrandBtn.setOnClickListener {
//            onItemSettingClick(itemView, model)
//        }
//
//        return itemView
//    }
//}