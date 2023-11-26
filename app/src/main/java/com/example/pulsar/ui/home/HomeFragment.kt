package com.example.pulsar.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.pulsar.MainViewModel
import com.example.pulsar.databinding.FragmentHomeBinding
import com.polar.sdk.api.PolarBleApi
import com.polar.sdk.api.PolarBleApiCallback
import com.polar.sdk.api.PolarBleApiDefaultImpl
import com.polar.sdk.api.errors.PolarInvalidArgument
import com.polar.sdk.api.model.PolarDeviceInfo
import com.polar.sdk.api.model.PolarHrData
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import java.util.UUID

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var api: PolarBleApi
    private var ecgDisposable: Disposable? = null
    private var hrDisposable: Disposable? = null

    private lateinit var textHR: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val mainViewModel =
            ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        val mainBtn: ImageButton = binding.mainBtn
        val dvcid: TextView = binding.deviceidTv
        textHR = binding.hrTv

        mainViewModel.text.observe(viewLifecycleOwner) {
            val id = it.toString().uppercase()
            if (id.isNotEmpty()) {
                initializeApi(id)
            }
        }

        var id = dvcid.text.toString().uppercase()


        mainBtn.setOnClickListener {
            if (id.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter a valid ID", Toast.LENGTH_SHORT).show()
            } else {
                initializeApi(id)
            }
        }


        textView.text = "Press Here To Start Recording Your Heart Rate"

        return root
    }

    private fun initializeApi(id: String){
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


            override fun bleSdkFeatureReady(identifier: String, feature: PolarBleApi.PolarBleSdkFeature) {
                Log.d("FTR RDY", "feature ready $feature")

                when (feature) {
                    PolarBleApi.PolarBleSdkFeature.FEATURE_POLAR_ONLINE_STREAMING -> {
//                                streamECG(
                        hrDisposable?.dispose() // Dispose previous streaming if any
                        hrDisposable = api.startHrStreaming(id)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                { hrData: PolarHrData ->
                                    for (sample in hrData.samples) {
                                        Log.d("TAG", "HR " + sample.hr)
                                        // Update UI or perform operations based on HR data...
                                        textHR.text = sample.hr.toString()

                                    }
                                },
                                { error: Throwable ->
                                    Log.e("HR ERROR", "HR stream failed. Reason $error")
                                    hrDisposable = null
                                },
                                { Log.d("HR CMPLT", "HR stream complete") }
                            )
                    }
                    else -> {}
                }
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

//    private fun streamHR(id: String) {
//        hrDisposable?.dispose() // Dispose previous streaming if any
//        hrDisposable = api.startHrStreaming(id)
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe(
//                { hrData: PolarHrData ->
//                    for (sample in hrData.samples) {
//                        Log.d("TAG", "HR " + sample.hr)
//                        // Update UI or perform operations based on HR data...
//                        textHR.text = sample.hr.toString()
//                    }
//                },
//                { error: Throwable ->
//                    Log.e("HR ERROR", "HR stream failed. Reason $error")
//                    hrDisposable = null
//                },
//                { Log.d("HR CMPLT", "HR stream complete") }
//            )
//    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        api.shutDown()
    }
}