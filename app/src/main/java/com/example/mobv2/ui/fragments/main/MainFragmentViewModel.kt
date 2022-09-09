package com.example.mobv2.ui.fragments.main

import androidx.databinding.Bindable
import com.example.mobv2.BR
import com.example.mobv2.models.MarkerInfo
import com.example.mobv2.utils.ObservableViewModel

class MainFragmentViewModel : ObservableViewModel() {
    var isAddressChanged = false

    @Bindable
    var fullname = ""
        set(value) {
            field = value

            notifyPropertyChanged(BR.fullname)
        }

    @Bindable
    var address = ""
        set(value) {
            field = value

            notifyPropertyChanged(BR.address)
        };

    @Bindable
    var postTitle = ""
        set(value) {
            field = value

            notifyPropertyChanged(BR.postTitle)
        }
}