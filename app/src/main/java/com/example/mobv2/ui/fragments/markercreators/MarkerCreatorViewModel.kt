package com.example.mobv2.ui.fragments.markercreators

import com.example.mobv2.models.MyAddress
import com.example.mobv2.utils.ObservableViewModel
import com.google.android.gms.maps.model.LatLng
import com.example.mobv2.serverapi.MOBServerAPI.MOBAPICallback

class MarkerCreatorViewModel : ObservableViewModel() {
    lateinit var latLng: LatLng
    lateinit var callback: MOBAPICallback
    lateinit var address: MyAddress
}