package com.projects.valerian.samplemapapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import com.projects.valerian.samplemapapplication.model.InfrastructureType
import com.projects.valerian.samplemapapplication.model.Suggestion
import kotlinx.android.synthetic.main.activity_suggestion_detail.*

class DetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.activity_suggestion_detail)
        val lat: Double = intent?.extras?.getDouble(EXTRA_LATITUDE)!!
        val lon: Double = intent?.extras?.getDouble(EXTRA_LONGITUDE)!!
        val proposal: String = intent?.extras?.getString(EXTRA_PROPOSAL)!!
        val suggestions = intent?.extras?.getStringArray(EXTRA_SUGGESTIONS)!!

        latitude.text = "Latitude: $lat"
        longitude.text = "Longitude: $lon"
        suggestions_detail.text = suggestions.map { InfrastructureType.valueOf(it).readable }.joinToString()
        proposal_detail.text = proposal
    }

    companion object {
        private const val EXTRA_LATITUDE = "extra_latitude"
        private const val EXTRA_LONGITUDE = "extra_longitude"
        private const val EXTRA_SUGGESTIONS = "extra_suggestions"
        private const val EXTRA_PROPOSAL = "extra_proposal"

        fun createIntent(
                context: Context,
                suggestion: Suggestion
        ) = Intent(context, DetailsActivity::class.java).apply {
                    putExtra(EXTRA_LATITUDE, suggestion.lat)
                    putExtra(EXTRA_LONGITUDE, suggestion.lon)
                    putExtra(EXTRA_SUGGESTIONS, suggestion.items.map { it.name }.toTypedArray() )
                    putExtra(EXTRA_PROPOSAL, suggestion.proposal)
                }
    }
}