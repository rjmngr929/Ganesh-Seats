package com.my.ganeshseats.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
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
import com.my.ganeshseats.adapters.DisplayImageCarousalAdapter
import com.my.ganeshseats.data.response.CategoryDataModel
import com.my.ganeshseats.data.response.ManufactureModelSelect
import com.my.ganeshseats.data.response.SeatDetail
import com.my.ganeshseats.databinding.ActivityDisplayImageBinding
import com.my.ganeshseats.ui.fragments.SeatCoversFragment
import com.my.ganeshseats.ui.viewmodel.CarModelViewModel
import com.my.ganeshseats.ui.viewmodel.MasterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import kotlin.getValue

@AndroidEntryPoint
class DisplayImageActivity : AppCompatActivity(), PermissionRequest.Listener {

    companion object{
        private const val TAG = "Display Image Activity"
    }

    private lateinit var binding: ActivityDisplayImageBinding

    private var mediaBottomSheet: BottomSheetDialog? = null

    @Inject
    lateinit var alertDialogService: AlertDialogUtility

    private val masterViewModel: MasterViewModel by viewModels ()

    private val carDataViewModel: CarModelViewModel by viewModels ()

    private var selectedCategory: String = ""

    private var selectedAvailability = ""
    private var selectedFilterCategory = ""

    private var selectedFilterManufactured = ""

    private lateinit var imgPath : String

    private var carBrandId = ""
    private var carModelId = ""

    private var selectedSeatId: Int = 0

    private var categoryAry : ArrayList<String> = ArrayList()

//    private var seatDetail : SeatDetail = SeatDetail()
    private var categoryDataAry : ArrayList<CategoryDataModel> = ArrayList()
    private var dialogSeatImage: ImageView? = null
    private var dialogSeatImageDefault: LinearLayout? = null

    private lateinit var adapter: DisplayImageCarousalAdapter

    private var seatArrayData = ArrayList<SeatDetail>()

    private var manufactureAry : ArrayList<String> = ArrayList()
    private var manufactureDataAry : ArrayList<ManufactureModelSelect> = ArrayList()

    private lateinit var loader: AlertDialog

    private var imgUrl: String? = null

