package com.example.mobv2.ui.activity.mainActivity

import androidx.databinding.Bindable
import com.example.mobv2.BR
import com.example.mobv2.util.ObservableViewModel

class MainActivityViewModel : ObservableViewModel() {
    var isAddressChanged = false

    @Bindable
    var avatarUrl = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.avatarUrl)
        }

    @Bindable
    var fullName = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.fullName)
        }

    @Bindable
    var nickName = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.nickName)
        }

    @Bindable
    var phoneNumber = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.phoneNumber)
        }

    @Bindable
    var email = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.email)
        }

    @Bindable
    var fullAddress = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.fullAddress)
        }
}