package com.taccardi.zak.card_deck.app

import com.taccardi.zak.library.dagger.DataComponent
import dagger.Component

/**
 * Created by zak.taccardi on 3/27/17.
 */

@AppScope
@Component(modules = arrayOf(

),
        dependencies = arrayOf(DataComponent::class)
)
interface AppComponent : DataComponent {


    @Component.Builder
    abstract class Builder {

        abstract fun data(component: DataComponent): Builder

        abstract fun build(): AppComponent
    }

}

