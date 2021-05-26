package com.lordeats.mobeats.listeners

import com.lordeats.mobeats.viewModel.CustomViewModel

interface CustomListeners {

    fun onClickLeft(item : CustomViewModel, position : Int)

    fun onClickRight(item : CustomViewModel, position : Int)
}