package com.example.pulsar.ui.dashboard

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.pulsar.MainActivity
import com.example.pulsar.MainViewModel
import com.example.pulsar.databinding.FragmentDashboardBinding
import com.polar.sdk.api.PolarBleApi
import com.polar.sdk.api.PolarBleApiCallback
import com.polar.sdk.api.PolarBleApiDefaultImpl
import com.polar.sdk.api.errors.PolarInvalidArgument
import com.polar.sdk.api.model.PolarDeviceInfo
import com.polar.sdk.api.model.PolarHrData
import java.util.UUID

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var api: PolarBleApi

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textDashboard
        dashboardViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        val deviceidinput: EditText = binding.inputDeviceid

        val textdevice: TextView = binding.tvDeviceId
        val textViewBattery: TextView = binding.deviceBatteryTv
        val textViewFwVersion: TextView = binding.firmwareTv
        val disconnect: Button = binding.btDisconnect
        disconnect.setOnClickListener {
            val id = deviceidinput.text.toString().uppercase()
            if (id.isEmpty()){
                Toast.makeText(requireContext(), "No Device Connected", Toast.LENGTH_SHORT).show()
            } else {
                api.disconnectFromDevice(id)
            }
        }

        val connectbt: Button = binding.btConnect
        connectbt.setOnClickListener {
            val id = deviceidinput.text.toString().uppercase()
            if (id.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter a valid ID", Toast.LENGTH_SHORT).show()
            } else {
                val mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
                mainViewModel.id.value = id

                textdevice.text = "Device ID :$id"
                api = PolarBleApiDefaultImpl.defaultImplementation(
                    requireContext().applicationContext,
                    setOf(
                        PolarBleApi.PolarBleSdkFeature.FEATURE_POLAR_ONLINE_STREAMING,
                        PolarBleApi.PolarBleSdkFeature.FEATURE_BATTERY_INFO,
                        PolarBleApi.PolarBleSdkFeature.FEATURE_DEVICE_INFO
                    )
                )
                api.setApiCallback(object : PolarBleApiCallback() {
                    override fun blePowerStateChanged(powered: Boolean) {
                        Log.d("BTCHANGED", "BluetoothStateChanged $powered")
                    }

                    override fun deviceConnected(polarDeviceInfo: PolarDeviceInfo) {
                        Log.d("DEVICE CONNECTED", "Device connected " + polarDeviceInfo.deviceId)
                        Toast.makeText(requireContext(), "Connected!", Toast.LENGTH_SHORT).show()
                    }

                    override fun deviceConnecting(polarDeviceInfo: PolarDeviceInfo) {
                        Log.d("DVC CONNECTING", "Device connecting ${polarDeviceInfo.deviceId}")
                    }

                    override fun deviceDisconnected(polarDeviceInfo: PolarDeviceInfo) {
                        Log.d("DVC DISCONNECTED", "Device disconnected ${polarDeviceInfo.deviceId}")
                    }


                    override fun disInformationReceived(identifier: String, uuid: UUID, value: String) {
                        if (uuid == UUID.fromString("00002a28-0000-1000-8000-00805f9b34fb")) {
                            val msg = "Firmware: " + value.trim { it <= ' ' }
                            Log.d("FRMWR", identifier + " " + value.trim { it <= ' ' })
                            textViewFwVersion.append(msg.trimIndent())
                        }
                    }

                    override fun batteryLevelReceived(identifier: String, level: Int) {
                        Log.d("BTRY", "Battery level $identifier $level%")
                        val batteryLevelText = " $level%"
                        textViewBattery.append(batteryLevelText)
                    }

                    override fun hrNotificationReceived(identifier: String, data: PolarHrData.PolarHrSample) {
                        // deprecated
                    }

                    override fun polarFtpFeatureReady(identifier: String) {
                        // deprecated
                    }

                    override fun streamingFeaturesReady(identifier: String, features: Set<PolarBleApi.PolarDeviceDataType>) {
                        // deprecated
                    }

                    override fun hrFeatureReady(identifier: String) {
                        // deprecated
                    }

                })
                try {
                    api.connectToDevice(id)
                } catch (a: PolarInvalidArgument) {
                    a.printStackTrace()
                }
            }
        }



        return root

    }





    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}