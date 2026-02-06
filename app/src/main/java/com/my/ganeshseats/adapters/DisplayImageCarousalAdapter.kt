package com.my.ganeshseats.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.my.ganeshseats.R
import com.my.ganeshseats.Utils.gone
import com.my.ganeshseats.Utils.visible
import com.my.ganeshseats.data.response.SeatDetail


class DisplayImageCarousalAdapter(
    private val myContext: Context,
    private val arrayData: ArrayList<SeatDetail>
) : RecyclerView.Adapter<DisplayImageCarousalAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: com.github.chrisbanes.photoview.PhotoView = view.findViewById(R.id.img_preview)
        val manufactureLayout: LinearLayout = view.findViewById(R.id.manufacture_layout)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.viewpager_item_layout, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {

        val itemData = arrayData[position]

        Glide.with(myContext)
            .load(itemData.seatImage)
            .error(R.drawable.error_img)
            .placeholder(R.drawable.ic_baseline_photo_library_24)
            .into(holder.imageView)

            val manufactureAry = itemData.manufacturersDetail

            if(manufactureAry.isNotEmpty()){
                holder.manufactureLayout.visible()
                manufactureAry.forEach { item ->
                    holder.manufactureLayout.removeAllViews()
                    val view = LayoutInflater.from(holder.itemView.context).inflate(
                        R.layout.manufacture_section_layout,
                        holder.manufactureLayout,
                        false
                    )
                    view.findViewById<TextView>(R.id.name_text).text = item.manufatureName?.ifEmpty { "N/A" }
                    view.findViewById<TextView>(R.id.mobile_text).text = item.manufatureNumber ?: "N/A"
                    view.findViewById<TextView>(R.id.set_text).text = item.manufacturedSet.toString().ifEmpty { "N/A" }
                    view.findViewById<TextView>(R.id.date_text).text = item.date.toString().ifEmpty { "N/A" }
                    holder.manufactureLayout.addView(view)
                }
            }else{
                holder.manufactureLayout.gone()
            }

    }

    override fun getItemCount(): Int = arrayData.size

    fun updateSeatArray(data: ArrayList<SeatDetail>){
        arrayData.clear()
        arrayData.addAll(data)
        notifyDataSetChanged()
    }
}