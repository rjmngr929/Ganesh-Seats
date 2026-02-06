package com.my.ganeshseats.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.GridView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.SearchView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.Lottie
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.my.ganeshseats.R
import com.my.ganeshseats.Utils.AlertDialogUtility
import com.my.ganeshseats.Utils.NetworkResult
import com.my.ganeshseats.Utils.getLoadingDialog
import com.my.ganeshseats.Utils.gone
import com.my.ganeshseats.Utils.hideLoader
import com.my.ganeshseats.Utils.showLoader
import com.my.ganeshseats.Utils.showToast
import com.my.ganeshseats.Utils.visible
import com.my.ganeshseats.data.response.BrandData
import com.my.ganeshseats.databinding.ActivityMainBinding
import com.my.ganeshseats.prefs.SharedPrefManager
import com.my.ganeshseats.ui.viewmodel.CarModelViewModel
import com.my.ganeshseats.ui.viewmodel.MasterViewModel
import com.my.ganeshseats.ui.viewmodel.UserDataViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.regex.Pattern
import javax.inject.Inject
import androidx.core.graphics.scale
import androidx.core.net.toUri
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.kotlinpermissions.PermissionStatus
import com.kotlinpermissions.allGranted
import com.kotlinpermissions.anyPermanentlyDenied
import com.kotlinpermissions.anyShouldShowRationale
import com.kotlinpermissions.extension.permissionsBuilder
import com.kotlinpermissions.request.PermissionRequest
import com.my.ganeshseats.Utils.Helper
import com.my.ganeshseats.Utils.ImageUtils
import com.my.ganeshseats.Utils.setOnSingleClickListener
import com.my.ganeshseats.Utils.showPermanentlyDeniedDialog
import com.my.ganeshseats.Utils.showRationaleDialog
import com.my.ganeshseats.adapters.BrandRecyclerViewAdapter
import com.my.ganeshseats.adapters.CarModelsRecyclerviewAdapter
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), PermissionRequest.Listener  {

    companion object{
        private const val TAG = "Main Activity"
    }

    private lateinit var binding : ActivityMainBinding

    private var mediaBottomSheet: BottomSheetDialog? = null

    @Inject
    lateinit var alertDialogService: AlertDialogUtility

    @Inject
    lateinit var sharedPrefManager: SharedPrefManager

    private lateinit var loader: AlertDialog

    private val carDataViewModel: CarModelViewModel by viewModels()

    private val masterViewModel: MasterViewModel by viewModels()

    private val userViewModel: UserDataViewModel by viewModels()

    private lateinit var imgPath : String

    private var dialogBrandImage: ImageView? = null
    private var dialogBrandImageDefault: LinearLayout? = null
    private var selectedImageUri: Uri? = null


    private lateinit var gridView: GridView

    private var brandData : ArrayList<BrandData> = ArrayList()
    private var filterBrandData : ArrayList<BrandData> = ArrayList()
//    private lateinit var gridViewAdapter: BrandGridViewAdapter

    private lateinit var adapter: BrandRecyclerViewAdapter

    private var photoURI: Uri? = null;
    private lateinit var bitmapdata : Bitmap
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

//    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
//
//        if (result.resultCode == Activity.RESULT_OK) {
//            dialogBrandImage?.let {
//                Glide.with(this@MainActivity)
//                    .load(photoURI)
//                    .placeholder(R.drawable.ic_baseline_photo_library_24) // Placeholder image while loading
//                    .error(R.drawable.error_img)
//                    .transition(DrawableTransitionOptions.withCrossFade()) // Fade transition
//                    .into(it)
//            }
//
//            lifecycleScope.launch(Dispatchers.IO) {
//                bitmapdata = Bitmap.createScaledBitmap(MediaStore.Images.Media.getBitmap(contentResolver, Uri.parse(
//                    photoURI.toString()
//                )), 400, 400, true)
//
//                val compressedFile = ImageUtils.instance.bitmapToFile(this@MainActivity, bitmapdata)
//                imgPath = compressedFile?.absolutePath.toString()
//
//            }
//
//        }
//    }

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
//            lifecycleScope.launch(Dispatchers.IO) {
//                bitmapdata = MediaStore.Images.Media.getBitmap(
//                    contentResolver, it.toString().toUri()
//                ).scale(400, 400)
//
//                val compressedFile = ImageUtils.instance.bitmapToFile(this@MainActivity, bitmapdata)
//                imgPath = compressedFile?.absolutePath.toString()
//
//                withContext(Dispatchers.Main) {
//
//                    dialogBrandImage?.let {
//                        Glide.with(this@MainActivity)
//                            .load(bitmapdata)
//                            .placeholder(R.drawable.ic_baseline_photo_library_24) // Optional: placeholder while loading
//                            .error(R.drawable.error_img) // Optional: error image if loading fails
//                            .into(it)
//                    }
//
//                    dialogBrandImageDefault?.gone()
//
//                }
//
//            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(v.left, systemBars.top, v.right, systemBars.bottom)
            (v.layoutParams as ViewGroup.MarginLayoutParams).apply {
                topMargin = systemBars.top
                bottomMargin = systemBars.bottom
            }
            insets
        }

        val toolbar = binding.mainscreenToolbar.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Ganesh Seats"

        request.addListener(this)

        loader = getLoadingDialog(this)

        carDataViewModel.fetchBrands()

