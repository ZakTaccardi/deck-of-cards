package com.taccardi.zak.library.dagger

import dagger.Module
import dagger.Provides
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Named

/**
 * A module for threading
 */
@Module
class ThreadingModule(
        val schedulers: com.taccardi.zak.library.pojo.Schedulers? = null
) {


    @Provides @DataScope
    fun provideSchedulers(): com.taccardi.zak.library.pojo.Schedulers = schedulers ?: com.taccardi.zak.library.pojo.Schedulers(
            main = main(),
            disk = disk(),
            network = network(),
            comp = comp()
    )

    //    @Provides @Named(DataLayer.MAIN) @DataScope
    fun main(): Scheduler = AndroidSchedulers.mainThread()

    //    @Provides @DataScope @Named(DataLayer.DISK)
    fun disk(): Scheduler = Schedulers.io()

    //    @Provides @DataScope @Named(DataLayer.NETWORK)
    fun network(): Scheduler = disk()

    //    @Provides @DataScope @Named(DataLayer.COMP)
    fun comp(): Scheduler = Schedulers.computation()

    //    @Provides @DataScope @Named(DataLayer.TRAMPOLINE)
    fun trampoline(): Scheduler = Schedulers.trampoline()
}





