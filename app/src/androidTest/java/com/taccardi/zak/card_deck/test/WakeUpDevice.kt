package com.taccardi.zak.card_deck.test

import android.app.Activity
import android.view.WindowManager


/**
 * This will wake up the device and try to unlock the screen. If there is a password/pattern lock
 * on the device, then MainActivity will get launched on top of the lock screen instead of
 * unlocking the device (how cool is that?).
 */

fun Activity.wakeUpDevice() {
    this.runOnUiThread {
        this.window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
}