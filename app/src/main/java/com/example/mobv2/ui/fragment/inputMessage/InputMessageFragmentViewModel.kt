package com.example.mobv2.ui.fragment.inputMessage

import com.example.mobv2.callback.abstraction.CommentOkCallback
import com.example.mobv2.util.ObservableViewModel

class InputMessageFragmentViewModel : ObservableViewModel()
{
    var active: Boolean = false
    lateinit var parentId: String
    lateinit var commentOkCallback: CommentOkCallback
}