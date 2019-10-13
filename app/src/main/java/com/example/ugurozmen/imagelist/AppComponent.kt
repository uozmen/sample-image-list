package com.example.ugurozmen.imagelist

import com.example.ugurozmen.imagelist.model.ModelModule
import com.example.ugurozmen.imagelist.ui.UiModule
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Component(modules = [AndroidInjectionModule::class, UiModule::class, ModelModule::class])
@Singleton
interface AppComponent : AndroidInjector<App> {
    @Component.Factory
    abstract class Builder : AndroidInjector.Factory<App>
}