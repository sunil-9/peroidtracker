package com.ifemini

import android.app.Application
import io.realm.Realm

class Log28 : Application() {
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
    }
}
