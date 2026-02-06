package com.my.ganeshseats.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.SearchView
import android.widget.TextView
import androidx.activity.addCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.scale
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.annotations.SerializedName
import com.kotlinpermissions.PermissionStatus
import com.kotlinpermissions.allGranted
import com.kotlinpermissions.anyPermanentlyDenied
import com.kotlinpermissions.anyShouldShowRationale
import com.kotlinpermissions.extension.permissionsBuilder
import com.kotlinpermissions.request.PermissionRequest
import com.my.ganeshseats.R
import com.my.ganeshseats.Utils.AlertDialogUtility
import com.my.ganeshseats.Utils.Helper
import com.my.ganeshseats.Utils.ImageUtils
import com.my.ganeshseats.Utils.NetworkResult
import com.my.ganeshseats.Utils.getLoadingDialog
import com.my.ganeshseats.Utils.gone
import com.my.ganeshseats.Utils.hideLoader
import com.my.ganeshseats.Utils.setOnSingleClickListener
import com.my.ganeshseats.Utils.showLoader
import com.my.ganeshseats.Utils.showPermanentlyDeniedDialog
import com.my.ganeshseats.Utils.showRationaleDialog
import com.my.ganeshseats.Utils.showToast
import com.my.ganeshseats.Utils.visible
import com.my.ganeshseats.adapters.CarModelsRecyclerviewAdapter
import com.my.ganeshseats.adapters.SeatsRecyclerviewAdapter
import com.my.ganeshseats.adapters.ViewPagerAdapter
import com.my.ganeshseats.data.models.SeatTab
import com.my.ganeshseats.data.response.AddEditSeatModel
import com.my.ganeshseats.data.response.AddSeatDataModel
import com.my.ganeshseats.data.response.BrandData
import com.my.ganeshseats.data.response.CarDetailModel
import com.my.ganeshseats.data.response.CategoryDataModel
import com.my.ganeshseats.data.response.ManufactureModelSelect
import com.my.ganeshseats.data.response.ManufacturerDetail
import com.my.ganeshseats.data.response.SeatCategoryModel
import com.my.ganeshseats.data.response.SeatData
import com.my.ganeshseats.data.response.SeatDetail
import com.my.ganeshseats.databinding.FragmentSeatCoversBinding
import com.my.ganeshseats.ui.DisplayImageActivity
import com.my.ganeshseats.ui.viewmodel.CarModelViewModel
import com.my.ganeshseats.ui.viewmodel.MasterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.regex.Pattern
import javax.inject.Inject
import kotlin.Boolean
import kotlin.collections.contains
import kotlin.getValue


@AndroidEntryPoint
class SeatCoversFragment : Fragment() , PermissionRequest.Listener{

    companion object{
        private const val TAG = "Seat Covers Fragment"
    }

    private lateinit var binding: FragmentSeatCoversBinding

    private var mediaBottomSheet: BottomSheetDialog? = null

    private var listData : ArrayList<CarDetailModel> = ArrayList()

    private val masterViewModel: MasterViewModel by activityViewModels ()
    private val carDataViewModel: CarModelViewModel by activityViewModels ()

    private lateinit var myContext: Context

    @Inject
    lateinit var alertDialogService: AlertDialogUtility

    private var seatAryData : ArrayList<SeatDetail> = ArrayList()
    private var filterSeatAryData : ArrayList<SeatDetail> = ArrayList()

    val availableSeat: ArrayList<SeatDetail> = ArrayList()
    val outStockSeat: ArrayList<SeatDetail> = ArrayList()

    val responseSeatDataArray: ArrayList<SeatCategoryModel> = ArrayList()

    private var stockAvailable: Boolean = true

    private lateinit var loader: AlertDialog

//    private lateinit var gridViewAdapter: SeatsGridViewAdapter
    private lateinit var seatDataAdapter: SeatsRecyclerviewAdapter

    private lateinit var imgPath : String

    private var dialogSeatImage: ImageView? = null
    private var dialogSeatImageDefault: LinearLayout? = null

    val tabAry : ArrayList<SeatTab> = ArrayList()


    private var modelId: String = "0"
    private var modelName: String = ""
    private var brandId: String = ""
    private var brandName: String = ""

    private var categoryList: ArrayList<CategoryDataModel> = ArrayList()

    val categoryAry = ArrayList<String>()

    private var manufactureList: ArrayList<ManufactureModelSelect> = ArrayList()

    val manufactureAry = ArrayList<String>()


//    private var response: SeatData? = SeatData()
    private var selectedCategory: String = ""

    private var selectedAvailability = ""
    private var selectedFilterCategory = ""
    private var selectedManufactured = ""

    private var photoURI: Uri? = null;
    private var bitmapdata : Bitmap? = null
    private lateinit var resized : Bitmap
    private lateinit var currentPhotoPath: String

    private var totalSeatCount: Int = 0

    private var isUpdatingSelectAll = false

    private var cameraImageUri: Uri? = null
    private var imageFile: File? = null

    private val cameraLaunch =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success && cameraImageUri != null) {
                uploadImage(cameraImageUri!!)
            }
        }
    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView: call that success")
        responseSeatDataArray.clear()
        seatAryData.clear()
        filterSeatAryData.clear()
        tabAry.clear()
        outStockSeat.clear()
        availableSeat.clear()

    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        myContext = context
    }

    private val request by lazy {
        permissionsBuilder(
            arrayListOf(
                Manifest.permission.CAMERA
            )
        ).build()
    }

    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if(result.resultCode == RESULT_OK){
//            chooseImage()
            if (isAdded && !isDetached) {
                showMediaBottomSheet()
            }
        }else{
            request.send()
        }

    }

