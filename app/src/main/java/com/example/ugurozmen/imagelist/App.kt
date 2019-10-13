package com.example.ugurozmen.imagelist

import androidx.annotation.VisibleForTesting
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import okhttp3.OkHttpClient
import javax.inject.Inject

class App : DaggerApplication() {
    @Inject
    @VisibleForTesting
    lateinit var okHttpClient: OkHttpClient

    override fun applicationInjector(): AndroidInjector<App> =
        DaggerAppComponent.factory().create(this)
}