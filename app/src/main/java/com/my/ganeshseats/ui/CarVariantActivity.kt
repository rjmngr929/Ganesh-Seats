package com.my.ganeshseats.ui

import android.os.Bundle
import android.view.MenuItem
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.my.ganeshseats.R
import com.my.ganeshseats.databinding.ActivityCarVariantBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CarVariantActivity : AppCompatActivity() {

    companion object{
        private const val TAG = "Car Variant Activity"
    }

    private lateinit var binding: ActivityCarVariantBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCarVariantBinding.inflate(layoutInflater)
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

        val toolbar = binding.carModelsToolbar.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.outline_arrow_back_24)


        supportActionBar?.title = "Car Models"

//        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_carmodel_navigation) as NavHostFragment
//        val navController = navHostFragment.navController
//
////        val appBarConfiguration = AppBarConfiguration(navController.graph)
////        setupActionBarWithNavController(navController, appBarConfiguration)

    }

//    override fun onSupportNavigateUp(): Boolean {
//        val navController = findNavController(R.id.nav_host_fragment_activity_carmodel_navigation)
//        return navController.navigateUp() || super.onSupportNavigateUp()
//    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                super.onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}