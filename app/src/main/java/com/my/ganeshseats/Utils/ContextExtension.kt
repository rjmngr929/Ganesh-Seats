package com.my.ganeshseats.Utils

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.kotlinpermissions.PermissionStatus
import com.kotlinpermissions.request.PermissionRequest
import com.my.ganeshseats.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

internal fun Context.showToast(msg: String){
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun View.visible() {
    visibility = View.VISIBLE
}

//componentId.visible()
fun View.gone() {
    visibility = View.GONE
}

//componentId.invisible()
fun View.invisible() {
    visibility = View.INVISIBLE
}


fun View.setOnSingleClickListener(interval: Long = 1000, onClick: (View) -> Unit) {
    var lastClickTime = 0L
    setOnClickListener {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime > interval) {
            lastClickTime = currentTime
            onClick(it)
        }
    }
}

fun Float.round(decimals: Int): Float = "%.${decimals}f".format(this).toFloat()
fun Double.round(decimals: Int): Double = "%.${decimals}f".format(this).toDouble()


@SuppressLint("MissingInflatedId")
internal fun Context.showRationaleDialog(myContext: Context, permissions: List<PermissionStatus>, request: PermissionRequest, msg: String) {
    var dialogBuilder = android.app.AlertDialog.Builder(myContext)
    val layoutView: View = LayoutInflater.from(myContext).inflate(R.layout.permission_alert_dialog, null)

    val title : TextView = layoutView.findViewById(R.id.alertdialog_permission_title)
    val message : TextView = layoutView.findViewById(R.id.alertdialog_permission_msg)
    val confirmBtn : MaterialButton = layoutView.findViewById(R.id.alertdialog_permission_confirm_btn)
    val cancelBtn : MaterialButton = layoutView.findViewById(R.id.alertdialog_permission_cancel_btn)

    dialogBuilder.setView(layoutView)
    val alertDialog = dialogBuilder.create()
    alertDialog.setCancelable(false)
    alertDialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
    alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    alertDialog.show()

    title.text = resources.getString( R.string.permissions_required)
    message.text = msg

    cancelBtn.setOnClickListener {
        alertDialog.dismiss()
    }

    confirmBtn.setOnClickListener {
        request.send()
        alertDialog.dismiss()
    }
}

@SuppressLint("MissingInflatedId")
internal fun Context.showPermanentlyDeniedDialog(myContext: Context, permissions: List<PermissionStatus>, msg: String, onButtonClick: () -> Unit = {}) {
    var dialogBuilder = android.app.AlertDialog.Builder(myContext)
    val layoutView: View = LayoutInflater.from(myContext).inflate(R.layout.permission_alert_dialog, null)

    val title : TextView = layoutView.findViewById(R.id.alertdialog_permission_title)
    val message : TextView = layoutView.findViewById(R.id.alertdialog_permission_msg)
    val confirmBtn : MaterialButton = layoutView.findViewById(R.id.alertdialog_permission_confirm_btn)
    val cancelBtn : MaterialButton = layoutView.findViewById(R.id.alertdialog_permission_cancel_btn)

    dialogBuilder.setView(layoutView)
    val alertDialog = dialogBuilder.create()
    alertDialog.setCancelable(false)
    alertDialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
    alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    alertDialog.show()

    title.text = resources.getString( R.string.permissions_required)
    message.text = msg

    cancelBtn.setOnClickListener {
        alertDialog.dismiss()
    }

    confirmBtn.setOnClickListener {
        onButtonClick()
        alertDialog.dismiss()
    }

}

// Activity version
fun AppCompatActivity.showBottomSheetSafely(
    tag: String,
    createDialog: () -> BottomSheetDialogFragment
) {
    // Pahle check karo agar already show ho raha hai toh
    if (supportFragmentManager.findFragmentByTag(tag) != null) return

    if (!lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
        // Agar app foreground mein nahi hai toh launch a coroutine wait karne ke liye
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                // Ek baar phir se check karo ki fragment already exist nahi karta
                if (supportFragmentManager.findFragmentByTag(tag) == null) {
                    createDialog().show(supportFragmentManager, tag)
                }
            }
        }
    } else {
        // Agar app already foreground (RESUMED) mein hai, toh turant show karo
        createDialog().show(supportFragmentManager, tag)
    }
}

// Fragment version (agar Fragment ke andar use kar rahe ho)
fun Fragment.showBottomSheetSafely(
    tag: String,
    createDialog: () -> BottomSheetDialogFragment
) {
    if (childFragmentManager.findFragmentByTag(tag) != null) return

    if (!lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                if (childFragmentManager.findFragmentByTag(tag) == null) {
                    createDialog().show(childFragmentManager, tag)
                }
            }
        }
    } else {
        createDialog().show(childFragmentManager, tag)
    }
}

