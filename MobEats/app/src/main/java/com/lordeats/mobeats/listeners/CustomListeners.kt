package com.lordeats.mobeats.listeners

import com.lordeats.mobeats.viewModel.CustomViewModel

interface CustomListeners {

    public fun onClickLeft(item : CustomViewModel, position : Int)

    public fun onClickRight(item : CustomViewModel, position : Int)
}