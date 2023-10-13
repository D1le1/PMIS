package by.korsakovegor.weightapplication

import android.Manifest
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import by.korsakovegor.weightapplication.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.PrintWriter
import java.lang.Exception
import java.lang.StringBuilder
import java.util.UUID

private lateinit var binding: ActivityMainBinding
private const val ADDRESS = "00:21:13:00:47:2E"

private lateinit var bluetoothSocket: BluetoothSocket


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mainScope = MainScope()

        mainScope.launch { connectToBluetoothDevice() }

        binding.button.setOnClickListener {
            val buffer = ByteArray(1024)
            val stringBuilder = StringBuilder()

            GlobalScope.launch(Dispatchers.IO) {
                while (isActive) { // Продолжаем читать, пока корутина активна
                    val bytesRead = bluetoothSocket.inputStream?.read(buffer)
                    if (bytesRead == null || bytesRead == -1) {
                        // Если чтение завершено, выходим из цикла
                        break
                    }

                    val receivedData = String(buffer, 0, bytesRead, Charsets.UTF_8)
                    stringBuilder.append(receivedData)

                    // Обновите UI в главном потоке
                    withContext(Dispatchers.Main) {
                        binding.textView.text = stringBuilder.toString()
                    }
                }
            }
        }
    }


    private suspend fun connectToBluetoothDevice() {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter
        val bluetoothDevice = bluetoothAdapter.getRemoteDevice(ADDRESS)
        val uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

        if (checkBluetoothPermission()) {
            try {
                bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid)
//                bluetoothAdapter.cancelDiscovery()
                bluetoothSocket.connect()

                if (bluetoothSocket.isConnected)
                    binding.textView.text = "Connected"


//                val inputStreamReader = InputStreamReader(bluetoothSocket.inputStream, "UTF-8") // Укажите кодировку
//                val bufferedReader = BufferedReader(inputStreamReader)
//
//                val stringBuilder = StringBuilder()
//                var line: String? = null
//                while ({ line = bufferedReader.readLine(); line }() != null) {
//                    stringBuilder.append(line)
//                }
//
//                val receivedData = stringBuilder.toString()
//
//                binding.textView.text = receivedData
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun changeToWeight() {
        try {
            val pw = PrintWriter(bluetoothSocket.outputStream)
            pw.println("1231")
            pw.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun checkBluetoothPermission(): Boolean {
        val permission = Manifest.permission.BLUETOOTH_CONNECT
        val result = ActivityCompat.checkSelfPermission(this, permission)
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestBluetoothPermission() {
        val permission = Manifest.permission.BLUETOOTH_CONNECT
        ActivityCompat.requestPermissions(
            this,
            arrayOf(permission, Manifest.permission.BLUETOOTH),
            1
        )

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED)
                requestBluetoothPermission()
        }
    }

}