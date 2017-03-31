package com.taccardi.zak.card_deck.presentation.base

import android.os.Bundle
import com.evernote.android.state.StateSaver

/**
 * Provides instance saving state.
 */
class StateSaverActivityDelegate(private val activity: BaseActivity) {
    fun onCreate(savedInstanceState: Bundle?) {
        StateSaver.restoreInstanceState(activity, savedInstanceState)
    }

    fun onSaveInstanceState(outState: Bundle) {
        StateSaver.saveInstanceState(activity, outState)
    }
}
