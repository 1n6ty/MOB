package com.example.mobv2.ui.fragment.markerCreator

import com.example.mobv2.callback.CreatePostCallback
import com.example.mobv2.model.AddressImpl
import com.example.mobv2.util.ObservableViewModel

class MarkerCreatorViewModel : ObservableViewModel() {
    lateinit var address: AddressImpl
    lateinit var createPostCallback: CreatePostCallback
}