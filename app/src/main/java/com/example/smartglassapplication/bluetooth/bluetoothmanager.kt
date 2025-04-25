package com.example.smartglassapplication.bluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.*
import android.bluetooth.le.*
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.ParcelUuid
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.util.*

class BleCommunicator(private val context: Context) {

    private var gatt: BluetoothGatt? = null
    private var txCharacteristic: BluetoothGattCharacteristic? = null
    private var rxCharacteristic: BluetoothGattCharacteristic? = null
    private var message: String? = null

    private val serviceUUID =  UUID.fromString("7A230001-5475-A6A4-654C-8431F6AD49C4")
    private val rxUUID = UUID.fromString("7A230003-5475-A6A4-654C-8431F6AD49C4")     // TX characteristic UUID
    private val txUUID = UUID.fromString("7A230002-5475-A6A4-654C-8431F6AD49C4")

    private val bluetoothAdapter: BluetoothAdapter by lazy {
        val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as android.bluetooth.BluetoothManager
        manager.adapter
    }

    private val scanner: BluetoothLeScanner by lazy {
        bluetoothAdapter.bluetoothLeScanner
    }

    fun connectToDevice(msg: String) {
        message = msg
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        val device = bluetoothAdapter.getRemoteDevice("DC:3A:AF:19:9D:35") // Replace with your device's MAC
        gatt = device.connectGatt(context, false, gattCallback, BluetoothDevice.TRANSPORT_LE)

    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            Log.d("BLE", "onConnectionStateChange: status=$status newState=$newState")
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.d("BLE", "Connected to device")
                runBlocking {
                    println("Before delay")
                    delay(200) // Delay for 2 seconds
                    println("After delay")
                }
                val device = gatt.device
                if (device.bondState == BluetoothDevice.BOND_NONE) {
                    Log.d("BLE", "Bonding required — attempting to bond...")
                    if (ActivityCompat.checkSelfPermission(
                            context,
                            Manifest.permission.BLUETOOTH_CONNECT
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return
                    }
                    device.createBond()
                } else {
                    Log.d("BLE", "Device is already bonded.")
                }
                if (ActivityCompat.checkSelfPermission(context,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                gatt.requestMtu(256)
            }
            if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                gatt.close()
            }
        }

        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            val service = gatt.getService(serviceUUID)
            txCharacteristic = service?.getCharacteristic(txUUID)
            rxCharacteristic = service?.getCharacteristic(rxUUID)
            val device = gatt.device
            runBlocking { delay(300) }
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            if (device.bondState == BluetoothDevice.BOND_BONDED) {
                Log.d("BLE", "Device is bonded and connected.")
                // Now proceed with sending data.
                message?.let { sendMessage(it) }
            } else {
                Log.e("BLE", "Device is not bonded or GATT is not connected.")
            }
        }

        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        override fun onCharacteristicWrite(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            Log.d("BLE", "onCharacteristicWrite fired!")
            val statusStr = when (status) {
                BluetoothGatt.GATT_SUCCESS -> "GATT_SUCCESS"
                BluetoothGatt.GATT_WRITE_NOT_PERMITTED -> "GATT_WRITE_NOT_PERMITTED"
                BluetoothGatt.GATT_REQUEST_NOT_SUPPORTED -> "GATT_REQUEST_NOT_SUPPORTED"
                BluetoothGatt.GATT_INSUFFICIENT_AUTHENTICATION -> "GATT_INSUFFICIENT_AUTHENTICATION"
                BluetoothGatt.GATT_FAILURE -> "GATT_FAILURE"
                else -> "Unknown error ($status)"
            }
            Log.d("BLE", "onCharacteristicWrite: status=$status ($statusStr)")
        }

        @Deprecated("Deprecated in Java")
        override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
                val data = characteristic.value
                val message = data.toString(Charsets.UTF_8)
                Log.d("BLE", "Received data: $message")
        }
        override fun onMtuChanged(gatt: BluetoothGatt, mtu: Int, status: Int) {
            Log.d("BLE", "onMtuChanged: mtu=$mtu, status=$status")
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            gatt.discoverServices()
        }
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun sendMessage(msg: String) {
        Log.d("BLE", "in send message")
        txCharacteristic?.let {
            val command = "frame.display.text('$msg', 1, 1);frame.display.show()"
            val byteArray: ByteArray = msg.toByteArray(Charsets.UTF_8)
            val success = if (ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            } else {
                gatt?.writeCharacteristic(txCharacteristic!!, byteArray, BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT)
            }
            Log.d("BLE", "Write success: $success")
        }
    }

}

