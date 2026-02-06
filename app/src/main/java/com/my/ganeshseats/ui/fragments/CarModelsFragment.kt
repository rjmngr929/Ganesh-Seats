package com.my.ganeshseats.ui.fragments

import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
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
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.SearchView
import android.widget.TextView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.graphics.scale
import androidx.core.net.toUri
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
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
import com.my.ganeshseats.adapters.BrandSelectRecyclerView
import com.my.ganeshseats.adapters.CarModelsRecyclerviewAdapter
import com.my.ganeshseats.data.response.BrandData
import com.my.ganeshseats.data.response.CarDetailModel
import com.my.ganeshseats.data.response.CategoryDataModel
import com.my.ganeshseats.databinding.FragmentCarModelsBinding
import com.my.ganeshseats.ui.MainActivity
import com.my.ganeshseats.ui.viewmodel.CarModelViewModel
import com.my.ganeshseats.ui.viewmodel.MasterViewModel
import com.my.ganeshseats.ui.viewmodel.UserDataViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.internal.Contexts
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.regex.Pattern
import javax.inject.Inject
import kotlin.getValue


@AndroidEntryPoint
class CarModelsFragment : Fragment(), PermissionRequest.Listener {

    companion object{
        private const val TAG = "Car Models Fragment"
    }


    private lateinit var binding: FragmentCarModelsBinding

    private var mediaBottomSheet: BottomSheetDialog? = null

    private lateinit var myContext: Context

    private lateinit var adapter: CarModelsRecyclerviewAdapter


    private lateinit var brandAdapter: BrandSelectRecyclerView
    private var brandData : ArrayList<BrandData> = ArrayList()

//    private var listData : ArrayList<CarDetailModel> = ArrayList()

    private var modelData : ArrayList<CarDetailModel> = ArrayList()
    private var filterModelData : ArrayList<CarDetailModel> = ArrayList()

    private lateinit var imgPath : String

    private var dialogModelImage: ImageView? = null
    private var dialogModelImageDefault: LinearLayout? = null

    private val carDataViewModel: CarModelViewModel by activityViewModels ()

    private val masterViewModel: MasterViewModel by activityViewModels ()

    @Inject
    lateinit var alertDialogService: AlertDialogUtility


    private lateinit var loader: AlertDialog

    private var photoURI: Uri? = null;
    private lateinit var bitmapdata : Bitmap
    private lateinit var resized : Bitmap
    private lateinit var currentPhotoPath: String

    private var brandId: Int = 0
    private var brandTitle: String = "Models"


    private var totalStock: String = ""

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


    private var cameraImageUri: Uri? = null
    private var imageFile: File? = null

    private val cameraLaunch =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success && cameraImageUri != null) {
                uploadImage(cameraImageUri!!)
            }
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
//            dialogModelImage?.let {
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
//                    dialogModelImage?.let {
//                        Glide.with(myContext)
//                            .load(bitmapdata)
//                            .placeholder(R.drawable.ic_baseline_photo_library_24) // Optional: placeholder while loading
//                            .error(R.drawable.error_img) // Optional: error image if loading fails
//                            .into(it)
//                    }
//
//                    dialogModelImageDefault?.gone()
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
        binding = FragmentCarModelsBinding.inflate(inflater, container, false)

        loader = getLoadingDialog(myContext)

        request.addListener(this)

        masterViewModel.selectedBrandData.observe(viewLifecycleOwner, Observer{data->
            Log.d(TAG, "onCreateView: brand data = > ${data?.brandName} and $")
            brandId = data?.brandId ?: 0
            brandTitle = data?.brandName.toString()
            if(modelData.isEmpty())
                carDataViewModel.fetchBrandModels(brandId.toString())
        })



        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        (requireActivity() as AppCompatActivity)
            .supportActionBar
            ?.title = if(totalStock.isEmpty()) brandTitle else String.format("%s   (%s)", brandTitle, totalStock)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        masterViewModel.brandDetailData.observe(viewLifecycleOwner) { data ->
            Log.d(TAG, "onViewCreated: brand data receiver => ${data.size}")
            brandData.clear()
            brandData.addAll(data)




        }

        //        **************************** Brand Select Data *************************************************
        binding.brandDisplayRecyclerview.layoutManager = LinearLayoutManager(context)
        // Initialize the adapter with an empty list and set it to the RecyclerView
        brandAdapter = BrandSelectRecyclerView(brandData, myContext,
            onItemClick = { data ->
//                Log.d(TAG, "onViewCreated: selected model value => ${data.brandName}")
                brandId = data.brandId ?: 0
                masterViewModel.setBrandData(data)
                brandTitle = data.brandName.toString()
                carDataViewModel.fetchBrandModels(data.brandId.toString())
            }
        )
        binding.brandDisplayRecyclerview.adapter = brandAdapter
