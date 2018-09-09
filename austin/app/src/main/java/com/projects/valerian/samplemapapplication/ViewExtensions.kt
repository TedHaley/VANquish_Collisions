package com.projects.valerian.samplemapapplication

import android.view.View

fun View.setVisibility(isEnabled: Boolean) = this.run {
     visibility = if (isEnabled) View.VISIBLE else View.GONE
}