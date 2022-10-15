package com.example.mobv2.ui.fragments.markercreators

import com.example.mobv2.models.AddressImpl
import com.example.mobv2.models.abstractions.Address
import com.example.mobv2.utils.ObservableViewModel
import com.google.android.gms.maps.model.LatLng
import serverapi.MOBServerAPI.MOBAPICallback

class MarkerCreatorViewModel : ObservableViewModel() {
    lateinit var latLng: LatLng
    lateinit var callback: MOBAPICallback
    lateinit var address: Address
}