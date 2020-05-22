package com.example.udacityemojifyme.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class MyViewModel(application: Application) : AndroidViewModel(application) {
    private var mState = MutableLiveData<Int>()

    fun getState(): MutableLiveData<Int> {
        return mState
    }

    fun setState(state: Int) {
        mState.value = state
    }
}