//        **************************** Brand Select Data *************************************************




        binding.carModelsRecyclerView.layoutManager = LinearLayoutManager(context)
        // Initialize the adapter with an empty list and set it to the RecyclerView
        adapter = CarModelsRecyclerviewAdapter(filterModelData, myContext,
            onItemClick = { data ->
                val bundle = Bundle()
                bundle.putString("modelId", data.carModelId.toString())
                bundle.putString("modelName", data.modelName)
                bundle.putString("brandId", data.brandId.toString())
                bundle.putString("brandName", brandTitle)
                NavHostFragment.findNavController(this@CarModelsFragment).navigate(R.id.action_carModelsFragment_to_seatCoversFragment, args = bundle)
            },
            onItemEditClick = {data ->
                addBrandModelsAlert(data)
            }
        )
        binding.carModelsRecyclerView.adapter = adapter


        //  ******************************** Search Models **************************************
        // Set its background to white
        val searchView = binding.searchModels

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

        binding.searchModels.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(p0: String?): Boolean {
                Log.d(TAG, "onQueryTextSubmit: ")
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                if(modelData.isNotEmpty()){
                    filterModelData.clear()
                    for (data in modelData) {
                        if (query?.let { Pattern.quote(it) }?.let {
                                Pattern.compile(it, Pattern.CASE_INSENSITIVE)
                                    .matcher(data.modelName).find()
                            } == true
                        ) {
                            filterModelData.add(data)
                        }
                    }

                    adapter.notifyDataSetChanged()
                }

                return false
            }
        })
