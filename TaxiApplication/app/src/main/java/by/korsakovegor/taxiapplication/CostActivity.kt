package by.korsakovegor.taxiapplication

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import by.korsakovegor.taxiapplication.databinding.ActivityCostBinding
import by.korsakovegor.taxiapplication.databinding.ActivityTaxiBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


class CostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCostBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val rideCost = intent.getDoubleExtra("cost", 0.0)
        val origin = intent.getStringExtra("origin")
        val destination = intent.getStringExtra("destination")

        binding.rideCost.text = "Ride cost: $rideCost BYN"
        binding.destinationPoint.text = "Destination point: $destination"
        binding.originPoint.text = "Origin point: $origin"

        binding.returnToMap.setOnClickListener{
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        setResult(100)
        finish()
    }
}