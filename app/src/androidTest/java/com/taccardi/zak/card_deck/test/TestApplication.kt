package com.taccardi.zak.card_deck.test

import com.taccardi.zak.card_deck.app.AppComponent
import com.taccardi.zak.card_deck.app.MyApplication

/**
 * A test application that can provide different dependencies.
 */
class TestApplication : MyApplication() {

    override fun buildComponent(): AppComponent {
        setInTesting(true)
        //just calling super for now
        return super.buildComponent()
    }
}