//    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
//
//        if (result.resultCode == Activity.RESULT_OK) {
//            dialogSeatImage?.let {
//                Glide.with(myContext)
//                    .load(photoURI)
//                    .placeholder(R.drawable.ic_baseline_photo_library_24) // Placeholder image while loading
//                    .error(R.drawable.error_img)
//                    .transition(DrawableTransitionOptions.withCrossFade()) // Fade transition
//                    .into(it)
//            }
//
//            lifecycleScope.launch(Dispatchers.IO) {
//                bitmapdata = Bitmap.createScaledBitmap(MediaStore.Images.Media.getBitmap(myContext.contentResolver, Uri.parse(
//                    photoURI.toString()
//                )), 400, 400, true)
//
//                val compressedFile = ImageUtils.instance.bitmapToFile(myContext, bitmapdata)
//                imgPath = compressedFile?.absolutePath.toString()
//
//            }
//
//        }
//    }

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { it ->
        // photo picker.
        if(it != null){
            uploadImage(it)
//            lifecycleScope.launch(Dispatchers.IO) {
//                bitmapdata = MediaStore.Images.Media.getBitmap(
//                    myContext.contentResolver, it.toString().toUri()
//                ).scale(400, 400)
//
//                val compressedFile = ImageUtils.instance.bitmapToFile(myContext, bitmapdata)
//                imgPath = compressedFile?.absolutePath.toString()
//
//                withContext(Dispatchers.Main) {
//
//                    dialogSeatImage?.let {
//                        Glide.with(myContext)
//                            .load(bitmapdata)
//                            .placeholder(R.drawable.ic_baseline_photo_library_24) // Optional: placeholder while loading
//                            .error(R.drawable.error_img) // Optional: error image if loading fails
//                            .into(it)
//                    }
//
//                    dialogSeatImageDefault?.gone()
//
////                    Glide.with(this@MainActivity)
////                        .load(bitmapdata)
////                        .circleCrop() // Makes the image circular
////                        .placeholder(R.drawable.ganesa) // Optional: placeholder while loading
////                        .error(R.drawable.ganesa) // Optional: error image if loading fails
////                        .into(dialogBrandImage!!)
//                }
//
//            }

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSeatCoversBinding.inflate(inflater, container, false)

        loader = getLoadingDialog(myContext)

        request.addListener(this)

        carDataViewModel.fetchCategory()

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments.let{
            if (it != null) {
                modelId = it.getString("modelId", "").toString()
                modelName = it.getString("modelName", "").toString()
                brandId = it.getString("brandId", "").toString()
                brandName = it.getString("brandName", "").toString()
                Log.d(TAG, "onViewCreated: model id is $modelId")
                carDataViewModel.fetchSeatData(modelId = modelId)
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (seatDataAdapter.isSelectionMode) {
                seatDataAdapter.clearSelection()
                binding.selectAllLayout.gone()
                binding.deleteModelSeatBtn.gone()
                binding.checkBoxSelectAll.isChecked = false
//                fab.visibility = View.GONE
                binding.addModelSeatBtn.setImageResource(R.drawable.outline_add_24)
            } else {
                isEnabled = false
                requireActivity().onBackPressed()
            }
        }

        setupTabListener()

        masterViewModel.refreshData.observe(viewLifecycleOwner, Observer{status ->
            if(status){
                Log.d(TAG, "onViewCreated: update data at that point $modelId")
//                carDataViewModel.fetchSeatData(modelId = modelId)
                
                val updatedData = masterViewModel.updatedSeatData.value
                
                if(updatedData != null){

                    val seatDataAry: ArrayList<SeatDetail> = ArrayList()

                    val newData = SeatDetail(
                        id = updatedData.id,
                        name = updatedData.name,
                        carBrandName = updatedData.carBrandName,
                        carModelName = updatedData.carModelName,
                        categoryName = updatedData.categoryName,
                        material = updatedData.material,
                        availableStock = updatedData.availableStock,
                        stockStatus = updatedData.stockStatus,
                        seatImage = updatedData.seatImage,
                        manufacturersDetail = updatedData.manufacturersDetail
                    )

                    seatDataAry.add(newData)

                    upsertSeatWithCategory(responseSeatDataArray, seatDataAry)

                }

                masterViewModel.setUpdatedSeatData(null)
                masterViewModel.refreshData.value = false
            }
        })

//        ********************* GridView Data **************************
//        gridViewAdapter = SeatsGridViewAdapter(myContext, seatAryData,
//            onItemClick = { data ->
//                masterViewModel.setSeatDetailData(data)
//                val intent = Intent(myContext, DisplayImageActivity::class.java)
//                intent.putExtra("carModelId", modelId)
//                startActivity(intent)
//            }
//        )
//
//        binding.seatsGridView.adapter = gridViewAdapter
//        ********************* GridView Data **************************

//        ******************* RecyclerView ********************************
        binding.seatsRecyclerview.layoutManager = GridLayoutManager(context, 2)
        // Initialize the adapter with an empty list and set it to the RecyclerView
        seatDataAdapter = SeatsRecyclerviewAdapter(  myContext,
            onItemClick = { data ->
                masterViewModel.setSeatDetailData(data)
                masterViewModel.setSelectedSeatId(data.id ?: 0)
                Log.d(TAG, "onViewCreated: updated seatData is ${data.name} and ${data.categoryName} ${data.id}")
                val intent = Intent(requireActivity(), DisplayImageActivity::class.java)
                intent.putExtra("carModelId", modelId)
                intent.putExtra("brandId", brandId)
                masterViewModel.setSeatArrayData(filterSeatAryData)
                requireActivity().runOnUiThread {
                    startActivity(intent)
                }
//                startActivity(intent)
            },
            onSelectionMode = {
                binding.selectAllLayout.visible()
                binding.addModelSeatBtn.setImageResource(R.drawable.share_ic)
                binding.deleteModelSeatBtn.visible()
            },
            onSelectionChanged = {
                isUpdatingSelectAll = true
                binding.checkBoxSelectAll.isChecked = seatDataAdapter.getSelectedItems().size == seatDataAdapter.currentList.size
                isUpdatingSelectAll = false

            },
            onChangeStockStatus = {data ->
                if(data.stockStatus == "in_stock"){
                    confirmStockUpdateAlert(data = data, stockStatus = "out-of-stock")
                }else{
                    confirmStockUpdateAlert(data = data, stockStatus = "in-stock")
                }
            }
        )
        binding.seatsRecyclerview.adapter = seatDataAdapter
//        ******************* RecyclerView ********************************

//  ******************************** Search Seats **************************************
        binding.searchSeats.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(p0: String?): Boolean {
                Log.d(TAG, "onQueryTextSubmit: ")
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
//                if(seatAryData.isNotEmpty()){
//                    filterSeatAryData.clear()
//                    for (data in seatAryData) {
//                        if (query?.let { Pattern.quote(it) }?.let {
//                                Pattern.compile(it, Pattern.CASE_INSENSITIVE)
//                                    .matcher(data.name).find()
//                            } == true
//                        ) {
//                            filterSeatAryData.add(data)
//                        }
//                    }
//
//                    seatDataAdapter.submitList(filterSeatAryData.toList())
////                    seatDataAdapter.notifyDataSetChanged()
//                }


                val searchText = query?.trim().orEmpty()

                if (searchText.isEmpty()) {
                    // Reset list
                    seatDataAdapter.submitList(filterSeatAryData.toList())
                    return true
                }

                val filteredList = filterSeatAryData.filter { seat ->
                    seat.name?.contains(searchText, ignoreCase = true) == true
                }

                seatDataAdapter.submitList(filteredList)


                return false
            }
        })
//  ******************************** Search Seats **************************************

        bindObservers()

        bindObserversSeatData()

        bindObserversChangeStockStatus()

        deleteSeatsBindObservers()

        if(stockAvailable){
            setActiveButton(binding.btnInStock)
            setInActiveButton(binding.btnOutStock)
        }else{
            setActiveButton(binding.btnOutStock)
            setInActiveButton(binding.btnInStock)
        }

        binding.btnInStock.setOnClickListener {
            stockAvailable = true
            setActiveButton(binding.btnInStock)
            setInActiveButton(binding.btnOutStock)

            updateGridView(selectedCategory)

//            seatAryData.clear()
//            filterSeatAryData.clear()
////            seatAryData.addAll(availableSeat)
//
//
//            seatAryData.addAll(availableSeat)
//            filterSeatAryData.addAll(availableSeat)
//
//            if(seatAryData.isNotEmpty()){
//                binding.dataNotFoundLayout.root.gone()
//            }else{
//                binding.dataNotFoundLayout.root.visible()
//            }
//
//            seatDataAdapter.notifyDataSetChanged()



        }

        binding.btnOutStock.setOnClickListener {
            stockAvailable = false
            setActiveButton(binding.btnOutStock)
            setInActiveButton(binding.btnInStock)
//            masterViewModel.setStockAvailable(false)

            updateGridView(selectedCategory)

//            seatAryData.clear()
//            filterSeatAryData.clear()
////            seatAryData.addAll(outStockSeat)
////            gridViewAdapter.notifyDataSetChanged()
//
//            seatAryData.addAll(outStockSeat)
//            filterSeatAryData.addAll(outStockSeat)
//
//            if(seatAryData.isNotEmpty()){
//                binding.dataNotFoundLayout.root.gone()
//            }else{
//                binding.dataNotFoundLayout.root.visible()
//            }
//
//            seatDataAdapter.notifyDataSetChanged()


//            gridViewAdapter.notifyDataSetChanged()
        }


        binding.addModelSeatBtn.setOnClickListener {
            if(seatDataAdapter.isSelectionMode){
                val selectedItems = seatDataAdapter.getSelectedItems()
                Log.d(TAG, "onViewCreated: selected image data is ${selectedItems.size}")
                val validItems = selectedItems.filter {
                    !it.seatImage.isNullOrBlank()
                }
                Log.d(TAG, "onViewCreated: selected image data is ${validItems.size}")
                if (validItems.isNotEmpty()) {
                    showLoader(myContext, loader)
                    shareSelectedImages(requireContext(), validItems)
                }else{
                    myContext.showToast("No valid images to share")
                }
            }else{
                addSeatAlert()
            }
        }
        
        binding.deleteModelSeatBtn.setOnClickListener {
            val selectedItems = seatDataAdapter.getSelectedItems()
            
            val selectedIds = ArrayList<String>()
            
            selectedItems.map { 
                selectedIds.add(it.id.toString())
            }
            
            if (selectedItems.isNotEmpty()) {
                showLoader(myContext, loader)
                seatDataAdapter.clearSelection()
                binding.selectAllLayout.gone()
                carDataViewModel.deleteSeatData(selectedIds)

                Log.d(TAG, "onViewCreated: selected seat for delete => $selectedIds")
                
            }else{
                myContext.showToast("No valid item selected")
            }
        }

        binding.checkBoxSelectAll.setOnCheckedChangeListener  { _, isChecked ->
            if (isUpdatingSelectAll) return@setOnCheckedChangeListener
            seatDataAdapter.selectAll(isChecked)
        }

        addModelSeatBindObservers()

        // Pull-to-refresh listener
        binding.swipeRefreshLayout.setOnRefreshListener {
            // Call your refresh function here
            refreshData()
        }

        binding.swipeRefreshLayout.setColorSchemeColors(
            ContextCompat.getColor(myContext, R.color.black),
            ContextCompat.getColor(myContext, R.color.black),
            ContextCompat.getColor(myContext, R.color.black)
        )

    }

    private fun refreshData() {
        // Simulate network call or database fetch
        Handler(Looper.getMainLooper()).postDelayed({
            // Update your data

            if(modelId.isNotEmpty() && modelId != "0") {
                carDataViewModel.fetchSeatData(modelId = modelId)
            }



            // Stop the refreshing animation
            binding.swipeRefreshLayout.isRefreshing = false
        }, 1000) // 2 seconds delay
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: call success")
//        response = null
        filterSeatAryData.clear()
        seatAryData.clear()
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as AppCompatActivity)
            .supportActionBar
            ?.title = modelName

