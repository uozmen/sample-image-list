package com.example.ugurozmen.imagelist.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ugurozmen.imagelist.model.Photo
import com.example.ugurozmen.imagelist.model.PhotoRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(private val photoRepository: PhotoRepository) :
    ViewModel() {
    private val _content = MutableLiveData<Content>(Content.Loading)
    val content: LiveData<Content> = _content

    private val _listItems = MutableLiveData<List<Item>>(listOf())
    val listItems: LiveData<List<Item>> = _listItems

    private var images = listOf<Photo>()
    private var loadingJob: Job? = null

    init {
        load()
    }

    private fun load() {
        loadingJob = viewModelScope.launch {
            try {
                images = photoRepository.getPhotos()
                _listItems.value = images.map {
                    Item(it.thumbnailUrl, it.title)
                }
                _content.value = Content.Loaded
            } catch (e: Exception) {
                _content.value = Content.Error(e.localizedMessage.orEmpty())
            }
        }

    }

    fun onItemSelected(selectedIndex: Int) {
        if (isLoaded()) {
            val selected = images[selectedIndex]
            _content.postValue(Content.Detail(selected.url, selectedIndex, selected.title))
        }
    }

    fun onBackPressed(): Boolean {
        if (isDetail()) {
            _content.postValue(Content.Loaded)
            return true
        }
        return false
    }

    fun onRetryClicked() {
        if (isFailed()) {
            loadingJob?.cancel()
            load()
        }
    }

    private fun isFailed() = content.value is Content.Error

    private fun isLoaded() = content.value is Content.Loaded

    private fun isDetail() = content.value is Content.Detail

    sealed class Content {
        object Loading : Content()
        data class Error(val description: String) : Content()
        object Loaded : Content()
        data class Detail(val imageUrl: String, val selectedIndex: Int, val title: String) :
            Content()
    }

    data class Item(val thumbnailUrl: String, val title: String)
}