package com.taccardi.zak.card_deck

import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity

/**
 * Base activity for all activities to inherit from.
 */
abstract class BaseActivity : AppCompatActivity() {
    @Suppress("LeakingThis")
    private val state = StateSaverActivityDelegate(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        state.onCreate(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        state.onSaveInstanceState(outState)
    }

}
