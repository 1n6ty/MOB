package com.example.mobv2.ui.fragment.main

import androidx.databinding.Bindable
import com.example.mobv2.BR
import com.example.mobv2.util.ObservableViewModel

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