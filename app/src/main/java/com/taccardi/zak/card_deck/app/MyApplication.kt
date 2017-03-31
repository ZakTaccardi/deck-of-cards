package com.taccardi.zak.card_deck.app

import android.app.Application
import android.content.Context
import com.taccardi.zak.card_deck.BuildConfig
import com.taccardi.zak.library.dagger.DaggerDataComponent
import com.taccardi.zak.library.dagger.DataComponent
import timber.log.Timber


/**
 * Overridden application.
 */
open class MyApplication : Application() {

    lateinit var component: AppComponent

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        buildComponent()
    }

    fun get(context: Context): MyApplication {
        return context.applicationContext as MyApplication
    }

    open protected fun buildComponent(): AppComponent {
        component = DaggerAppComponent.builder()
                .data(buildDataComponent())
                .build()

        return component
    }

    open protected fun setInTesting(isInTestMode: Boolean) {
        if (isInTestMode) {
            ACTIVATE_PRESENTERS = false
        }
    }

    protected fun buildDataComponent(): DataComponent {
        return DaggerDataComponent.builder()
                .build()
    }

    companion object {
        var ACTIVATE_PRESENTERS = true
            private set
    }
}