    private var photoURI: Uri? = null;
    private  var bitmapdata : Bitmap? = null
    private lateinit var resized : Bitmap
    private lateinit var currentPhotoPath: String

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
            showMediaBottomSheet()
        }else{
            request.send()
        }

    }

    private var cameraImageUri: Uri? = null
    private var imageFile: File? = null

    private val cameraLaunch =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success && cameraImageUri != null) {
                uploadImage(cameraImageUri!!)
            }
        }

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { it ->
        // photo picker.
        if(it != null){
            uploadImage(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDisplayImageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            (v.layoutParams as ViewGroup.MarginLayoutParams).apply {
                topMargin = systemBars.top
                bottomMargin = systemBars.bottom
            }
            insets
        }

        val toolbar = binding.displayImageToolbar.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.outline_arrow_back_24)

        request.addListener(this)

        loader = getLoadingDialog(this)

        carModelId = intent.getStringExtra("carModelId").toString()
        carBrandId = intent.getStringExtra("brandId").toString()
        selectedSeatId = masterViewModel.selectedSeatId.value!!


//        masterViewModel.seatDetailData.observe(this, Observer{it->
//            Log.d(TAG, "onCreate: display title level 1 => ${it.name}")
//            if(it!= null){
//                Log.d(TAG, "onCreate: display title level 2 => ${it.name}")
//                supportActionBar?.title = it.name
//
//                seatDetail = it
//
//                imgUrl = it.seatImage
//                Glide.with(this)
//                    .load(it.seatImage)
//                    .error(R.drawable.error_img)
//                    .placeholder(R.drawable.ic_baseline_photo_library_24)
//                    .into(binding.imgPreview)
//
//                val manufactureAry = seatDetail.manufacturersDetail
//
//                if(manufactureAry.isNotEmpty()){
//                    binding.manufactureLayout.visible()
//                    manufactureAry.forEach { item ->
//                        val view = layoutInflater.inflate(
//                            R.layout.manufacture_section_layout,
//                            binding.manufactureLayout,
//                            false
//                        )
//                        view.findViewById<TextView>(R.id.name_text).text = item.manufatureName?.ifEmpty { "N/A" }
//                        view.findViewById<TextView>(R.id.mobile_text).text = item.manufatureNumber ?: "N/A"
//                        view.findViewById<TextView>(R.id.set_text).text = item.manufacturedSet.toString().ifEmpty { "N/A" }
//                        view.findViewById<TextView>(R.id.date_text).text = item.date.toString().ifEmpty { "N/A" }
//                        binding.manufactureLayout.addView(view)
//                    }
//                }else{
//                    binding.manufactureLayout.gone()
//                }
//
//
//
//            }
//
//        })

        masterViewModel.categoryData.observe(this, Observer{
            categoryDataAry.addAll(it as ArrayList)
        })

        masterViewModel.categoryList.observe(this, Observer{
            categoryAry.addAll(it as ArrayList)
        })

        masterViewModel.manufactureData.observe(this, Observer{
            manufactureDataAry.addAll(it as ArrayList)
        })

        masterViewModel.manufactureList.observe(this, Observer{
            manufactureAry.addAll(it as ArrayList)
        })


        masterViewModel.seatArrayData.observe(this, Observer{data ->
            seatArrayData.clear()
            seatArrayData.addAll(data)
            adapter.updateSeatArray(data)
            Log.d(TAG, "onCreate: seat id is => $selectedSeatId")
            Log.d(TAG, "onCreate: seat id is => $seatArrayData")
            val currentIndex = seatArrayData.indexOfFirst {it.id == selectedSeatId }
            if(currentIndex != -1){
                binding.viewPager.setCurrentItem(currentIndex, false)
            }
        })

        adapter = DisplayImageCarousalAdapter(this, seatArrayData)
        binding.viewPager.adapter = adapter




        binding.editBtn.setOnClickListener {
            updateSeatAlert()
        }

        binding.shareBtn.setOnClickListener {
            showLoader(this, loader)
            ShareImage()
        }

        binding.deleteBtn.setOnClickListener {
            val index = binding.viewPager.currentItem
            val data = seatArrayData[index]

            carDataViewModel.deleteSeatData(arrayListOf(data.id.toString()))
        }

        updateSeatDataListener()

        deleteSeatsBindObservers()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                super.onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("MissingInflatedId")
    fun updateSeatAlert(){
        val index = binding.viewPager.currentItem
        val seatDetail = seatArrayData[index]


        imgPath = ""
        var dialogBuilder = AlertDialog.Builder(this)
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

        alertTitle.text = "Update Product details"

        val imgUploadLayout : RelativeLayout = layoutView.findViewById(R.id.imgUpload_layout_product)
        dialogSeatImage  = layoutView.findViewById(R.id.previewImg_product)
        dialogSeatImageDefault  = layoutView.findViewById(R.id.img_default_product)


        selectAvailability.gone()


//  ************************************* Product Quality Section *******************************************

        val categorySelectAdapter = ArrayAdapter(
            this, R.layout.custom_textview, categoryAry
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

//        *************** pre filled ***************************
        selectedFilterCategory = seatDetail.categoryName.toString()
        productQualityFilter.setText(selectedFilterCategory, false)
//        *************** pre filled ***************************


        if(categoryAry.size > 5)
            productQualityFilter.dropDownHeight = 1000

        productQualityFilter.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, id->
                selectedFilterCategory = categoryAry[position]
            }

//  ************************************* Product Quality Section *******************************************

//  ************************************* Manufacture Select Section *******************************************

        val manufactureSelectAdapter = ArrayAdapter(
            this, R.layout.custom_textview, manufactureAry
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

//        *************** pre filled ***************************
        val manufacturedData = seatDetail.manufacturersDetail
        if(manufacturedData.isNotEmpty()){
            selectedFilterManufactured = manufacturedData.first().manufatureName.toString()
            manufactureFilter.setText(selectedFilterManufactured, false)

            manufacturedText.setText(manufacturedData.first().manufacturedSet.toString())
        }


//        *************** pre filled ***************************


        if(manufactureAry.size > 5)
            manufactureFilter.dropDownHeight = 1000

        manufactureFilter.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, id->
                selectedFilterManufactured = manufactureAry[position]
            }

//  ************************************* Manufacture Select Section *******************************************


        seatText.setText(seatDetail.name)

        dialogSeatImage?.let {
            Glide.with(this@DisplayImageActivity)
                .load(seatDetail.seatImage)
                .placeholder(R.drawable.ic_baseline_photo_library_24) // Optional: placeholder while loading
                .error(R.drawable.error_img) // Optional: error image if loading fails
                .into(it)
        }
        dialogSeatImageDefault?.gone()



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
//            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        submit_btn.setOnClickListener {
            if(seatText.text.toString().isEmpty()){
                showToast("Please enter Seat Name")
            }else{
                if(selectedFilterCategory.isNotEmpty()){

                    val categoryData = categoryDataAry.filter { it.name == selectedFilterCategory }
                    val manufactureData = manufactureDataAry.filter { it.manufactureName == selectedFilterManufactured }

                    if(categoryData.isNotEmpty()) {
                        if(selectedFilterManufactured.isNotEmpty()) {
                            if (manufactureData.isNotEmpty()) {
                                if (manufacturedText.text.isNullOrEmpty()) {
                                    showToast("Please enter quantity")
                                } else {
                                    if(bitmapdata != null){

//                                        val bitmapWithFooter = ImageUtils().addFooterTextToBitmap(this,
//                                            bitmapdata!!, String.format("%1s-%2s", seatText.text.toString(), selectedFilterCategory) )
//                                        imageFile = ImageUtils().compressBitmapTo2MB(this, bitmapWithFooter)

                                        carDataViewModel.updateSeatData(
                                            seatId = seatDetail.id.toString(),
                                            seatName = seatText.text.toString(),
                                            carBrandId = carBrandId,
                                            carModelId = carModelId,
                                            status = "active",
                                            categoryId = categoryData.first().id.toString(),
                                            manufactureId = manufactureData.first().manufactureId.toString(),
                                            manufacturedQuantity = manufacturedText.text.toString(),
                                            seatImage = imageFile
                                        )

                                        alertDialog.dismiss()
                                    } else {
                                        carDataViewModel.updateSeatData(
                                            seatId = seatDetail.id.toString(),
                                            seatName = seatText.text.toString(),
                                            carBrandId = carBrandId,
                                            carModelId = carModelId,
                                            categoryId = categoryData.first().id.toString(),
                                            status = "active",
                                            manufactureId = manufactureData.first().manufactureId.toString(),
                                            manufacturedQuantity = manufacturedText.text.toString(),
                                            seatImage = null
                                        )

                                        alertDialog.dismiss()
                                    }
                                }
                            } else {
                                showToast("invalid Manufacture")
                            }
                        }else{
                            showToast("Please select manufacture")
                        }
                    }else{
                       showToast("invalid category data exist")
                    }
                }else{
                    showToast("Please select category")
                }

            }



        }

    }

    private fun updateSeatDataListener() {
        carDataViewModel.updateSeatDataResponseLiveData.observe(this, Observer {res ->
            when (res) {
                is NetworkResult.Success -> {


                    val seatDataAry = res.data?.seatData ?: ArrayList()

                    imageFile = null

                    Log.d(TAG, "updateSeatDataListener: seat detail Update success!!")

                    val index = binding.viewPager.currentItem
                    val data = seatArrayData[index]

                    if(seatDataAry.isNotEmpty()){
                        val seatData = seatDataAry.first()

                        Log.d(TAG, "updateSeatDataListener: available stock data is ${seatData.availableStock}")
                        val newData = SeatDetail(
                            id = seatData.seatId,
                            name = seatData.seatName,
                            carBrandName = data.carBrandName,
                            carModelName = seatData.carModelName,
                            categoryName = seatData.categoryName,
                            material = seatData.material,
                            availableStock = seatData.availableStock,
                            stockStatus = if(seatData.availableStock == 0) "out_of_stock" else "in_stock",
                            seatImage = seatData.seatImage,
                            manufacturersDetail = seatData.manufactures
                        )

                        bitmapdata = null

                        carDataViewModel.clearUpdateSeatRes()
                        masterViewModel.setUpdatedSeatData(newData)
                        masterViewModel.refreshData.value = true
//                        finish()

                        val currentIndex = binding.viewPager.currentItem
                        val size = seatArrayData.size

                        if (size == 0) {
                            finish()
                        }// nothing to update

// 1️⃣ update item
                       seatArrayData[currentIndex] = newData


// 2️⃣ notify adapter
                        adapter.notifyItemChanged(currentIndex)

// 3️⃣ optionally, stay on same page
                        binding.viewPager.setCurrentItem(currentIndex, false)



                    }

                    hideLoader(this, loader)



                }
                is NetworkResult.Error -> {
                    Log.d(TAG, "fetchBrandListener: error data => $res")
                    hideLoader(this, loader)
                    alertDialogService.alertDialogAnim(this, res.message.toString(), R.raw.failed)

                }
                is NetworkResult.Loading ->{
                    showLoader(this, loader)
                }
                is NetworkResult.Empty -> {
                    hideLoader(this, loader)
                }
            }
        })
    }

    private fun deleteSeatsBindObservers() {

        carDataViewModel.deleteSeatDataResponseLiveData.observe(this, Observer {
            when (it) {
                is NetworkResult.Success -> {

                    hideLoader(this, loader)

                    val resData = it.data?.seatData?.toList() ?: listOf()

                    Log.d(TAG, "deleteSeatsBindObservers: call this time => $seatArrayData")

//                    seatArrayData.filter { it.id == resData[0].id }
//
//                    masterViewModel.setSeatArrayData(seatArrayData)

                    val currentIndex = binding.viewPager.currentItem
                    val size = seatArrayData.size

                    if (size == 0){
                        finish()
                    }

                    // 1️⃣ item remove
                    seatArrayData.removeAt(currentIndex)

                    adapter.notifyItemRemoved(currentIndex)

                    // 2️⃣ decide next index
                    val newSize = seatArrayData.size
                    if (newSize == 0) {
                        finish()
                    }

                    val nextIndex = when {
                        currentIndex < newSize -> currentIndex          // next item exists
                        else -> newSize - 1                              // show previous
                    }

                    // 3️⃣ set new index
                    binding.viewPager.setCurrentItem(nextIndex, false)

                    carDataViewModel.clearDeleteSeatRes()
                }
                is NetworkResult.Error -> {
                    hideLoader(this, loader)

                    alertDialogService.alertDialogAnim(
                        this,
                        it.message.toString(),
                        R.raw.failed
                    )
                    Log.d(TAG, "bindObservers: response received => Error = ${it.message}")
                }
                is NetworkResult.Loading ->{
                    showLoader(this, loader)
                }
                is NetworkResult.Empty -> {
                    hideLoader(this, loader)
                }
            }
        })
    }
    private fun showMediaBottomSheet() {

        // ⛔ Already showing → return
        if (mediaBottomSheet?.isShowing == true) return

        mediaBottomSheet = BottomSheetDialog(this).apply {

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
            this.cacheDir,
            "images/${System.currentTimeMillis()}.jpg"
        )
        imageFile.parentFile?.mkdirs()

        return FileProvider.getUriForFile(
            this,
            "${packageName}.provider",
            imageFile
        )
    }

    private fun uploadImage(uri: Uri) {

        lifecycleScope.launch(Dispatchers.IO) {

            val footerText = "Ganesh Seats Industries Pvt Ltd, Jodhpur Rajasthan India"

            val originalBitmap = ImageUtils().getBitmapFromUri(this@DisplayImageActivity, uri)

            bitmapdata = ImageUtils().resizeBitmap(originalBitmap, 1920)

//            val bitmapWithFooter = ImageUtils().addFooterTextToBitmap(this@DisplayImageActivity,resizedBitmap, footerText)
//
//            imageFile = ImageUtils().compressBitmapTo2MB(this@DisplayImageActivity, bitmapWithFooter)


            imageFile = ImageUtils().compressBitmapTo2MB(this@DisplayImageActivity, bitmapdata!!)

            withContext(Dispatchers.Main) {

                dialogSeatImage?.let { imageView ->
                    Glide.with(this@DisplayImageActivity)
                        .load(uri) // 👈 Direct URI
                        .placeholder(R.drawable.error_img)
                        .error(R.drawable.error_img)
                        .into(imageView)
                }

                dialogSeatImageDefault?.gone()

            }
        }

    }


    override fun onPause() {
        super.onPause()
        mediaBottomSheet?.dismiss()
        mediaBottomSheet = null
    }

    override fun onDestroy() {
        super.onDestroy()
        carDataViewModel.updateSeatDataResponseLiveData.removeObservers(this)
        carDataViewModel.deleteSeatDataResponseLiveData.removeObservers(this)
//        masterViewModel.setSeatArrayData(seatArrayData)
    }

    override fun onPermissionsResult(result: List<PermissionStatus>) {

        when {
            result.anyPermanentlyDenied() -> showPermanentlyDeniedDialog(this, result, "Please allowed camera Permission"){
                val intent = Intent().apply {
                    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    data = Uri.fromParts("package", packageName, null)
                }
                permissionLauncher.launch(intent)
            }
            result.anyShouldShowRationale() -> showRationaleDialog(this, result, request, "Please allowed camera Permission")
            result.allGranted() -> {
//                chooseImage()
                showMediaBottomSheet()
            }
        }

    }

    suspend fun loadBitmapFromUrl(imageUrl: String?): Bitmap? {
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

    fun ShareImage(){
        val index = binding.viewPager.currentItem
        val data = seatArrayData[index]

        if(!data.seatImage.isNullOrEmpty()){
            lifecycleScope.launch(Dispatchers.IO) {

                Log.d(TAG, "shareSelectedImages: selected image length => load bitMap ${data.seatImage}")
                val bitmap = loadBitmapFromUrl(data.seatImage)

                val bitmapWithFooter = ImageUtils().addFooterTextToBitmap(this@DisplayImageActivity,
                    bitmap!!, String.format("%1s-%2s", data.name.toString(), data.categoryName.toString()) )

                val file = File(
                    cacheDir,
                    "seat_${System.currentTimeMillis()}.jpg"
                )

                FileOutputStream(file).use { fos ->
                    bitmapWithFooter?.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                }

                val uri = FileProvider.getUriForFile(
                    this@DisplayImageActivity,
                    "${packageName}.provider",
                    file
                )


                withContext(Dispatchers.Main) {
                    hideLoader(this@DisplayImageActivity, loader)
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "image/*"
                        putExtra(Intent.EXTRA_STREAM, uri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }

                    startActivity(
                        Intent.createChooser(intent, "Share Images")
                    )
                }
            }
        }else{
            hideLoader(this, loader)
            showToast("Image not found")
        }



    }


}