//        gridView = findViewById(R.id.grid_view)


        //  ******************************** Search Brands **************************************
        // Set its background to white
        val searchView = binding.searchBrand

// Background & underline
        val searchPlateId = searchView.context.resources.getIdentifier("android:id/search_plate", null, null)
        val searchPlate = searchView.findViewById<View>(searchPlateId)
        searchView.setBackgroundColor(Color.WHITE)
        searchPlate?.background = null

// Text & hint colors
        val searchTextId = searchView.context.resources.getIdentifier("android:id/search_src_text", null, null)
        val searchEditText = searchView.findViewById<EditText>(searchTextId)
        searchEditText?.setTextColor(Color.BLACK)
        searchEditText?.setHintTextColor(Color.DKGRAY)

        binding.searchBrand.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(p0: String?): Boolean {
                Log.d(TAG, "onQueryTextSubmit: ")
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                if(brandData.isNotEmpty()){
                    filterBrandData.clear()
                    for (data in brandData) {
                        if (query?.let { Pattern.quote(it) }?.let {
                                Pattern.compile(it, Pattern.CASE_INSENSITIVE)
                                    .matcher(data.brandName).find()
                            } == true
                        ) {
                            filterBrandData.add(data)
                        }
                    }

                    adapter.notifyDataSetChanged()
                }

                return false
            }
        })
//  ******************************** Search Brands **************************************


        binding.brandRecyclerView.layoutManager = GridLayoutManager(this, 2)
        // Initialize the adapter with an empty list and set it to the RecyclerView
        adapter = BrandRecyclerViewAdapter(filterBrandData, this,
            onItemClick = { data ->
                Log.d(TAG, "onCreate: selected brand detail is ${data.brandName}")
                masterViewModel.setBrandData(data)
                val intent = Intent(this, CarVariantActivity::class.java)
                startActivity(intent)
            },
            onItemEditClick = {data ->
                addBrandAlert(data)
            }
        )
        binding.brandRecyclerView.adapter = adapter

//        gridViewAdapter = BrandGridViewAdapter(this, filterBrandData,
//            onItemClick = { data ->
//                Log.d(TAG, "onCreate: selected brand detail is ${data.brandName}")
//                masterViewModel.setBrandData(data)
//                masterViewModel.setBrandId(data.brandId)
//                val intent = Intent(this, CarVariantActivity::class.java)
//                startActivity(intent)
//            },
//            onItemSettingClick = {itemView, data ->
////                showPopupMenu(itemView, data)
//                addBrandAlert(data)
//            }
//        )
//        gridView.adapter = gridViewAdapter


        fetchBrandListener()

        binding.addBrandBtn.setOnClickListener {
            addBrandAlert()
        }

        updateBrandListener()

        addBrandListener()


        // Pull-to-refresh listener
        binding.swipeRefreshLayout.setOnRefreshListener {
            // Call your refresh function here
            refreshData()
        }

        binding.swipeRefreshLayout.setColorSchemeColors(
            ContextCompat.getColor(this, R.color.black),
            ContextCompat.getColor(this, R.color.black),
            ContextCompat.getColor(this, R.color.black)
        )

    }

