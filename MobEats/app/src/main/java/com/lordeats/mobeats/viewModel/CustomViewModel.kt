package com.lordeats.mobeats.viewModel

class CustomViewModel {

    var icon : Int
    var name : String
    var state : SwipeState

    constructor(icon : Int, name : String) {
        this.icon = icon
        this.name = name
        this.state = SwipeState.NONE
    }

    constructor(icon : Int, name : String, state : SwipeState) {
        this.icon = icon
        this.name = name
        this.state = state
    }
}