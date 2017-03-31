package com.taccardi.zak.library.pojo

import com.taccardi.zak.library.dagger.DataLayer
import com.taccardi.zak.library.dagger.DataScope
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject
import javax.inject.Named

/**
 * A single point of entry for injecting all our schedulers.
 */
@DataScope
class Schedulers @Inject constructor(
        @Named(DataLayer.MAIN) val main: Scheduler,
        @Named(DataLayer.DISK) val disk: Scheduler,
        @Named(DataLayer.COMP) val comp: Scheduler,
        @Named(DataLayer.NETWORK) val network: Scheduler
) {
    companion object {
        fun createTrampoline(useMain: Boolean = false): Schedulers {
            return Schedulers(
                    main = if (useMain) AndroidSchedulers.mainThread() else io.reactivex.schedulers.Schedulers.trampoline(),
                    disk = io.reactivex.schedulers.Schedulers.trampoline(),
                    comp = io.reactivex.schedulers.Schedulers.trampoline(),
                    network = io.reactivex.schedulers.Schedulers.trampoline()
            )
        }
    }
}