//    override fun onResume() {
//        super.onResume()
//        fetchBrandListener()
//    }

    private fun refreshData() {
        // Simulate network call or database fetch
        Handler(Looper.getMainLooper()).postDelayed({
            // Update your data

            carDataViewModel.fetchBrands()

            // Stop the refreshing animation
            binding.swipeRefreshLayout.isRefreshing = false
        }, 1000) // 2 seconds delay
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                showLogoutDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showLogoutDialog() {
        var dialogBuilder = AlertDialog.Builder(this@MainActivity)
        val layoutView: View = layoutInflater.inflate(R.layout.confirmation_alertdialog, null)

        dialogBuilder.setCancelable(false)

        val cancel_btn : MaterialButton = layoutView.findViewById(R.id.operation_cancel_btn)
        val submit_btn : MaterialButton = layoutView.findViewById(R.id.operation_done_btn)

        val title : TextView = layoutView.findViewById(R.id.alert_msg)

        val animIcon : com.airbnb.lottie.LottieAnimationView = layoutView.findViewById(R.id.alertdialog_anim_icon)

        animIcon.setAnimation(R.raw.logout)

        title.setText("Are you sure you want to logout?")

        submit_btn.setText("Yes")

        dialogBuilder.setView(layoutView)
        val alertDialog = dialogBuilder.create()
        alertDialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()

        submit_btn.setOnClickListener {
            userViewModel.logoutUser()
            startActivity(Intent(this, LoginActivity::class.java))
            finishAffinity()
        }

        cancel_btn.setOnClickListener {
            alertDialog.dismiss()
        }

    }

    @SuppressLint("MissingInflatedId")
    fun addBrandAlert(data: BrandData? = null){
        imgPath = ""
        var dialogBuilder = AlertDialog.Builder(this@MainActivity)
        val layoutView: View = layoutInflater.inflate(R.layout.add_brand_alertdialog, null)

        dialogBuilder.setCancelable(false)

        val cancel_btn : MaterialButton = layoutView.findViewById(R.id.brand_add_cancel_btn)
        val submit_btn : MaterialButton = layoutView.findViewById(R.id.brand_add_save_btn)

        val brandTextLayout : TextInputLayout = layoutView.findViewById(R.id.brandNameTextField)
        val brandText : TextInputEditText = layoutView.findViewById(R.id.brandName_text)

        val alertTitle : TextView = layoutView.findViewById(R.id.alert_title)

        val imgUploadLayout : RelativeLayout = layoutView.findViewById(R.id.imgUpload_layout)
        dialogBrandImage  = layoutView.findViewById(R.id.previewImg)
        dialogBrandImageDefault  = layoutView.findViewById(R.id.img_default)


        if(data != null){

            alertTitle.setText("Edit Brand Detail")

            brandText.setText(data.brandName)

            dialogBrandImageDefault?.gone()
            dialogBrandImage?.let {
                Glide.with(this)
                    .load(data.brandImage)
                    .placeholder(R.drawable.ic_baseline_photo_library_24)
                    .error(R.drawable.error_img)
                    .into(it)
            }

        }

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
            if(data != null){
                if(brandText.text.toString().isEmpty()){
                    showToast("Please enter Brand Name")
                }else{
                    if(imageFile != null){
                        carDataViewModel.updateBrands(
                            brandId = data?.brandId.toString(),
                            brandName = brandText.text.toString(),
                            brandImage = imageFile
                        )
                    }else{
                        carDataViewModel.updateBrands(
                            brandId = data?.brandId.toString(),
                            brandName = brandText.text.toString(),
                            brandImage = null
                        )
                    }

                }
            }else{
                if(brandText.text.toString().isEmpty()){
                    showToast("Please enter Brand Name")
                }else{
                    if(imageFile != null){
                        carDataViewModel.addBrands(
                            brandName = brandText.text.toString(),
                            brandImage = imageFile
                        )
                    }else{
                        carDataViewModel.addBrands(
                            brandName = brandText.text.toString(),
                            brandImage = null
                        )
                    }

                }
            }


            alertDialog.dismiss()
        }

    }

    @SuppressLint("MissingInflatedId")
    fun confirmOperationAlert(mode: String = "delete"){
        var dialogBuilder = AlertDialog.Builder(this@MainActivity)
        val layoutView: View = layoutInflater.inflate(R.layout.confirmation_alertdialog, null)

        dialogBuilder.setCancelable(false)

        val cancel_btn : MaterialButton = layoutView.findViewById(R.id.operation_cancel_btn)
        val submit_btn : MaterialButton = layoutView.findViewById(R.id.operation_done_btn)

        val animIcon : com.airbnb.lottie.LottieAnimationView = layoutView.findViewById(R.id.alertdialog_anim_icon)

        if(mode == "delete"){
            animIcon.setAnimation(R.raw.delete)
        }

        dialogBuilder.setView(layoutView)
        val alertDialog = dialogBuilder.create()
        alertDialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()

        cancel_btn.setOnClickListener {
            alertDialog.dismiss()
        }

    }

    private fun fetchBrandListener() {
        carDataViewModel.fetchBrandResponseLiveData.observe(this, Observer {
            when (it) {
                is NetworkResult.Success -> {
                    hideLoader(this, loader)

                    val res = it.data?.brandResponse

                    val brandDataArray = it.data?.brandResponse?.brandData ?: ArrayList()

                    brandData.clear()
                    filterBrandData.clear()



                    brandData.addAll(brandDataArray)
                    filterBrandData.addAll(brandDataArray)

                    masterViewModel.setBrandDetailData(brandData)

                    supportActionBar?.title = String.format("Ganesh Seats  (%s)", res?.totalStock)

                    if(brandData.isEmpty()){
                        binding.dataNotFoundLayout.root.visible()
                    }else{
                        binding.dataNotFoundLayout.root.gone()
                    }

                    adapter.notifyDataSetChanged()

                }
                is NetworkResult.Error -> {
                    Log.d(TAG, "fetchBrandListener: error data => $it")
                    hideLoader(this, loader)
                    alertDialogService.alertDialogAnim(this, it.message.toString(), R.raw.failed)

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

    private fun updateBrandListener() {
        carDataViewModel.updateBrandResponseLiveData.observe(this, Observer {res ->
            when (res) {
                is NetworkResult.Success -> {


                    val updatedData = res.data?.brandData

                    imageFile = null

                    if(updatedData != null){
                        val index = brandData.indexOfFirst { it.brandId == updatedData.brandId }

                       val newData = BrandData(
                            brandId =  updatedData.brandId,
                            brandName = updatedData.brandName,
                            brandStatus = updatedData.status,
                            brandImage = updatedData.brandImage
                        )

//                        if (index != -1) {
//                            gridViewAdapter.remove(gridViewAdapter.getItem(index))
//                            gridViewAdapter.insert(newData, index)
//
//                        }else{
//                            brandData.add(newData)
//                            gridViewAdapter.add(newData)
//                        }

                        if (index != -1) {
                            brandData[index] = newData
                            filterBrandData[index] = newData
                            adapter.notifyItemChanged(index)
                        } else {
                            brandData.add(newData)
                            filterBrandData.add(newData)
                            adapter.notifyItemInserted(filterBrandData.size - 1)
                        }
                    }


                    if(brandData.isEmpty()){
                        binding.dataNotFoundLayout.root.visible()
                    }else{
                        binding.dataNotFoundLayout.root.gone()
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

    private fun addBrandListener() {
        carDataViewModel.addBrandResponseLiveData.observe(this, Observer {res ->
            when (res) {
                is NetworkResult.Success -> {


                    val updatedData = res.data?.brandData

                    imageFile = null

//                    if(updatedData != null){
                        val newBrandData = BrandData(
                            brandId =  updatedData?.brandId,
                            brandName = updatedData?.brandName,
                            brandStatus = updatedData?.status,
                            brandImage = updatedData?.brandImage
                        )
                        brandData.add(newBrandData)
                        filterBrandData.add(newBrandData)
                        adapter.notifyDataSetChanged()
//                        gridViewAdapter.add(newBrandData)
//                    }


                    if(brandData.isEmpty()){
                        binding.dataNotFoundLayout.root.visible()
                    }else{
                        binding.dataNotFoundLayout.root.gone()
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

//    private fun createImageFile(): File? {
//        val directory = File(filesDir, "GaneshSeats")
//
//        if (!directory.exists()) {
//            val isDirectoryCreated = directory.mkdirs()
//
//            if (isDirectoryCreated) {
//                val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
//                val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
//
//                return File.createTempFile(
//                    "JPEG_${timeStamp}_", /* prefix */
//                    ".jpg", /* suffix */
//                    directory /* directory */
//                ).apply {
//                    // Save a file: path for use with ACTION_VIEW intents
//                    currentPhotoPath = absolutePath
//                }
//            } else{
//               showToast("something went wrong")
//                return null
//            }
//        } else {
//            Log.d(TAG, "createImageFile: enter that function for create file")
//            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
//            val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
//            Log.d(TAG, "createImageFile: storage dir => $storageDir ")
//            return File.createTempFile(
//                "JPEG_${timeStamp}_", /* prefix */
//                ".jpg", /* suffix */
//                directory /* directory */
//            ).apply {
//                // Save a file: path for use with ACTION_VIEW intents
//                currentPhotoPath = absolutePath
//            }
//        }
//
//    }


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

//        Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//            .also { takePictureIntent ->
//                if (takePictureIntent.resolveActivity(packageManager) != null) {
//                    // Ensure that there's a camera activity to handle the intent
//                    takePictureIntent.resolveActivity(packageManager)?.also {
//                        val photoFile: File? = try {
//                            createImageFile()
//                        } catch (ex: IOException) {
//                            // Error occurred while creating the File
//
//                            null
//                        }
//                        // Continue only if the File was successfully created
//                        photoFile?.also {
//                            photoURI = FileProvider.getUriForFile(
//                                this,
//                                "${packageName}.provider",
//                                it
//                            )
//                            takePictureIntent.putExtra(
//                                MediaStore.EXTRA_OUTPUT,
//                                photoURI
//                            )
//                            cameraLauncher.launch(takePictureIntent)
//                        }
//                    }
//                } else {
//                    try {
//                        val photoFile: File? = try {
//                            createImageFile()
//                        } catch (ex: IOException) {
//                            null
//                        }
//
//                        photoFile?.also {
//                            photoURI = FileProvider.getUriForFile(
//                                this,
//                                "${packageName}.provider",
//                                it
//                            )
//                            takePictureIntent.putExtra(
//                                MediaStore.EXTRA_OUTPUT,
//                                photoURI
//                            )
////                                            Log.d(TAG, "onCreate: take pic at that point")
//                            cameraLauncher.launch(takePictureIntent)
//                        }
//                    } catch (e: ActivityNotFoundException) {
//                        alertDialogService.alertDialogAnim(
//                            this,
//                            "something went wrong with profile",
//                            R.raw.failed
//                        )
////                            Toast.makeText(myContext, "something went wrong", Toast.LENGTH_SHORT).show()
//                    }
//                }
//            }
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

            imageFile = ImageUtils().compressImageTo2MB(this@MainActivity, uri)

            withContext(Dispatchers.Main) {

                dialogBrandImage?.let { imageView ->
                    Glide.with(this@MainActivity)
                        .load(uri) // 👈 Direct URI
                        .placeholder(R.drawable.error_img)
                        .error(R.drawable.error_img)
                        .into(imageView)
                }

                dialogBrandImageDefault?.gone()

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

//    private fun uriToFile(uri: Uri): File {
//        val inputStream = contentResolver.openInputStream(uri)
//        val file = File(cacheDir, "upload_${System.currentTimeMillis()}.jpg")
//
//        val outputStream = FileOutputStream(file)
//        inputStream?.copyTo(outputStream)
//
//        inputStream?.close()
//        outputStream.close()
//
//        return file
//    }

//    ************************
//    private fun getBitmapFromUri(context: Context, uri: Uri): Bitmap {
//        val inputStream = context.contentResolver.openInputStream(uri)
//        return BitmapFactory.decodeStream(inputStream).also {
//            inputStream?.close()
//        }
//    }
//
//
//    private fun resizeBitmap(bitmap: Bitmap, maxSize: Int): Bitmap {
//        var width = bitmap.width
//        var height = bitmap.height
//
//        val ratio = width.toFloat() / height.toFloat()
//
//        if (ratio > 1) {
//            width = maxSize
//            height = (width / ratio).toInt()
//        } else {
//            height = maxSize
//            width = (height * ratio).toInt()
//        }
//
//        return Bitmap.createScaledBitmap(bitmap, width, height, true)
//    }
//
//
//    private fun compressImageTo2MB(context: Context, uri: Uri): File {
//        val originalBitmap = getBitmapFromUri(context, uri)
//
//        // 👇 Resize to max 1920px (camera safe size)
//        val resizedBitmap = resizeBitmap(originalBitmap, 1920)
//
//        var quality = 100
//        val maxSize = 2 * 1024 * 1024 // 2MB
//        lateinit var file: File
//
//        do {
//            val baos = ByteArrayOutputStream()
//            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos)
//
//            file = File(context.cacheDir, "img_${System.currentTimeMillis()}.jpg")
//            FileOutputStream(file).use {
//                it.write(baos.toByteArray())
//            }
//
//            quality -= 5
//        } while (file.length() > maxSize && quality > 20)
//
//        return file
//    }




//    fun chooseImage(){
//        val bottomSheetDialog = BottomSheetDialog(this)
//        bottomSheetDialog.setContentView(R.layout.selectmedia_dialog)
//
//        val cameraBtn =
//            bottomSheetDialog.findViewById<ImageButton>(R.id.camera_circle);
//        val galleryBtn =
//            bottomSheetDialog.findViewById<ImageButton>(R.id.gallery_circle);
//
//        bottomSheetDialog.show()
//
//        cameraBtn?.setOnClickListener {
//            bottomSheetDialog.dismiss()
//            Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//                .also { takePictureIntent ->
//                    if (takePictureIntent.resolveActivity(packageManager) != null) {
//                        // Ensure that there's a camera activity to handle the intent
//                        takePictureIntent.resolveActivity(packageManager)?.also {
//                            val photoFile: File? = try {
//                                createImageFile()
//                            } catch (ex: IOException) {
//                                // Error occurred while creating the File
//
//                                null
//                            }
//                            // Continue only if the File was successfully created
//                            photoFile?.also {
//                                photoURI = FileProvider.getUriForFile(
//                                    this,
//                                    "${packageName}.provider",
//                                    it
//                                )
//                                takePictureIntent.putExtra(
//                                    MediaStore.EXTRA_OUTPUT,
//                                    photoURI
//                                )
//                                cameraLauncher.launch(takePictureIntent)
//                            }
//                        }
//                    } else {
//                        try {
//                            val photoFile: File? = try {
//                                createImageFile()
//                            } catch (ex: IOException) {
//                                null
//                            }
//
//                            photoFile?.also {
//                                photoURI = FileProvider.getUriForFile(
//                                    this,
//                                    "${packageName}.provider",
//                                    it
//                                )
//                                takePictureIntent.putExtra(
//                                    MediaStore.EXTRA_OUTPUT,
//                                    photoURI
//                                )
////                                            Log.d(TAG, "onCreate: take pic at that point")
//                                cameraLauncher.launch(takePictureIntent)
//                            }
//                        } catch (e: ActivityNotFoundException) {
//                            alertDialogService.alertDialogAnim(
//                                this,
//                                "something went wrong with profile",
//                                R.raw.failed
//                            )
////                            Toast.makeText(myContext, "something went wrong", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                }
//
//        }
//
//        galleryBtn?.setOnClickListener {
//            bottomSheetDialog.dismiss()
//            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
//        }
//    }

    override fun onPause() {
        super.onPause()
        mediaBottomSheet?.dismiss()
        mediaBottomSheet = null
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
}
