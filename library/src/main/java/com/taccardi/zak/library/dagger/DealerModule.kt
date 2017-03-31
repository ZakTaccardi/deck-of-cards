package com.taccardi.zak.library.dagger

import com.taccardi.zak.library.model.Dealer
import com.taccardi.zak.library.model.ForceError
import com.taccardi.zak.library.model.InMemoryDealer
import com.taccardi.zak.library.pojo.Schedulers
import dagger.Module
import dagger.Provides

/**
 * Handles dependencies for [Dealer]
 */
@Module
class DealerModule {


    @Provides
    @DataScope
    fun dealer(
            schedulers: Schedulers
    ): Dealer {
        return InMemoryDealer(
                schedulers.comp,
                forceError = ForceError.SOMETIMES,
                delayMs = 500
        )
    }
}
