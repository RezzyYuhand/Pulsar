package com.example.pulsar

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.pulsar.databinding.ActivityMainBinding
import com.polar.sdk.api.PolarBleApi
import com.polar.sdk.api.PolarBleApiDefaultImpl
import com.polar.sdk.api.errors.PolarInvalidArgument

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "Polar_MainActivity"
        private const val DEVICE_ID = "polar_device_id"
        private const val PERMISSION_REQUEST_CODE = 1
    }

    private lateinit var sharedPreferences: SharedPreferences
    private val bluetoothOnActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode != Activity.RESULT_OK) {
            Log.w(TAG, "Bluetooth off")
        }
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var api: PolarBleApi

    private var deviceId: String? = null

    private fun connectDevice(deviceID: String) {
        api = PolarBleApiDefaultImpl.defaultImplementation(
            applicationContext,
            setOf(
                PolarBleApi.PolarBleSdkFeature.FEATURE_POLAR_ONLINE_STREAMING,
                PolarBleApi.PolarBleSdkFeature.FEATURE_BATTERY_INFO,
                PolarBleApi.PolarBleSdkFeature.FEATURE_DEVICE_INFO
            )
        )

        try {
            api.connectToDevice(deviceID)
        } catch (a: PolarInvalidArgument) {
            Toast.makeText(this, "Device ID is invalid", Toast.LENGTH_SHORT).show()
            a.printStackTrace()
        }
    }

    private val sharedPreferencesListener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            if (key == DEVICE_ID) {
                val newDeviceId = sharedPreferences.getString(DEVICE_ID, null)
                newDeviceId?.let {
                    // Panggil connectToDevice dengan deviceId yang baru
                    connectDevice(it)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread.sleep(2000)

        supportActionBar?.hide()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkBT()
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferencesListener)
        deviceId = sharedPreferences.getString(DEVICE_ID, "") ?: throw Exception("ECGActivity couldn't be created, no deviceId given")

        if (deviceId.isNullOrEmpty()) {
            // Lakukan tindakan jika deviceId belum ada atau tidak valid
            // Contoh: Tampilkan pesan kepada pengguna
            Toast.makeText(this, "Device ID is invalid", Toast.LENGTH_SHORT).show()
        } else {
            // Panggil fungsi connectToDevice dengan deviceId yang sudah ada
            connectDevice(deviceId!!)
        }

        val navView: BottomNavigationView = binding.navView


        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    private fun checkBT() {
        val btManager = applicationContext.getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter: BluetoothAdapter? = btManager.adapter
        if (bluetoothAdapter == null) {
            showToast("Device doesn't support Bluetooth")
            return
        }

        if (!bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            bluetoothOnActivityResultLauncher.launch(enableBtIntent)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                requestPermissions(arrayOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT), PERMISSION_REQUEST_CODE)
            } else {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_CODE)
            }
        } else {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), PERMISSION_REQUEST_CODE)
        }
    }

    private fun showToast(message: String) {
        val toast = Toast.makeText(applicationContext, message, Toast.LENGTH_LONG)
        toast.show()
    }
}