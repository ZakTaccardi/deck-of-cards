package com.taccardi.zak.library.dagger

import com.taccardi.zak.library.model.Dealer
import com.taccardi.zak.library.pojo.Schedulers
import dagger.Component
import javax.inject.Scope

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class DataScope


@DataScope
@Component(modules = arrayOf(
        DealerModule::class,
        ThreadingModule::class
))
interface DataComponent {

    val Dealer: Dealer



    @Component.Builder
    abstract class Builder {

        abstract fun dealearModule(module: DealerModule): Builder

        abstract fun threadingModule(module: ThreadingModule): Builder

        fun schedulers(schedulers: Schedulers): Builder {
            return threadingModule(ThreadingModule(schedulers))
        }

        abstract fun build(): DataComponent
    }

    companion object {
        fun builder() {
        }
    }
}


class DataLayer {
    companion object {
        /**
         * The main/UI thread.
         */
        const val MAIN = "main"
        /**
         * A computation threadpool. Should be used for object creation/calculation.
         */
        const val COMP = "comp"
        /**
         * A thread for accessing the database.
         */
        const val DISK = "disk"

        const val NETWORK = "network"

        const val TRAMPOLINE = "trampoline"
    }
}