//        val arrayData = masterViewModel.seatArrayData.value
//        if(!arrayData.isNullOrEmpty()){
//            filterSeatAryData.clear()
//            filterSeatAryData.addAll(arrayData)
//            seatDataAdapter.submitList(filterSeatAryData.toList())
//            masterViewModel.setSeatArrayData(arrayListOf())
//        }
    }

    private fun setActiveButton(button: MaterialButton) {
        button.apply {
            setBackgroundColor(
                ContextCompat.getColor(context, R.color.darkMineShaft)
            )
            setTextColor(Color.WHITE)
            strokeWidth = 0
        }
    }

    private fun setInActiveButton(button: MaterialButton) {
        button.apply {
            setBackgroundColor(Color.TRANSPARENT)
            setTextColor(
                ContextCompat.getColor(context, R.color.black)
            )
            strokeWidth = 2
            strokeColor = ColorStateList.valueOf(
                ContextCompat.getColor(context, R.color.black)
            )
            setTextColor(Color.BLACK)
        }
    }

    private fun bindObservers() {
        carDataViewModel.fetchCategoryModelResponseLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is NetworkResult.Success -> {

                    hideLoader(myContext, loader)


                    listData.clear()

                    val resData = it.data?.categoryManufactureData

                    val categoryAryList = resData?.categoryAryData ?: ArrayList()
                    val manufactureAryList = resData?.manufactureAryData ?: ArrayList()

                    categoryList.clear()
                    categoryAry.clear()

                    categoryList.addAll(categoryAryList)


                    categoryList.map {
                        categoryAry.add(it.name.toString())
                    }

                    masterViewModel.setCategoryData(categoryList)
                    masterViewModel.setCategoryList(categoryAry)

                    manufactureList.clear()
                    manufactureAry.clear()

                    manufactureList.addAll(manufactureAryList)

                    manufactureList.map {
                        manufactureAry.add(it.manufactureName.toString())
                    }

                    masterViewModel.setManufactureData(manufactureAryList)
                    masterViewModel.setManufactureList(manufactureAry)

                }
                is NetworkResult.Error -> {
                    hideLoader(myContext, loader)

                    alertDialogService.alertDialogAnim(
                        myContext,
                        it.message.toString(),
                        R.raw.failed
                    )
                    Log.d(TAG, "bindObservers: response received => Error = ${it.message}")
                }
                is NetworkResult.Loading ->{
                    showLoader(myContext, loader)
                }
                is NetworkResult.Empty -> {
                    hideLoader(myContext, loader)
                }
            }
        })
    }

    private fun bindObserversSeatData() {
        carDataViewModel.fetchSeatDataResponseLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is NetworkResult.Success -> {

                    hideLoader(myContext, loader)

                    val response = it?.data?.seatData

                    totalSeatCount = response?.totalAvailableSets ?: 0

                    responseSeatDataArray.clear()

                    responseSeatDataArray.addAll(response?.seatData ?: ArrayList())

//                    (requireActivity() as AppCompatActivity)
//                        .supportActionBar
//                        ?.title = String.format("%s   (%s)", modelName, totalSeatCount)

                    tabAry.clear()

//                    val categoryData = response?.seatData ?: ArrayList()
                    val categoryData = responseSeatDataArray

                    Log.d(TAG, "bindObserversSeatData: ${categoryData.size}")

                    for (data in 0 until categoryData.size){
                        tabAry.add(SeatTab(title = categoryData[data].categoryName.toString(), seats=categoryData[data].totalStock!!))
                        Log.d(TAG, "bindObserversSeatData: ${categoryData[data].categoryName}")
                    }



//                    binding.tabs.removeAllTabs()
//
//                    binding.tabs.clearOnTabSelectedListeners()
//
//                    Log.d(TAG, "bindObserversSeatData: tab array data => ${tabAry}")
//
//                    tabAry.forEach { category ->
//                        binding.tabs.addTab(
//                            binding.tabs.newTab().setText(category)
//                        )
//                    }
//
                    if(tabAry.isNotEmpty()) {
                        if(selectedCategory.isEmpty()){
                            selectedCategory = tabAry[0].title
                        }
                        refreshTabs(selectedCategory)
                    }
//
//                    binding.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
//                        override fun onTabSelected(tab: TabLayout.Tab) {
//                            Log.d(TAG, "onTabSelected: tab length is ${tabAry.size}")
//                            selectedCategory = tabAry[tab.position]
//                            Log.d(TAG, "onTabSelected: selected tab => $selectedCategory")
////                            loadDataByCategory(selectedCategory)
////                            masterViewModel.setSelectedCategory(selectedCategory)
//
//                            updateGridView(selectedCategory)
//
//                        }
//
//                        override fun onTabUnselected(tab: TabLayout.Tab) {}
//                        override fun onTabReselected(tab: TabLayout.Tab) {}
//                    })

                    if(tabAry.isNotEmpty())
                        updateGridView(selectedCategory)

                    Log.d(TAG, "bindObserversSeatData: response recevied")

                    carDataViewModel.clearSeatRes()

                }
                is NetworkResult.Error -> {
                    hideLoader(myContext, loader)

                    alertDialogService.alertDialogAnim(
                        myContext,
                        it.message.toString(),
                        R.raw.failed
                    )
                    Log.d(TAG, "bindObservers: response received => Error = ${it.message}")
                }
                is NetworkResult.Loading ->{
                    showLoader(myContext, loader)
                }
                is NetworkResult.Empty -> {
                    hideLoader(myContext, loader)
                }
            }
        })
    }

    private fun bindObserversChangeStockStatus() {
        carDataViewModel.changeStockDataResponseLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is NetworkResult.Success -> {

                    hideLoader(myContext, loader)

                    val response = it.data?.data ?: ArrayList()

                    if(response.isNotEmpty()){
                        val updateData = SeatDetail(
                            id = response.first().id,
                            name = response.first().name,
                            carBrandName = response.first().carBrandName,
                            carModelName = response.first().carModelName,
                            categoryName = response.first().categoryName,
                            material = response.first().material,
                            availableStock = response.first().availableStock,
                            stockStatus = response.first().stockStatus,
                            seatImage = response.first().image,
                            manufacturersDetail = response.first().manufacturers
                        )

                        // Add to correct stock list
                        if (updateData.stockStatus == "in_stock") {
                            totalSeatCount++
                        } else {
                            if(totalSeatCount>0)
                             totalSeatCount--
                        }

//                        (requireActivity() as AppCompatActivity)
//                            .supportActionBar
//                            ?.title = String.format("%s   (%s)", modelName, totalSeatCount)

                        updateSeatStock(updateData)
                    }

                    carDataViewModel.clearStockStatusRes()

                }
                is NetworkResult.Error -> {
                    hideLoader(myContext, loader)

                    alertDialogService.alertDialogAnim(
                        myContext,
                        it.message.toString(),
                        R.raw.failed
                    )
                    Log.d(TAG, "bindObservers: response received => Error = ${it.message}")
                }
                is NetworkResult.Loading ->{
                    showLoader(myContext, loader)
                }
                is NetworkResult.Empty -> {
                    hideLoader(myContext, loader)
                }
            }
        })
    }

    private fun deleteSeatsBindObservers() {

        carDataViewModel.deleteSeatDataResponseLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is NetworkResult.Success -> {

                    hideLoader(myContext, loader)
                    
                    val resData = it.data?.seatData?.toList() ?: listOf()


                    deleteSeatsFromCategories(responseSeatDataArray, resData)

                }
                is NetworkResult.Error -> {
                    hideLoader(myContext, loader)

                    alertDialogService.alertDialogAnim(
                        myContext,
                        it.message.toString(),
                        R.raw.failed
                    )
                    Log.d(TAG, "bindObservers: response received => Error = ${it.message}")
                }
                is NetworkResult.Loading ->{
                    showLoader(myContext, loader)
                }
                is NetworkResult.Empty -> {
                    hideLoader(myContext, loader)
                }
            }
        })
    }

    fun confirmStockUpdateAlert(stockStatus: String, data: SeatDetail){
        var dialogBuilder = AlertDialog.Builder(myContext)
        val layoutView: View = layoutInflater.inflate(R.layout.confirmation_alertdialog, null)

        dialogBuilder.setCancelable(false)

        val cancel_btn : MaterialButton = layoutView.findViewById(R.id.operation_cancel_btn)
        val submit_btn : MaterialButton = layoutView.findViewById(R.id.operation_done_btn)

        val titleText : TextView = layoutView.findViewById(R.id.alert_msg)

        val animIcon : com.airbnb.lottie.LottieAnimationView = layoutView.findViewById(R.id.alertdialog_anim_icon)

        animIcon.setAnimation(R.raw.warning)

        if(data.stockStatus == "in_stock"){
            titleText.setText("Mark as Out of Stock?")
        }else{
            titleText.setText("Mark as In Stock?")
        }

        dialogBuilder.setView(layoutView)
        val alertDialog = dialogBuilder.create()
        alertDialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()

        cancel_btn.setOnClickListener {
            alertDialog.dismiss()
        }

        submit_btn.setOnClickListener {
            carDataViewModel.updateStockStatus(data.id.toString(), stockStatus)
            alertDialog.dismiss()
        }

    }

    fun updateSeatStock(updatedData: SeatDetail) {

        val category =
            responseSeatDataArray.firstOrNull { it.categoryName == updatedData.categoryName }
                ?: return

        val inStockList = category.seatDetailInStock
        val outStockList = category.seatDetailOutStock

        // Remove seat from both lists (safety)
        inStockList.removeAll { it.id == updatedData.id }
        outStockList.removeAll { it.id == updatedData.id }

        // Add to correct stock list
        if (updatedData.stockStatus == "in_stock") {
            inStockList.add(updatedData)
        } else {
            outStockList.add(updatedData)
        }

        val listToShow = when {
            category == null -> emptyList()
            stockAvailable -> inStockList
            else -> outStockList
        }

        binding.dataNotFoundLayout.root
            .isVisible = listToShow.isEmpty()

        // 🔥 ONLY THIS LINE updates RecyclerView
        seatDataAdapter.submitList(listToShow.toList())

    }

    @SuppressLint("MissingInflatedId")
    fun addSeatAlert(){
        imgPath = ""
        var dialogBuilder = AlertDialog.Builder(myContext)
        val layoutView: View = layoutInflater.inflate(R.layout.add_edit_seat_alert, null)

        dialogBuilder.setCancelable(false)

        val cancel_btn : MaterialButton = layoutView.findViewById(R.id.product_add_cancel_btn)
        val submit_btn : MaterialButton = layoutView.findViewById(R.id.product_add_save_btn)

        val seatTextLayout : TextInputLayout = layoutView.findViewById(R.id.seatNameTextField)
        val seatText : TextInputEditText = layoutView.findViewById(R.id.seatName_text)

        val selectAvailability : TextInputLayout = layoutView.findViewById(R.id.select_availability)
        val availabilityFilter : AutoCompleteTextView = layoutView.findViewById(R.id.availabilityFilter)

        val selectProductQuality : TextInputLayout = layoutView.findViewById(R.id.select_product_quality)
        val productQualityFilter : AutoCompleteTextView = layoutView.findViewById(R.id.product_qualityFilter)

        val selectManufacture : TextInputLayout = layoutView.findViewById(R.id.select_manufacture)
        val manufactureFilter : AutoCompleteTextView = layoutView.findViewById(R.id.product_manufactureFilter)

        val manufacturedTextLayout : TextInputLayout = layoutView.findViewById(R.id.manufactureQuantityTextField)
        val manufacturedText : TextInputEditText = layoutView.findViewById(R.id.manufactureQuantity_text)

        val alertTitle : TextView = layoutView.findViewById(R.id.alert_title)

        val imgUploadLayout : RelativeLayout = layoutView.findViewById(R.id.imgUpload_layout_product)
        dialogSeatImage  = layoutView.findViewById(R.id.previewImg_product)
        dialogSeatImageDefault  = layoutView.findViewById(R.id.img_default_product)


//  ************************************* Stock Availability Section *******************************************

        selectAvailability.gone()

//        val stockAry = arrayListOf("Available", "Out of Stock")
//
//        val stockSelectAdapter = ArrayAdapter(
//            myContext, R.layout.custom_textview, stockAry
//        )
//        availabilityFilter.setAdapter(stockSelectAdapter)
//
//
//        if(stockAry.size > 5)
//            availabilityFilter.dropDownHeight = 500
//
//        availabilityFilter.setDropDownBackgroundDrawable(
//            ResourcesCompat.getDrawable(
//                resources,
//                R.drawable.rounded_border,
//                null
//            )
//        )
//
//        if(stockAry.size > 5)
//            availabilityFilter.dropDownHeight = 1000
//
//        availabilityFilter.onItemClickListener =
//            AdapterView.OnItemClickListener { _, _, position, id->
//                selectedAvailability = stockAry[position]
//            }

//  ************************************* Stock Availability Section *******************************************

//  ************************************* Product Quality Section *******************************************

        val categorySelectAdapter = ArrayAdapter(
            myContext, R.layout.custom_textview, categoryAry
        )
        productQualityFilter.setAdapter(categorySelectAdapter)


        if(categoryAry.size > 5)
            productQualityFilter.dropDownHeight = 500

        productQualityFilter.setDropDownBackgroundDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.rounded_border,
                null
            )
        )

        if(categoryAry.size > 5)
            productQualityFilter.dropDownHeight = 1000

        productQualityFilter.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, id->
                selectedFilterCategory = categoryAry[position]
            }

