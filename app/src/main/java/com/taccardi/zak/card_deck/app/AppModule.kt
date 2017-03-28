package com.taccardi.zak.card_deck.app

import android.app.Application
import dagger.Module
import dagger.Provides

/**
 * Provides application level dependencies.
 *
 * @see AppComponent
 */
@Module
class AppModule(val application: MyApplication) {


    @Provides @AppScope
    fun provideApplcation(): Application = application

    @Provides @AppScope
    fun provideMyApplcation(): MyApplication = application

}
