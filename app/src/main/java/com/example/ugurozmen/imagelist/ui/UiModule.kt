package com.example.ugurozmen.imagelist.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ugurozmen.imagelist.App
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import javax.inject.Provider

@Module(includes = [ViewModelModule::class, ViewModule::class])
class UiModule

@Module
abstract class ViewModule {
    @Binds
    abstract fun bindApplication(app: App): Application

    @Binds
    abstract fun bindContext(application: Application): Context

    @ContributesAndroidInjector
    abstract fun contributeMainActivityInjector(): MainActivity
}

@Module
class ViewModelModule {
    @Provides
    fun provideViewModelProviderFactory(
        vehiclesVMProvider: Provider<MainViewModel>
    ): ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T =
                when (modelClass) {
                    MainViewModel::class.java -> vehiclesVMProvider.get() as T
                    else -> throw IllegalArgumentException("Unexpected modelClass: $modelClass")
                }
        }
}