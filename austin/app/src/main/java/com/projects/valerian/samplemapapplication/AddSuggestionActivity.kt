package com.projects.valerian.samplemapapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.projects.valerian.samplemapapplication.model.InfrastructureType
import com.projects.valerian.samplemapapplication.view.Action
import com.projects.valerian.samplemapapplication.view.InfrastructureTypeAdapter
import com.projects.valerian.samplemapapplication.view.ViewActionListener
import kotlinx.android.synthetic.main.activity_suggestion.*

class AddSuggestionActivity : AppCompatActivity() {

    private val selectedItems = mutableSetOf<InfrastructureType>()
    private lateinit var coords: LatLng

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_suggestion)

        val lat: Double = intent?.extras?.getDouble(EXTRA_LATITUDE)!!
        val lon: Double = intent?.extras?.getDouble(EXTRA_LONGITUDE)!!

        coords = LatLng(lat,lon)

        lbl_latitude.text = "Latitude: $lat"
        lbl_longitude.text = "Longitude: $lon"

        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.content) as SupportMapFragment

        mapFragment.getMapAsync { googleMap ->
            googleMap.run {
                addMarker(MarkerOptions().position(coords))
                moveCamera(CameraUpdateFactory.newLatLngZoom(coords, 15.0f))
            }
        }

        setupButton(coords)
        setupList()
    }

    private fun setupList() {
        rv_selection.layoutManager = LinearLayoutManager(this)
        rv_selection.adapter = InfrastructureTypeAdapter(object : ViewActionListener {
            override fun onAction(action: Action, data: Any): Boolean {
                when (action) {
                    Action.LONG_PRESS -> selectedItems.add(data as InfrastructureType)
                    Action.CLICK -> selectedItems.remove(data as InfrastructureType)
                }
                return true
            }
        })
    }

    private fun setupButton(coords: LatLng) =
            btn_next.setOnClickListener {
                if (this.selectedItems.isEmpty()) {
                    Snackbar.make(main, "Long-press to select an improvement from the list above", Snackbar.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                startActivity(AddDetailsActivity.createIntent(this, coords.latitude, coords.longitude, this.selectedItems.map{ it.name }.toTypedArray()))
                overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left)
                this.selectedItems.clear()
                finish()
            }

    companion object {
        private const val EXTRA_LATITUDE = "extra_latitude"
        private const val EXTRA_LONGITUDE = "extra_longitude"

        fun createIntent(context: Context,
                         lat: Double,
                         lon: Double
        ): Intent = Intent(context, AddSuggestionActivity::class.java).apply {
            putExtra(EXTRA_LATITUDE, lat)
            putExtra(EXTRA_LONGITUDE, lon)
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        }

    }
}