//  ************************************* Product Quality Section *******************************************

//  ************************************* Manufacture Selection Section *******************************************

        val manufactureSelectAdapter = ArrayAdapter(
            myContext, R.layout.custom_textview, manufactureAry
        )
        manufactureFilter.setAdapter(manufactureSelectAdapter)


        if(manufactureAry.size > 5)
            manufactureFilter.dropDownHeight = 500

        manufactureFilter.setDropDownBackgroundDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.rounded_border,
                null
            )
        )

        if(manufactureAry.size > 5)
            manufactureFilter.dropDownHeight = 1000

        manufactureFilter.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, id->
                selectedManufactured = manufactureAry[position]
            }

//  ************************************* Manufacture Selection Section *******************************************


        dialogBuilder.setView(layoutView)
        val alertDialog = dialogBuilder.create()
        alertDialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()



        cancel_btn.setOnClickListener {
            imgPath = ""
            alertDialog.dismiss()
        }

        imgUploadLayout.setOnSingleClickListener {
            Helper.hideKeyboard(layoutView)
            request.send()
        }

        submit_btn.setOnClickListener {
            if(seatText.text.toString().isEmpty()){
                myContext.showToast("Please enter Seat Name")
            }else{
//                if(selectedAvailability.isNotEmpty()){
                    if(selectedFilterCategory.isNotEmpty()){


                        val categoryData = categoryList.filter { it.name == selectedFilterCategory }
                        val manufactureData = manufactureList.filter { it.manufactureName == selectedManufactured }

                        if(categoryData.isNotEmpty()) {
                            if(selectedManufactured.isNotEmpty()) {
                                if(manufactureData.isNotEmpty()) {
                                    if(manufacturedText.text.isNullOrEmpty()){
                                        myContext.showToast("Please enter quantity")
                                    }else{

                                            if(bitmapdata != null){

//                                                val bitmapWithFooter = ImageUtils().addFooterTextToBitmap(myContext,
//                                                    bitmapdata!!, String.format("%1s-%2s", seatText.text.toString(), selectedFilterCategory) )
//                                                imageFile = ImageUtils().compressBitmapTo2MB(myContext, bitmapWithFooter)

                                                carDataViewModel.addSeatData(
                                                    seatName = seatText.text.toString(),
                                                    carBrandId = brandId,
                                                    carModelId = modelId.toString(),
                                                    categoryId = categoryData.first().id.toString(),
                                                    manufactureId = manufactureData.first().manufactureId.toString(),
                                                    manufacturedQuantity = manufacturedText.text.toString(),
                                                    seatImage = imageFile
                                                )

                                                alertDialog.dismiss()
                                            }else{
                                                carDataViewModel.addSeatData(
                                                    seatName = seatText.text.toString(),
                                                    carBrandId = brandId,
                                                    carModelId = modelId.toString(),
                                                    categoryId = categoryData.first().id.toString(),
                                                    manufactureId = manufactureData.first().manufactureId.toString(),
                                                    manufacturedQuantity = manufacturedText.text.toString(),
                                                    seatImage = null
                                                )

                                                alertDialog.dismiss()
                                            }


                                    }
                                }else{
                                    myContext.showToast("invalid Manufacture")
                                }

                            }else{
                                myContext.showToast("Please select manufacture")
                            }
                        }else{
                            myContext.showToast("invalid category data exist")
                        }
                    }else{
                        myContext.showToast("Please select category")
                    }
//                }else{
//                    myContext.showToast("Please select Stock availability")
//                }

            }



        }

    }

    fun updateGridView(receivedCategory: String){



//        if(!tabAry.contains(receivedCategory)){
//            tabAry.add( receivedCategory)
//            refreshTabs(receivedCategory)
//        }

        val categoryData = responseSeatDataArray
            .firstOrNull { it.categoryName == receivedCategory }

        val index = tabAry.indexOfFirst { it.title == receivedCategory }

        if (index == -1) {
            val seats = categoryData?.totalStock ?: 0
            tabAry.add(SeatTab(title=receivedCategory, seats=seats) )
            refreshTabs(receivedCategory)
        }

//        val listToShow = when {
//            category == null -> emptyList()
//            stockAvailable -> category.seatDetailInStock
//            else -> category.seatDetailOutStock
//        }

        filterSeatAryData.clear()

        if (categoryData != null) {
            if (stockAvailable) {
                filterSeatAryData.addAll(categoryData.seatDetailInStock)
            } else {
                filterSeatAryData.addAll(categoryData.seatDetailOutStock)
            }
        }



        binding.dataNotFoundLayout.root
            .isVisible = filterSeatAryData.isEmpty()



        // 🔥 ONLY THIS LINE updates RecyclerView
        seatDataAdapter.submitList(filterSeatAryData.toList())

//        *****************************************************
//        val dataAry = response?.seatData?.filter {
//            it.categoryName == receivedCategory
//        } ?: ArrayList()


//        *****************
//        val dataAry = responseSeatDataArray.filter {
//            it.categoryName == receivedCategory
//        }
//
//        Log.d(TAG, "bindObservers: dataAry is ${receivedCategory} and ${dataAry.size}")
//        if(dataAry.isNotEmpty()){
//            seatAryData.clear()
//            filterSeatAryData.clear()
//
//            availableSeat.clear()
//            outStockSeat.clear()
//
//            for(res in dataAry){
//                availableSeat.addAll(res.seatDetailInStock)
//                outStockSeat.addAll(res.seatDetailOutStock)
//            }
//
//            if(stockAvailable){
//                seatAryData.addAll(availableSeat)
//                filterSeatAryData.addAll(availableSeat)
//            }else{
//                seatAryData.addAll(outStockSeat)
//                filterSeatAryData.addAll(outStockSeat)
//            }
//
//            if(seatAryData.isNotEmpty()){
//                binding.dataNotFoundLayout.root.gone()
//            }else{
//                binding.dataNotFoundLayout.root.visible()
//            }
//
//
//        }else{
//            seatAryData.clear()
//            filterSeatAryData.clear()
//            availableSeat.clear()
//            outStockSeat.clear()
//            binding.dataNotFoundLayout.root.visible()
//        }
//
//        seatDataAdapter.notifyDataSetChanged()
    }

    private fun addModelSeatBindObservers() {
        carDataViewModel.addSeatDataResponseLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is NetworkResult.Success -> {

                    hideLoader(myContext, loader)

                    val seatDataAry = it.data?.seatData ?: ArrayList()

                    imageFile = null
                    bitmapdata = null

                    if(seatDataAry.isNotEmpty()){
                        val updatedSeatDataAry : ArrayList<SeatDetail> = ArrayList()
                        for (i in 0 until seatDataAry.size){
                            updatedSeatDataAry.add(
                                SeatDetail(
                                    id = seatDataAry[i].seatId,
                                    name = seatDataAry[i].seatName,
                                    carBrandName = brandName,
                                    carModelName = seatDataAry[i].carModelName,
                                    categoryName = seatDataAry[i].categoryName,
                                    material = seatDataAry[i].material,
                                    availableStock = seatDataAry[i].availableStock,
                                    stockStatus = if(seatDataAry[i].availableStock == 0) "out_of_stock" else "in_stock",
                                    seatImage = seatDataAry[i].seatImage,
                                    manufacturersDetail = seatDataAry[i].manufactures
                                )
                            )
                        }

                        upsertSeatWithCategory(responseSeatDataArray, updatedSeatDataAry)


//                        val seatData = seatDataAry.first()
//                        val newData = SeatDetail(
//                            id = seatData.seatId,
//                            name = seatData.seatName,
//                            carBrandName = brandName,
//                            carModelName = seatData.carModelName,
//                            categoryName = seatData.categoryName,
//                            material = seatData.material,
//                            availableStock = seatData.availableStock,
//                            stockStatus = if(seatData.availableStock == 0) "out_of_stock" else "in_stock",
//                            seatImage = seatData.seatImage,
//                            manufacturersDetail = seatData.manufactures
//                        )

//                        Log.d(TAG, "addModelSeatBindObservers: selected category => $selectedCategory = ${seatData.categoryName} and ${seatData.availableStock}")

//                        if(tabAry.isEmpty()){
//                            val seatData = seatDataAry.first()
//                            tabAry.add( seatData.categoryName.toString())
//                            refreshTabs(seatData.categoryName)
//
//                            if(seatData.availableStock == 0) {
//                                outStockSeat.add(newData)
//                                responseSeatDataArray.add(
//                                    SeatCategoryModel(
//                                        categoryName = newData.categoryName,
//                                        seatDetailInStock = ArrayList(),
//                                        seatDetailOutStock = outStockSeat
//                                    )
//                                )
//
//                            }else{
//                                availableSeat.add(newData)
//                                responseSeatDataArray.add(
//                                    SeatCategoryModel(
//                                        categoryName = newData.categoryName,
//                                        seatDetailInStock = availableSeat,
//                                        seatDetailOutStock = ArrayList()
//                                    )
//                                )
//                            }
//
//                            updateGridView(seatData.categoryName.toString())
//
//                            return@Observer
//                        }else{
////                            upsertSeatWithCategory(responseSeatDataArray, newData)
//                            upsertSeatWithCategory(responseSeatDataArray, updatedSeatDataAry)
//                        }

//**********************************************************************
//                        if( selectedCategory.isNotEmpty() && selectedCategory  == seatData.categoryName){
//                            if(seatData.availableStock == 0) {
//                                outStockSeat.add(newData)
//                                if (!stockAvailable) {
//                                    seatAryData.add(newData)
//                                    filterSeatAryData.add(newData)
//                                    seatDataAdapter.notifyDataSetChanged()
////                                gridViewAdapter.add(newData)
//                                }
//                            }else{
//                                availableSeat.add(newData)
//                                if (stockAvailable) {
//                                    seatAryData.add(newData)
//                                    filterSeatAryData.add(newData)
//                                    seatDataAdapter.notifyDataSetChanged()
////                                gridViewAdapter.add(newData)
//                                }
//                            }
//                        }else{
////                            val seatDataArray = response?.seatData ?: ArrayList()
//
//                            val index = responseSeatDataArray.indexOfFirst { it.categoryName ==  seatData.categoryName}
//                            if(index != -1){
//                                if(seatData.availableStock == 0) {
//                                    responseSeatDataArray[index].seatDetailOutStock.add(newData)
//                                }else{
//                                    responseSeatDataArray[index].seatDetailInStock.add(newData)
//                                }
//                            }else{
//
//                                val StockAry: ArrayList<SeatDetail> = ArrayList()
//                                StockAry.add(newData)
//                                if(seatData.availableStock == 0){
//                                    responseSeatDataArray.add(
//                                        SeatCategoryModel(
//                                            categoryName = seatData.categoryName,
//                                            seatDetailInStock = ArrayList<SeatDetail>(),
//                                            seatDetailOutStock = StockAry
//                                        )
//                                    )
//                                }else{
//                                    responseSeatDataArray.add(
//                                        SeatCategoryModel(
//                                            categoryName = seatData.categoryName,
//                                            seatDetailInStock = StockAry,
//                                            seatDetailOutStock = ArrayList<SeatDetail>()
//                                        )
//                                    )
//                                }
//
//                                if(tabAry.isNotEmpty()) {
//                                    if (!tabAry.contains(seatData.categoryName)) {
//                                        tabAry.add(tabAry.size , seatData.categoryName.toString())
//                                        refreshTabs(seatData.categoryName)
//                                    }
//                                }
//
//                            }
//
////                            updateGridView(seatData.categoryName.toString())
//                        }


                    }


                    carDataViewModel.clearAddSeatRes()
                }
                is NetworkResult.Error -> {
                    hideLoader(myContext, loader)

                    alertDialogService.alertDialogAnim(
                        myContext,
                        it.message.toString(),
                        R.raw.failed
                    )
                    Log.d(TAG, "bindObservers: response received => Error = ${it.message}")
                }
                is NetworkResult.Loading ->{
                    showLoader(myContext, loader)
                }
                is NetworkResult.Empty -> {
                    hideLoader(myContext, loader)
                }
            }
        })
    }

    private fun setupTabListener() {
        binding.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                selectedCategory = tabAry[tab.position].title
                Log.d(TAG, "Selected tab => $selectedCategory")
                seatDataAdapter.clearSelection()
                binding.selectAllLayout.gone()
                binding.deleteModelSeatBtn.gone()
                updateGridView(selectedCategory)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun refreshTabs(selectCategory: String? = null) {
        binding.tabs.removeAllTabs()


// Pehle tabs add karo
        tabAry.forEach {
            binding.tabs.addTab(binding.tabs.newTab())
        }

        // Ab custom view set karo
        for (i in tabAry.indices) {
            val tab = binding.tabs.getTabAt(i)
            val view = LayoutInflater.from(myContext)
                .inflate(R.layout.custom_tab, null)

            view.findViewById<TextView>(R.id.tabTitle).text = tabAry[i].title
            view.findViewById<TextView>(R.id.tabSeats).text =
                "${tabAry[i].seats}"

            tab?.customView = view
        }

        val selectedIndex = tabAry.indexOfFirst { it.title == selectCategory }
            .takeIf { it != -1 } ?: 0

        if (selectedIndex >= 0) {
            binding.tabs.getTabAt(selectedIndex)?.select()
        }

//        tabAry.forEach {
////            binding.tabs.addTab(binding.tabs.newTab().setText(it))
//            binding.tabs.addTab(binding.tabs.newTab().setText(it))
//        }

//        val index = selectCategory?.let { tabAry.indexOf(it) } ?: 0
//        if (index >= 0) {
//            binding.tabs.getTabAt(index)?.select()
//        }
    }

    fun upsertSeatWithCategory(
        responseSeatDataArray: MutableList<SeatCategoryModel>,
        updatedData: ArrayList<SeatDetail>
    ) {

        val affectedCategories = mutableSetOf<String>()

        val categoryName = updatedData.first().categoryName

        updatedData.forEach { updatedSeat ->

            var oldCategory: SeatCategoryModel? = null

            // 1️⃣ Remove seat from ALL categories (safe)
            responseSeatDataArray.forEach { category ->

                val removedFromInStock =
                    category.seatDetailInStock.removeAll { it.id == updatedSeat.id }

                val removedFromOutStock =
                    category.seatDetailOutStock.removeAll { it.id == updatedSeat.id }

                if (removedFromInStock || removedFromOutStock) {
                    oldCategory = category
                    affectedCategories.add(category.categoryName.toString())
                }
            }

            // 2️⃣ Find or create target category
            var targetCategory =
                responseSeatDataArray.find { it.categoryName == updatedSeat.categoryName }

            if (targetCategory == null) {
                targetCategory = SeatCategoryModel(
                    categoryName = updatedSeat.categoryName,
                    seatDetailInStock = ArrayList(),
                    seatDetailOutStock = ArrayList()
                )
                responseSeatDataArray.add(targetCategory)
                tabAry.add(SeatTab(title = updatedSeat.categoryName.toString(), seats = updatedSeat.availableStock!!) )
                refreshTabs(updatedSeat.categoryName.toString())
            }

            // 3️⃣ Insert based on stock
            updatedSeat.availableStock?.let {
                if (it > 0) {
                    targetCategory.seatDetailInStock.add(updatedSeat)
                } else {
                    targetCategory.seatDetailOutStock.add(updatedSeat)
                }
            }

            affectedCategories.add(updatedSeat.categoryName.toString())

            // 4️⃣ Remove empty old category
            oldCategory?.let {
                if (it.seatDetailInStock.isEmpty() && it.seatDetailOutStock.isEmpty()) {
                    responseSeatDataArray.remove(it)
                    tabAry.remove(SeatTab(title = it.categoryName.toString(), seats = it.totalStock!!))
                    refreshTabs(categoryName.toString())
                }
            }
        }



        val index = tabAry.indexOfFirst { it.title == categoryName }

        if (index != -1) {
            binding.tabs.getTabAt(index)?.select()
        }

        // 5️⃣ Refresh grid for current category (safe)
        updateGridView(categoryName.toString())

    }

    fun deleteSeatsFromCategories(
        responseSeatDataArray: MutableList<SeatCategoryModel>,
        seatsToDelete: List<SeatDetail>
    ) {

        val affectedCategories = mutableSetOf<String>()

        seatsToDelete.forEach { seatToDelete ->

            var oldCategory: SeatCategoryModel? = null

            // 1️⃣ Remove seat from ALL categories
            responseSeatDataArray.forEach { category ->

                val removedFromInStock =
                    category.seatDetailInStock.removeAll { it.id == seatToDelete.id }

                val removedFromOutStock =
                    category.seatDetailOutStock.removeAll { it.id == seatToDelete.id }

                if (removedFromInStock || removedFromOutStock) {
                    oldCategory = category
                    affectedCategories.add(category.categoryName.toString())
                }
            }

            // 2️⃣ Remove empty old category
            oldCategory?.let {
                if (it.seatDetailInStock.isEmpty() && it.seatDetailOutStock.isEmpty()) {
                    responseSeatDataArray.remove(it)
                    tabAry.remove(SeatTab(title = it.categoryName.toString(), seats = it.totalStock ?: 0))
                    refreshTabs(it.categoryName.toString())
                }
            }
        }

        // 3️⃣ Select first affected category tab
        val firstCategory = affectedCategories.firstOrNull()
        firstCategory?.let {
            val index = tabAry.indexOfFirst { tab -> tab.title == it }
            if (index != -1) binding.tabs.getTabAt(index)?.select()
            updateGridView(it)
        }
    }

    private fun createImageFile(): File? {
        val directory = File(myContext.filesDir, "GaneshSeats")

        if (!directory.exists()) {
            val isDirectoryCreated = directory.mkdirs()

            if (isDirectoryCreated) {
                val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                val storageDir: File? = myContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

                return File.createTempFile(
                    "JPEG_${timeStamp}_", /* prefix */
                    ".jpg", /* suffix */
                    directory /* directory */
                ).apply {
                    // Save a file: path for use with ACTION_VIEW intents
                    currentPhotoPath = absolutePath
                }
            } else{
                myContext.showToast("something went wrong")
                return null
            }
        } else {
            Log.d(TAG, "createImageFile: enter that function for create file")
            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val storageDir: File? = myContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            Log.d(TAG, "createImageFile: storage dir => $storageDir ")
            return File.createTempFile(
                "JPEG_${timeStamp}_", /* prefix */
                ".jpg", /* suffix */
                directory /* directory */
            ).apply {
                // Save a file: path for use with ACTION_VIEW intents
                currentPhotoPath = absolutePath
            }
        }

    }

    private fun showMediaBottomSheet() {

        // ⛔ Already showing → return
        if (mediaBottomSheet?.isShowing == true) return

        mediaBottomSheet = BottomSheetDialog(myContext).apply {

            setContentView(R.layout.selectmedia_dialog)

            val cameraBtn = findViewById<ImageButton>(R.id.camera_circle)
            val galleryBtn = findViewById<ImageButton>(R.id.gallery_circle)

            setOnDismissListener {
                mediaBottomSheet = null // reset
            }

            cameraBtn?.setOnClickListener {
                dismiss()
                openCamera()
            }

            galleryBtn?.setOnClickListener {
                dismiss()
                openGallery()
            }

            show()
        }
    }

    private fun openCamera() {
        cameraImageUri = createImageUri()
        cameraImageUri?.let { cameraLaunch.launch(it) }
    }

    private fun openGallery() {
        pickMedia.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    private fun createImageUri(): Uri {
        val imageFile = File(
            myContext.cacheDir,
            "images/${System.currentTimeMillis()}.jpg"
        )
        imageFile.parentFile?.mkdirs()

        return FileProvider.getUriForFile(
            myContext,
            "${myContext.packageName}.provider",
            imageFile
        )
    }

    private fun uploadImage(uri: Uri) {

        lifecycleScope.launch(Dispatchers.IO) {

//            imageFile = ImageUtils().compressImageTo2MB(myContext, uri)


            val footerText = "Ganesh Seats Industries Pvt Ltd, Jodhpur Rajasthan India"

            val originalBitmap = ImageUtils().getBitmapFromUri(myContext, uri)

            bitmapdata = ImageUtils().resizeBitmap(originalBitmap, 1920)

            imageFile = ImageUtils().compressBitmapTo2MB(myContext, bitmapdata!!)

            withContext(Dispatchers.Main) {

                dialogSeatImage?.let { imageView ->
                    Glide.with(myContext)
                        .load(uri) // 👈 Direct URI
                        .placeholder(R.drawable.error_img)
                        .error(R.drawable.error_img)
                        .into(imageView)
                }

                dialogSeatImageDefault?.gone()

            }
        }



//        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
//        val body = MultipartBody.Part.createFormData(
//            "image",
//            file.name,
//            requestFile
//        )

        // Retrofit API call here
    }

    override fun onPause() {
        super.onPause()
        mediaBottomSheet?.dismiss()
        mediaBottomSheet = null
    }


    override fun onPermissionsResult(result: List<PermissionStatus>) {

        when {
            result.anyPermanentlyDenied() -> myContext.showPermanentlyDeniedDialog(myContext, result, "Please allowed camera Permission"){
                val intent = Intent().apply {
                    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    data = Uri.fromParts("package", myContext.packageName, null)
                }
                permissionLauncher.launch(intent)
            }
            result.anyShouldShowRationale() -> myContext.showRationaleDialog(myContext, result, request, "Please allowed camera Permission")
            result.allGranted() -> {
//                chooseImage()
                if (isAdded && !isDetached) {
                    showMediaBottomSheet()
                }
            }
        }

    }


    private fun shareSelectedImages(
        context: Context,
        selectedItems: List<SeatDetail>
    ) {

        val uriList = ArrayList<Uri>()

        lifecycleScope.launch(Dispatchers.IO) {

            val cacheDir = File(context.cacheDir, "shared_images").apply {
                if (!exists()) mkdirs()
            }

            selectedItems.filter { it.seatImage != null }

            Log.d(TAG, "shareSelectedImages: selected image length => ${selectedItems}")

            selectedItems.forEachIndexed { index, item ->
                Log.d(TAG, "shareSelectedImages: selected image length => load bitMap ${item.seatImage.toString()} ${selectedItems.size}")
                val bitmap = loadBitmapFromUrl(item.seatImage.toString()) ?: return@forEachIndexed

                val bitmapWithFooter = ImageUtils().addFooterTextToBitmap(myContext,
                    bitmap, String.format("%1s-%2s", item.name.toString(), selectedCategory) )

//                val finalBitmap = addFooterToImage(bitmap, item.name.toString())

                val file = File(
                    cacheDir,
                    "seat_${System.currentTimeMillis()}_$index.jpg"
                )

                FileOutputStream(file).use { fos ->
                    bitmapWithFooter.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                }

                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    file
                )
                uriList.add(uri)
            }

            withContext(Dispatchers.Main) {
                if (uriList.isNotEmpty()) {
                    shareMultipleUris(context, uriList)
                }else{
                    hideLoader(myContext, loader)
                    myContext.showToast("Something wrong on image share, please contact developer.")
                }
            }
        }
    }


    suspend fun loadBitmapFromUrl(imageUrl: String): Bitmap? {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL(imageUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.connect()
                BitmapFactory.decodeStream(connection.inputStream)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }


    private fun shareMultipleUris(
        context: Context,
        uriList: ArrayList<Uri>
    ) {
        hideLoader(myContext, loader)
        val intent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
            type = "image/*"
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriList)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(
            Intent.createChooser(intent, "Share Images")
        )
    }

}