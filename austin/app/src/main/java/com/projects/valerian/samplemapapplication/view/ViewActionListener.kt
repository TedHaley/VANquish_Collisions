package com.projects.valerian.samplemapapplication.view

interface ViewActionListener {
    fun onAction(action: Action, data: Any): Boolean
}

enum class Action {
    LONG_PRESS, CLICK
}