package com.my.ganeshseats.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.my.ganeshseats.R
import com.my.ganeshseats.Utils.gone
import com.my.ganeshseats.Utils.showToast
import com.my.ganeshseats.Utils.visible
import com.my.ganeshseats.data.response.BrandData
import com.my.ganeshseats.data.response.CarDetailModel
import com.my.ganeshseats.data.response.SeatCategoryModel
import com.my.ganeshseats.data.response.SeatDetail
import com.my.ganeshseats.databinding.CarModelsItemviewBinding
import com.my.ganeshseats.databinding.SeatItemviewBinding


//class SeatsGridViewAdapter(
//    context: Context,
//    private val seatList: MutableList<SeatDetail>,
//    private val onItemClick: (SeatDetail) -> Unit
//) : ArrayAdapter<SeatDetail?>(context, 0, seatList ) {
//
//
//
//    fun updateList(newList: List<SeatDetail>) {
//        Log.d("TAG", "updateList: arraySize => $newList")
//        seatList.clear()
//        seatList.addAll(newList)
//        notifyDataSetChanged()
//    }
//
//    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
//
//        var itemView = view
//        if (itemView == null) {
//            itemView = LayoutInflater.from(context).inflate(R.layout.seat_itemview, parent, false)
//        }
//
//        val model: SeatDetail? = seatList[position]
//
//        val seatName = itemView!!.findViewById<TextView>(R.id.seat_name)
//        val seatImage = itemView.findViewById<ImageView>(R.id.seat_img)
//
//        seatName.text = model!!.name
//
//        Glide.with(context)
//            .load(model.seatImage)
//            .error(R.drawable.ganesa)
//            .placeholder(R.drawable.ganesa)
//            .into(seatImage)
//
//        itemView.rootView.setOnClickListener {
//            onItemClick(model)
//        }
//
//
//        return itemView
//    }
//}

//class SeatsRecyclerviewAdapter(private var seatList : MutableList<SeatDetail>, private var myContext: Context, private val onItemClick: (SeatDetail) -> Unit) : RecyclerView.Adapter<SeatsRecyclerviewAdapter.ItemViewHolder>() {
//
//    inner class ItemViewHolder(val binding : SeatItemviewBinding): RecyclerView.ViewHolder(binding.root)
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
//        val binding =  SeatItemviewBinding.inflate(LayoutInflater.from(parent.context) , parent , false)
//        return ItemViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: ItemViewHolder, @SuppressLint("RecyclerView") position: Int) {
//
//        with(holder){
//
//            Log.d("TAG", "onBindViewHolder: recyclerview data length => ${seatList.size}")
//
//            with(seatList[position]){
//
//                binding.seatName.text = this.name
//
//                Glide.with(myContext)
//                    .load(this.seatImage)
//                    .error(R.drawable.error_img)
//                    .placeholder(R.drawable.ic_baseline_photo_library_24)
//                    .into(binding.seatImg)
//
//                itemView.rootView.setOnClickListener {
//                    onItemClick(this)
//                }
//
//
//            }
//        }
//
//    }
//
//    override fun getItemCount(): Int {
//        Log.d("TAG", "getItemCount: item length for that => ${seatList.size}")
//        return seatList.size
//    }
//
//    // Add this function to update the dataset
////    fun updateItems(newItems: MutableList<CarDetailModel>) {
////        carModelList = newItems
////        notifyDataSetChanged() // Notify adapter of data change
////    }
//    fun submitList(newList: List<SeatDetail>) {
//        seatList.clear()
//        seatList.addAll(newList)
//        notifyDataSetChanged()
//    }
//
//}


class SeatsRecyclerviewAdapter(
    private val myContext: Context,
    private val onItemClick: (SeatDetail) -> Unit,
    private val onSelectionChanged: (() -> Unit)? = null, // NEW
    private val onSelectionMode: () -> Unit, // Long press ke liye
    private val onChangeStockStatus: (SeatDetail) -> Unit,
) : ListAdapter<SeatDetail, SeatsRecyclerviewAdapter.ItemViewHolder>(
    SeatDiffCallback()
) {

    inner class ItemViewHolder(val binding: SeatItemviewBinding)
        : RecyclerView.ViewHolder(binding.root)

    var isSelectionMode = false // Long press ke baad true hoga

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = SeatItemviewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = getItem(position)

        // Checkbox visibility & state
        holder.binding.checkBox.visibility = if (isSelectionMode) View.VISIBLE else View.GONE
        holder.binding.checkBox.isChecked = item.isSelected

        holder.binding.seatName.text = item.name

        Log.d("TAG", "shareSelectedImages: selected image length =>${item.seatImage}")

        Glide.with(myContext)
            .load(item.seatImage)
            .error(R.drawable.error_img)
            .placeholder(R.drawable.ic_baseline_photo_library_24)
            .into(holder.binding.seatImg)

        if(isSelectionMode){
            holder.binding.changeStatusBtn.gone()
        }else{
            holder.binding.changeStatusBtn.visible()
        }

        holder.itemView.setOnClickListener {
            if (isSelectionMode) {
                item.isSelected = !item.isSelected
                holder.binding.checkBox.isChecked = item.isSelected
                onSelectionChanged?.invoke() // notify parent
            } else {
                onItemClick(item)
            }
//            onItemClick(item)
        }

        // Long press to start selection mode
        holder.itemView.setOnLongClickListener {
            if (!isSelectionMode) {
                isSelectionMode = true
                item.isSelected = true
                holder.binding.checkBox.isChecked = true
                onSelectionMode() // Inform Activity/Fragment
                notifyDataSetChanged() // Show all checkboxes
                onSelectionChanged?.invoke() // notify parent
            }
            true
        }

        holder.binding.changeStatusBtn.setOnClickListener {view ->

            onChangeStockStatus(item)

//            val popup = PopupMenu(myContext, view)
//            popup.inflate(R.menu.stock_status)
//
//            popup.setOnMenuItemClickListener { menuItem ->
//                when (menuItem.itemId) {
//
//                    R.id.action_stock_status -> {
//                        myContext.showToast("stock status change success")
//                        onChangeStockStatus(item)
//                        true
//                    }
//
//                    else -> false
//                }
//            }
//
//            popup.show()
        }

    }

    // Selected items for sharing
    fun getSelectedItems(): List<SeatDetail> = currentList.filter { it.isSelected }

    // Clear selection
    fun clearSelection() {
        isSelectionMode = false
        currentList.forEach { it.isSelected = false }
        notifyDataSetChanged()
        onSelectionChanged?.invoke() // notify parent
    }

    fun selectAll(select: Boolean) {
//        isSelectionMode = select
        currentList.forEach { it.isSelected = select }
        notifyDataSetChanged()
        onSelectionChanged?.invoke() // notify parent
    }


}

class SeatDiffCallback : DiffUtil.ItemCallback<SeatDetail>() {

    override fun areItemsTheSame(
        oldItem: SeatDetail,
        newItem: SeatDetail
    ): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(
        oldItem: SeatDetail,
        newItem: SeatDetail
    ): Boolean = oldItem == newItem
}