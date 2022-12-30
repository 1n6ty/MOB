package com.example.mobv2.ui.fragment.markerCreator

import androidx.databinding.Bindable
import com.example.mobv2.BR
import com.example.mobv2.callback.CreatePostCallback
import com.example.mobv2.model.AddressImpl
import com.example.mobv2.util.ObservableViewModel
import com.google.android.gms.maps.model.LatLng

class MarkerCreatorViewModel : ObservableViewModel() {
    lateinit var address: AddressImpl

    @Bindable
    var latLng: LatLng = address.latLng
        set(value) {
            field = value
            notifyPropertyChanged(BR.latLng)
        }

    @Bindable
    var secondaryAddressString = address.secondary
        set(value) {
            field = value
            notifyPropertyChanged(BR.secondaryAddressString)
        }

    lateinit var createPostCallback: CreatePostCallback
}