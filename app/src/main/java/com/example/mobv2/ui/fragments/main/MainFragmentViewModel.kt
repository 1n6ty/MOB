package com.example.mobv2.ui.fragments.main

import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import com.example.mobv2.BR
import com.example.mobv2.models.MarkerInfo
import com.example.mobv2.models.PostWithMark
import com.example.mobv2.utils.ObservableViewModel
import com.google.android.gms.maps.model.Marker

class MainFragmentViewModel : ObservableViewModel() {
    val postsWithMarks: List<PostWithMark> = ArrayList()
    var markerInfo = MarkerInfo(null, -1)
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