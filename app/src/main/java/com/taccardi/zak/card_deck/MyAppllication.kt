package com.taccardi.zak.card_deck

import android.app.Application
import timber.log.Timber

/**
 * Created by zak.taccardi on 3/24/17.
 */

class MyAppllication : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
