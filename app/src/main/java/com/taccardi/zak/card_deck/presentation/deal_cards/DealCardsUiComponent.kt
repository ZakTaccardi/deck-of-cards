package com.taccardi.zak.card_deck.presentation.deal_cards

import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import com.jakewharton.rxrelay2.Relay
import com.taccardi.zak.card_deck.R
import com.taccardi.zak.card_deck.app.ActivityScope
import dagger.Subcomponent
import javax.inject.Named

@Subcomponent(modules = arrayOf(
        DealCardsUiModule::class
)
)
@ActivityScope
interface DealCardsUiComponent {
    fun injectMembers(dealCardsActivity: DealCardsActivity)

    val renderer: DealCardsUiRenderer

    val cards: CardsRecycler

    val presenter: DealCardsPresenter

    @get:Named(DealCardsUiComponent.DEAL_CARD)
    val dealCardClicks: Relay<Unit>

    @get:Named(DealCardsUiComponent.SHUFFLE_DECK)
    val shuffleDeckClicks: Relay<Unit>

    @get:Named(DealCardsUiComponent.NEW_DECK)
    val newDeckRequests: Relay<Unit>

    @get:Named(DealCardsUiComponent.NEW_DECK)
    val newDeckButton: View

    @get:Named(DealCardsUiComponent.SHUFFLE_DECK)
    val shuffleButton: View

    @get:Named(DealCardsUiComponent.CARDS_LEFT)
    val cardsLeftHint: TextView

    @get:Named(DealCardsUiComponent.PROGRESS_BAR)
    val progressBar: ProgressBar

    @get:Named(DealCardsUiComponent.DEAL_CARDS_LAYOUT)
    val dealCardsUi: ViewGroup

    @get:Named(DealCardsUiComponent.ERROR)
    val error: TextView
    
    companion object {
        const val NEW_DECK = R.id.button_new_deck.toString()
        const val DEAL_CARD = "deal_card_first_item_in_recycler"
        const val SHUFFLE_DECK = R.id.button_shuffle.toString()
        const val CARDS_LEFT = R.id.dealCardsUi_cardsRemaining_textView.toString()
        const val PROGRESS_BAR = R.id.dealCardsUi_progressBar_loading.toString()
        const val ERROR = R.id.dealCardsUi_error.toString()
        const val DEAL_CARDS_LAYOUT = R.id.dealCardsUi.toString()
    }
}