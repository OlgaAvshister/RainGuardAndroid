package com.olga.avshister.rainguard

import android.app.Application
import androidx.multidex.MultiDexApplication
import com.yandex.mapkit.MapKitFactory

class App: MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        MapKitFactory.setApiKey(BuildConfig.MAPKIT_API_KEY)
        MapKitFactory.initialize(this)
    }
}