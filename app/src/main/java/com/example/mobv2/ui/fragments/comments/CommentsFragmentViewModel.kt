package com.example.mobv2.ui.fragments.comments

import androidx.databinding.Bindable
import com.example.mobv2.BR
import com.example.mobv2.utils.ObservableViewModel
import com.example.mobv2.adapters.PostsAdapter.PostItem

class CommentsFragmentViewModel : ObservableViewModel() {
    var postItem: PostItem? = null
}