package com.example.ugurozmen.imagelist.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ugurozmen.imagelist.model.PhotoRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(private val photoRepository: PhotoRepository) :
    ViewModel() {

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            photoRepository.getPhotos().forEach {
                Log.d("MainVM", it.toString())
            }
        }
    }
}