package com.pp.coworkingapp.app

import android.app.Application
import com.yandex.mapkit.MapKitFactory

class App: Application() {
    override fun onCreate() {
        super.onCreate()

        // Reading API key from BuildConfig.
        // Do not forget to add your MAPKIT_API_KEY property to local.properties file.
        MapKitFactory.setApiKey("b3a0cf10-c007-468a-b836-5031f0e0b965")
    }
}