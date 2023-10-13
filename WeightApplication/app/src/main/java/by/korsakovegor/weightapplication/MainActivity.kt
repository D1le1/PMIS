package by.korsakovegor.weightapplication

import android.Manifest
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import by.korsakovegor.weightapplication.databinding.MenuLayoutBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.OutputStream
import java.io.PrintWriter
import java.util.Scanner
import java.util.UUID
import kotlin.math.log

private lateinit var binding: MenuLayoutBinding
private const val ADDRESS = "00:21:13:00:47:2E"

private lateinit var bluetoothSocket: BluetoothSocket
private lateinit var drawerLayout: DrawerLayout
private lateinit var currentFragmentButton: Button


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = MenuLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        drawerLayout = binding.drawerLayout
        currentFragmentButton = binding.userInfo

        CoroutineScope(Dispatchers.IO).launch {
            connectToBluetoothDevice()
        }


        binding.reconnectButton.setOnClickListener {
            changeStatus(0)
            CoroutineScope(Dispatchers.IO).launch {
                connectToBluetoothDevice()
            }
        }

        binding.userInfo.setOnClickListener {
            val stringBuilder = StringBuilder()
            val bufferSize = 1024 // Выберите подходящий размер буфера

            CoroutineScope(Dispatchers.IO).launch {
                val inputStream = bluetoothSocket.inputStream

                val buffer = ByteArray(bufferSize)
                var bytesRead: Int

                while (isActive) {
                    bytesRead = inputStream.read(buffer)

                    if (bytesRead != -1) {
                        val receivedData = String(buffer, 0, bytesRead)
                        stringBuilder.append(receivedData)

                        val dataString = stringBuilder.toString()
                        val messages = dataString.split("Massa:")

                        if (messages.size >= 2) {
                            val lastMessage = "Massa:" + messages[messages.size - 1]
                            withContext(Dispatchers.Main) {
                                binding.data.text = lastMessage
                            }
                            stringBuilder.clear()
                            stringBuilder.append(messages[messages.size - 1])
                        } else {
                            stringBuilder.clear()
                        }
                    }
                }
            }
        }

//        binding.userInfo.setOnClickListener {
//            val stringBuilder = StringBuilder()
//
//            CoroutineScope(Dispatchers.IO).launch {
//                val inputStream = bluetoothSocket.inputStream
//
//
//                while (isActive) {
//                    val bytesAvailable = inputStream.available()
//                    if(bytesAvailable > 0)
//                    {
//                        val buffer = ByteArray(bytesAvailable)
//                        val bytesRead = inputStream.read(buffer, 0, bytesAvailable)
//                        if (bytesRead > 0)
//                        {
//                            val receivedData = String(buffer, 0, bytesRead)
//                            stringBuilder.append(receivedData)
//                            withContext(Dispatchers.Main) {
//                                binding.data.text = stringBuilder.toString()
//                                stringBuilder.clear()
//                            }
//                        }
//                        delay(500)
//                    }
//                }
//            }
//        }

        binding.weightButton.setOnClickListener {
            binding.weightButton.isEnabled = false
            CoroutineScope(Dispatchers.Main).launch {
                delay(2000)
                currentFragmentButton.isEnabled = true
                currentFragmentButton = binding.weightButton
            }
            showFragment(WeightFragment())
            changeToWeight()
        }

        binding.differenceButton.setOnClickListener {
            binding.differenceButton.isEnabled = false
            CoroutineScope(Dispatchers.Main).launch {
                delay(2000)
                currentFragmentButton.isEnabled = true
                currentFragmentButton = binding.differenceButton
            }
            showFragment(DifferenceFragment())
            changeToDifference()
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
                bluetoothSocket.connect()

                if (bluetoothSocket.isConnected) {
                    withContext(Dispatchers.Main)
                    {
                        changeStatus(1)
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    changeStatus(2)
                }
                e.printStackTrace()
            }
        } else {
            withContext(Dispatchers.Main) {
                changeStatus(2)
            }
            requestBluetoothPermission()
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

    private fun changeToDifference() {
        try {
            val pw = PrintWriter(bluetoothSocket.outputStream)
            pw.println("111111")
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

    private fun changeStatus(status: Int) {
        when (status) {
            0 -> {
                binding.reconnectButton.isEnabled = false
                binding.connectText.text = "Connecting..."
                binding.connectIndicator.setImageDrawable(
                    ContextCompat
                        .getDrawable(applicationContext, R.drawable.circle_connecting)
                )
            }

            1 -> {
                binding.reconnectButton.isEnabled = true
                binding.connectText.text = "Connected"
                binding.connectIndicator.setImageDrawable(
                    ContextCompat
                        .getDrawable(applicationContext, R.drawable.circle_active)
                )
            }

            2 -> {
                binding.reconnectButton.isEnabled = true
                binding.connectText.text = "Not Connected"
                binding.connectIndicator.setImageDrawable(
                    ContextCompat
                        .getDrawable(applicationContext, R.drawable.circle_deactivated)
                )
            }
        }
    }

    private fun showFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.slide_in, R.anim.slide_out)
        transaction.replace(binding.fragmentContainerView.id, fragment)
        transaction.commit()
    }

    fun toggleDrawer(view: View) {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START)
        else
            drawerLayout.openDrawer(GravityCompat.START)
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
            else
                CoroutineScope(Dispatchers.IO).launch {
                    connectToBluetoothDevice()
                }
        }
    }

}