package com.example.smartglassapplication.ble

import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.BluetoothGatt.GATT_SUCCESS
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.os.Build
import android.util.Log
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.UUID

/**
 * BLE helper for Brilliant Labs Frame:
 *  - Scans for a device whose name contains "Frame"
 *  - Connects
 *  - Writes a UTF‑8 text line (chunked ≤20 bytes) to the Nordic‑UART TX characteristic
 *  - Logs each step under tag "BLE-TX"
 */
object BrilliantBleClient {

    private val SERVICE_UUID = UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E")
    private val TX_UUID      = UUID.fromString("6E400002-B5A3-F393-E0A9-E50E24DCCA9E")

    @SuppressLint("MissingPermission")
    fun sendText(ctx: Context, text: String): Flow<Result<Unit>> = callbackFlow {
        val adapter = ctx.getSystemService(BluetoothManager::class.java).adapter

        // 1) Connect & write packets
        fun connectAndWrite(device: BluetoothDevice) {
            device.connectGatt(ctx, false, object : BluetoothGattCallback() {
                private lateinit var chunks: List<ByteArray>
                private var idx = 0

                override fun onConnectionStateChange(
                    gatt: BluetoothGatt,
                    status: Int,
                    newState: Int
                ) {
                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        Log.d("BLE-TX", "Connected → discovering services…")
                        gatt.discoverServices()
                    } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                        Log.d("BLE-TX", "Disconnected")
                        trySend(Result.failure(Exception("Disconnected")))
                        close()
                    }
                }

                override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                    val txChar = gatt.getService(SERVICE_UUID)
                        ?.getCharacteristic(TX_UUID)
                    if (txChar == null) {
                        Log.e("BLE-TX", "UART service not found")
                        trySend(Result.failure(Exception("UART service not found")))
                        close()
                        return
                    }
                    // Chunk into ≤20‑byte packets
                    chunks = text.toByteArray(Charsets.UTF_8)
                        .toList()
                        .chunked(20) { it.toByteArray() }
                    idx = 0
                    sendNextPacket(gatt, txChar)
                }

                private fun sendNextPacket(
                    gatt: BluetoothGatt,
                    txChar: BluetoothGattCharacteristic
                ) {
                    if (idx >= chunks.size) {
                        Log.d("BLE-TX", "All chunks sent, disconnecting")
                        trySend(Result.success(Unit))
                        gatt.disconnect()
                        close()
                        return
                    }
                    val packet = chunks[idx]
                    Log.d("BLE-TX", "Chunk #$idx → “${packet.decodeToString()}”")

                    val ok: Boolean = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val result = gatt.writeCharacteristic(
                            txChar,
                            packet,
                            BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
                        )
                        result == GATT_SUCCESS
                    } else {
                        @Suppress("DEPRECATION")
                        txChar.value = packet
                        @Suppress("DEPRECATION")
                        gatt.writeCharacteristic(txChar)
                    }

                    if (!ok) {
                        Log.e("BLE-TX", "Characteristic write failed")
                        trySend(Result.failure(Exception("Characteristic write failed")))
                        gatt.disconnect()
                        close()
                    }
                }

                override fun onCharacteristicWrite(
                    gatt: BluetoothGatt,
                    characteristic: BluetoothGattCharacteristic,
                    status: Int
                ) {
                    if (status == GATT_SUCCESS) {
                        idx++
                        sendNextPacket(gatt, characteristic)
                    } else {
                        Log.e("BLE-TX", "Write error status=$status")
                        trySend(Result.failure(Exception("Write error (status $status)")))
                        gatt.disconnect()
                        close()
                    }
                }
            })
        }

        // 2) Scan callback
        val scanCb = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                result.device.name?.let { name ->
                    Log.d("BLE-TX", "Discovered device: $name")
                    if (name.contains("Frame", ignoreCase = true)) {
                        adapter.bluetoothLeScanner.stopScan(this)
                        connectAndWrite(result.device)
                    }
                }
            }

            override fun onScanFailed(errorCode: Int) {
                Log.e("BLE-TX", "Scan failed: code=$errorCode")
                trySend(Result.failure(Exception("Scan failed (code $errorCode)")))
                close()
            }
        }

        // 3) Start scanning
        adapter.bluetoothLeScanner.startScan(scanCb)
        awaitClose { adapter.bluetoothLeScanner.stopScan(scanCb) }
    }
}
