package com.taccardi.zak.card_deck

import timber.log.Timber

/**
 * A simple timber tree that logs to the system output stream
 */
object SystemOutTree : Timber.Tree() {
    override fun log(logLevel: Int, tag: String?, message: String?, throwable: Throwable?) {
        System.out.println("$tag == $message")
    }
}