fun FragmentActivity.replaceBottomSheetLifecycleSafe(
    currentTag: String,
    newTag: String,
    createNewDialog: () -> BottomSheetDialogFragment
) {
    lifecycleScope.launch {
        val currentSheet = supportFragmentManager.findFragmentByTag(currentTag) as? BottomSheetDialogFragment
        currentSheet?.dismissAllowingStateLoss()

        // Wait for dialog to be dismissed
        delay(200)

        lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            if (supportFragmentManager.findFragmentByTag(newTag) == null) {
                createNewDialog().show(supportFragmentManager, newTag)
            }
        }
    }
}

//fun View.showSnack(message: String, action: String = "", actionListener: () -> Unit = {}): Snackbar {
//    var snackbar = Snackbar.make(this, message, Snackbar.LENGTH_SHORT)
//    if (action != "") {
//        snackbar.duration = Snackbar.LENGTH_INDEFINITE
//        snackbar.setAction(action) {
//            actionListener()
//            snackbar.dismiss()
//        }
//    }
//    snackbar.show()
//    return snackbar
//}

fun String.capitalizeEachWord(): String =
    this.trim().lowercase()
        .split(" ")
        .joinToString(" ") { it.replaceFirstChar { c -> c.titlecase() }}

fun View.showSnack(message: String, textColor: Int = 0, bgColor: Int = 0, action: String = "",  actionListener: () -> Unit = {}): Snackbar {
    var snackbar = Snackbar.make(this, message, Snackbar.LENGTH_SHORT)
    // Set background color
//    snackbar.view.setBackgroundColor()
    if(textColor != 0)
        snackbar.setTextColor(ContextCompat.getColor(this.context, textColor))

    if(bgColor != 0){
        snackbar.setBackgroundTint(ContextCompat.getColor(this.context, bgColor))
    }

    if (action != "") {
        snackbar.duration = Snackbar.LENGTH_INDEFINITE

        snackbar.setAction(action) {
            actionListener()
            snackbar.dismiss()
        }
    }
    snackbar.show()
    return snackbar
}

fun String.capitalizeFirstLetter(): String {
    return if (isNotEmpty()) {
        this[0].uppercaseChar() + substring(1)
    } else {
        this
    }
}

fun String.isValidEmail(): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.isValidMobile(): Boolean {
    return Patterns.PHONE.matcher(this).matches()
}

fun String.isAlphabetWithSpace(): Boolean {
//    return this.matches(Regex("^[a-zA-Z\\s]+$"))
    return this.matches(Regex("^[a-zA-Z\\s]{3,}$"))
}

fun String.isValidPan(): Boolean {
    return this.matches(Regex("^[A-Za-z]{5}[0-9]{4}[A-Za-z]$"))
}

fun String.isValidAadhaar(): Boolean {
    return this.matches(Regex("^[2-9]{1}[0-9]{11}$"))
}

class SingleLiveEvent<T> : MutableLiveData<T>() {
    private val pending = AtomicBoolean(false)

    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        super.observe(owner) { t ->
            if (pending.compareAndSet(true, false)) {
                observer.onChanged(t)
            }
        }
    }

    override fun setValue(value: T?) {
        pending.set(true)
        super.setValue(value)
    }
}


inline fun <T> LiveData<T>.observeOnce(
    lifecycleOwner: LifecycleOwner,
    crossinline onChanged: (T) -> Unit
) {
    val observer = object : Observer<T> {
        override fun onChanged(t: T) {
            onChanged(t)
            removeObserver(this)  // Remove after one call
        }
    }
    observe(lifecycleOwner, observer)
}



//Animations
fun View.toptobottomAnimation(myContext: Context) {
    return startAnimation(AnimationUtils.loadAnimation(myContext , R.anim.ttb))
}

fun View.zoomOutAnimation(myContext: Context) {
    return startAnimation(AnimationUtils.loadAnimation(myContext , R.anim.stb))
}

fun View.bottomtotop_type_oneAnimation(myContext: Context) {
    return startAnimation(AnimationUtils.loadAnimation(myContext , R.anim.btt))
}

fun View.bottomtotop_type_twoAnimation(myContext: Context) {
    return startAnimation(AnimationUtils.loadAnimation(myContext , R.anim.btt2))
}


//**************************** Loader ************************************************************
//Loader
@SuppressLint("MissingInflatedId")
fun getLoadingDialog(myContext: Context, rawFile: Int = R.raw.loader): androidx.appcompat.app.AlertDialog {
    val alertDialogBuilder = androidx.appcompat.app.AlertDialog.Builder(myContext, R.style.MyDialogStyle_transparent)

    val layoutView: View = LayoutInflater.from(myContext).inflate(R.layout.layout_loading_alert_dialog, null)

    val icon : LottieAnimationView = layoutView.findViewById(R.id.alertdialog_anim_icon_loader)

    icon.setAnimation(rawFile)

    alertDialogBuilder.setView(layoutView)
    alertDialogBuilder.setCancelable(false)

    return alertDialogBuilder.create()
}

//show Loader
fun showLoader(myContext: Context, loader: androidx.appcompat.app.AlertDialog){
    loader.show()
}

//hide loader
fun hideLoader(myContext: Context, loader: androidx.appcompat.app.AlertDialog){
    if (loader.isShowing)
        loader.dismiss()
}
//**************************** Loader ************************************************************
