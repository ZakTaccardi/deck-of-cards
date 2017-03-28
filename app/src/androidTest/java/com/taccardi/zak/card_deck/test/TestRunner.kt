package com.taccardi.zak.card_deck.test

import android.app.Application
import android.content.Context
import android.support.test.runner.AndroidJUnitRunner

/**
 * A test runner that lets us provide a different application subclass which will instantiate dependencies differently.
 */
class TestRunner : AndroidJUnitRunner() {
    @Throws(InstantiationException::class, IllegalAccessException::class, ClassNotFoundException::class)


    override fun newApplication(classLoader: ClassLoader, className: String, context: Context): Application {
        return super.newApplication(classLoader, TestApplication::class.java.name, context)
    }
}