//  ******************************** Search Models **************************************

        binding.addBrandModelBtn.setOnClickListener {
            addBrandModelsAlert()
        }

        bindObservers()

        addModelBindObservers()

        updateModelBindObservers()

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

            if(brandId > 0) {
                carDataViewModel.fetchBrandModels(brandId.toString())
            }

            // Stop the refreshing animation
            binding.swipeRefreshLayout.isRefreshing = false
        }, 1000) // 2 seconds delay
    }

    private fun bindObservers() {
        carDataViewModel.fetchBrandModelResponseLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is NetworkResult.Success -> {

                    hideLoader(myContext, loader)

                    modelData.clear()
                    filterModelData.clear()

                    val res = it.data?.carResponse

                    val modelList = it.data?.carResponse?.carModelData ?: ArrayList()

                    modelData.addAll(modelList)
                    filterModelData.addAll(modelData)

                    totalStock = res?.totalStock.toString()

                    (requireActivity() as AppCompatActivity)
                        .supportActionBar
                        ?.title = String.format("%s   (%s)", brandTitle, res?.totalStock)

                    if(modelData.isEmpty()){
                        binding.dataNotFoundLayout.root.visible()
                    }else{
                        binding.dataNotFoundLayout.root.gone()
                    }

                    adapter.notifyDataSetChanged()

                    carDataViewModel.clearBrandModelRes()

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

    fun addBrandModelsAlert(data: CarDetailModel? = null){
        imgPath = ""
        var dialogBuilder = AlertDialog.Builder(myContext)
        val layoutView: View = layoutInflater.inflate(R.layout.add_brand_alertdialog, null)

        dialogBuilder.setCancelable(false)

        val cancel_btn : MaterialButton = layoutView.findViewById(R.id.brand_add_cancel_btn)
        val submit_btn : MaterialButton = layoutView.findViewById(R.id.brand_add_save_btn)

        val modelTextLayout : TextInputLayout = layoutView.findViewById(R.id.brandNameTextField)
        val modelText : TextInputEditText = layoutView.findViewById(R.id.brandName_text)

        val alertTitle : TextView = layoutView.findViewById(R.id.alert_title)

        val imgUploadLayout : RelativeLayout = layoutView.findViewById(R.id.imgUpload_layout)
        dialogModelImage  = layoutView.findViewById(R.id.previewImg)
        dialogModelImageDefault  = layoutView.findViewById(R.id.img_default)


        modelTextLayout.hint = "Car Model Name"

        if(data != null){

            alertTitle.setText("Edit Car Model Detail")

            modelText.setText(data.modelName)

            dialogModelImageDefault?.gone()
            dialogModelImage?.let {
                Glide.with(this)
                    .load(data.modelImage)
                    .placeholder(R.drawable.ic_baseline_photo_library_24)
                    .error(R.drawable.error_img)
                    .into(it)
            }

        }else{
            alertTitle.setText("Add Car Model")
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
                if(modelText.text.toString().isEmpty()){
                    myContext.showToast("Please enter Brand Model Name")
                }else{
                    if(imageFile != null ){
                        carDataViewModel.updateBrandModels(
                            brandId = data?.brandId.toString(),
                            modelId = data?.carModelId.toString(),
                            status = data?.carModelStatus.toString(),
                            modelName = modelText.text.toString(),
                            modelImage = imageFile
                        )
                    }else{
                        carDataViewModel.updateBrandModels(
                            brandId = data?.brandId.toString(),
                            modelId = data?.carModelId.toString(),
                            status = data?.carModelStatus.toString(),
                            modelName = modelText.text.toString(),
                            modelImage = null
                        )
                    }

                }
            }else{
                if(modelText.text.toString().isEmpty()){
                    myContext.showToast("Please enter Brand Model name")
                }else{
                    if(imageFile != null ){
                        carDataViewModel.addBrandModels(
                            brandId = brandId.toString(),
                            modelName = modelText.text.toString(),
                            modelImage = imageFile
                        )
                    }else{
                        carDataViewModel.addBrandModels(
                            brandId = brandId.toString(),
                            modelName = modelText.text.toString(),
                            modelImage = null
                        )
                    }

                }
            }


            alertDialog.dismiss()
        }

    }

    private fun addModelBindObservers() {
        carDataViewModel.addBrandModelResponseLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is NetworkResult.Success -> {

                    hideLoader(myContext, loader)

                    val modelList = it.data?.carModelData

                    imageFile = null

                    if(modelList != null){
                        val newData = CarDetailModel(
                            brandId = modelList.brandId?.toInt(),
                            carModelId = modelList.modelId,
                            modelName = modelList.modelName,
                            modelImage = modelList.modelImage,
                            seatCoverAvailable = modelList.totalSeatCovers.toString(),
                            carModelStatus = modelList.status

                        )

                        if (modelData.none { it.carModelId == newData.carModelId }) {
                            modelData.add(newData)
                            filterModelData.add(newData)
                            adapter.notifyDataSetChanged()
                        }




                    }


                    if(modelData.isEmpty()){
                        binding.dataNotFoundLayout.root.visible()
                    }else{
                        binding.dataNotFoundLayout.root.gone()
                    }

                    carDataViewModel.clearBrandModelRes()

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

    private fun updateModelBindObservers() {
        carDataViewModel.updateBrandModelResponseLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is NetworkResult.Success -> {

                    hideLoader(myContext, loader)

                    val modelList = it.data?.carModelData

                    imageFile = null

                    if(modelList != null){
                        val index = filterModelData.indexOfFirst { it.carModelId == modelList.modelId }

                        Log.d(TAG, "updateModelBindObservers: find data is ${modelList.totalSeatCovers}")

                        val newData = CarDetailModel(
                            brandId = modelList.brandId?.toInt(),
                            carModelId = modelList.modelId,
                            modelName = modelList.modelName,
                            modelImage = modelList.modelImage,
                            seatCoverAvailable = modelList.totalSeatCovers.toString(),
                            carModelStatus = modelList.status

                        )

                        if (index != -1) {
                            modelData[index] = newData
                            filterModelData[index] = newData
                            adapter.notifyItemChanged(index)
                        } else {
                            modelData.add(newData)
                            filterModelData.add(newData)
                            adapter.notifyItemInserted(filterModelData.size - 1)
                        }

                    }

                    if(modelData.isEmpty()){
                        binding.dataNotFoundLayout.root.visible()
                    }else{
                        binding.dataNotFoundLayout.root.gone()
                    }

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

//        Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//            .also { takePictureIntent ->
//                if (takePictureIntent.resolveActivity(myContext.packageManager) != null) {
//                    // Ensure that there's a camera activity to handle the intent
//                    takePictureIntent.resolveActivity(myContext.packageManager)?.also {
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
//                                myContext,
//                                "${myContext.packageName}.provider",
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
//                                myContext,
//                                "${myContext.packageName}.provider",
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
//                            myContext,
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

            imageFile = ImageUtils().compressImageTo2MB(myContext, uri)

            withContext(Dispatchers.Main) {

                dialogModelImage?.let { imageView ->
                    Glide.with(myContext)
                        .load(uri) // 👈 Direct URI
                        .placeholder(R.drawable.error_img)
                        .error(R.drawable.error_img)
                        .into(imageView)
                }

                dialogModelImageDefault?.gone()

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

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: call that on destroy")
        filterModelData.clear()
        modelData.clear()

        carDataViewModel.clearAddModelRes()
        carDataViewModel.clearUpdateModelRes()
        carDataViewModel.clearBrandModelRes()

    }

//    fun chooseImage(){
//        val bottomSheetDialog = BottomSheetDialog(myContext)
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
//                    if (takePictureIntent.resolveActivity(myContext.packageManager) != null) {
//                        // Ensure that there's a camera activity to handle the intent
//                        takePictureIntent.resolveActivity(myContext.packageManager)?.also {
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
//                                    myContext,
//                                    "${myContext.packageName}.provider",
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
//                                    myContext,
//                                    "${myContext.packageName}.provider",
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
//                                myContext,
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

}