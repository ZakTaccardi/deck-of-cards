package com.taccardi.zak.card_deck.app

import com.taccardi.zak.card_deck.presentation.deal_cards.DealCardsUiComponent
import com.taccardi.zak.card_deck.presentation.deal_cards.DealCardsUiModule
import com.taccardi.zak.library.dagger.DataComponent
import dagger.Component

/**
 * Dagger component to provide application wide dependencies
 */
@AppScope
@Component(modules = arrayOf(
        AppModule::class
),
        dependencies = arrayOf(DataComponent::class)
)
interface AppComponent : DataComponent {


    @Component.Builder
    abstract class Builder {

        abstract fun moduleApp(appModule: AppModule): Builder

        fun app(app: MyApplication): Builder = moduleApp(AppModule(app))

        abstract fun data(component: DataComponent): Builder

        abstract fun build(): AppComponent
    }

    fun plus(module: DealCardsUiModule): DealCardsUiComponent

}

