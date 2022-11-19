package com.example.mobv2.ui.fragment.markerCreator

import com.example.mobv2.model.AddressImpl
import com.example.mobv2.util.ObservableViewModel
import serverAPI.MOBServerAPI.MOBAPICallback

class MarkerCreatorViewModel : ObservableViewModel() {
    lateinit var callback: MOBAPICallback
    lateinit var address: AddressImpl
}