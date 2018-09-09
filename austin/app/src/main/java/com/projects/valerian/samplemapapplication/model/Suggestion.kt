package com.projects.valerian.samplemapapplication.model

import com.google.gson.annotations.SerializedName


enum class InfrastructureType(val readable: String, val description: String) {
    @SerializedName("ROUNDABOUT")
    ROUNDABOUT("Roundabout", "Small and Medium roundabouts slow down traffic in residential areas"),
    @SerializedName("Bike Lane")
    BIKE_LANE("Bike Lane", "Designated lane space for bikes, providing safety to motorists, cyclists, and pedestrians"),
    @SerializedName("STREETLIGHT")
    STREETLIGHT("Streetlight", "Increase visibility in the area"),
    @SerializedName("FOUR_WAY_STOP")
    FOUR_WAY_STOP("Four way stop", "Drivers must acknowledge each other and right of way"),
    @SerializedName("SPEED_HUMP")
    SPEED_BUMP("Speed hump", "Force drivers to slow down when going over speed hump")
}

class Suggestion(val lat: Double, val lon: Double, val items: List<InfrastructureType>, val proposal: String)