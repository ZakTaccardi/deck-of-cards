package com.taccardi.zak.card_deck.presentation.deal_cards

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import com.taccardi.zak.card_deck.R
import com.taccardi.zak.card_deck.app.ActivityScope
import com.taccardi.zak.library.model.Dealer
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class DealCardsUiModule(val activity: DealCardsActivity) {

    @Provides @ActivityScope @Named(DealCardsUiComponent.DEAL_CARD)
    fun dealCardClicks(): Relay<Unit> = PublishRelay.create<Unit>()


    @Provides @ActivityScope @Named(DealCardsUiComponent.SHUFFLE_DECK)
    fun shuffleDeckClicks(): Relay<Unit> = PublishRelay.create<Unit>()

    @Provides @ActivityScope @Named(DealCardsUiComponent.NEW_DECK)
    fun newDeckRequests(): Relay<Unit> = PublishRelay.create<Unit>()

    @Provides @ActivityScope
    fun renderer(dealCardsUi: DealCardsUi, schedulers: com.taccardi.zak.library.pojo.Schedulers): DealCardsUiRenderer {
        return DealCardsUiRenderer(activity, main = schedulers.main, comp = schedulers.comp)
    }

    @Provides @ActivityScope
    fun cards(@Named(DealCardsUiComponent.DEAL_CARD) relay: Relay<Unit>): CardsRecycler {
        val recycler = activity.findViewById(R.id.cards_recycler) as RecyclerView
        return CardsRecycler(recycler, relay)
    }

    @Provides @ActivityScope
    fun presenter(
            ui: DealCardsUi,
            intentions: DealCardsUi.Intentions,
            dealer: Dealer
    ): DealCardsPresenter {
        return DealCardsPresenter(
                ui, intentions, dealer
        )
    }

    @Provides @ActivityScope @Named(DealCardsUiComponent.NEW_DECK)
    fun newDeckButton(): View = activity.findViewById(R.id.button_new_deck)

    @Provides @ActivityScope @Named(DealCardsUiComponent.SHUFFLE_DECK)
    fun shuffleButton(): View = activity.findViewById(R.id.button_shuffle)

    @Provides @ActivityScope @Named(DealCardsUiComponent.CARDS_LEFT)
    fun cardsLeftHint(): TextView = activity.findViewById(R.id.dealCardsUi_cardsRemaining_textView) as TextView

    @Provides @ActivityScope @Named(DealCardsUiComponent.PROGRESS_BAR)
    fun progressBar(): ProgressBar = activity.findViewById(R.id.dealCardsUi_progressBar_loading) as ProgressBar

    @Provides @ActivityScope @Named(DealCardsUiComponent.DEAL_CARDS_LAYOUT)
    fun dealCardsUiLayout(): ViewGroup = activity.findViewById(R.id.dealCardsUi) as ViewGroup

    @Provides @ActivityScope
    fun dealCardsUi(): DealCardsUi = activity

    @Provides @ActivityScope
    fun dealCardsUiActions(): DealCardsUi.Actions = activity

    @Provides @ActivityScope
    fun dealCardsUiIntentions(): DealCardsUi.Intentions = activity

    @Provides @ActivityScope @Named(DealCardsUiComponent.ERROR)
    fun error(): TextView = activity.findViewById(R.id.dealCardsUi_error) as TextView
}

//    @Provides @ActivityScope
//    fun main(): Scheduler {
//
//    }
//
//    @Provides @ActivityScope
//    fun disk(): Scheduler {
//
//    }
//
//    @Provides @ActivityScope
//    fun comp(): Scheduler {
//
//    }


//    class Dependencies(
//            val activity: DealCardsActivity,
//            val dealer: Dealer
//    ) : DealCardsUiComponent {
//
//        override fun injectMembers(p0: DealCardsActivity?) {
//            throw UnsupportedOperationException("not implemented")
//        }
//
//        override val dealCardClicks: Relay<Unit> by lazy { PublishRelay.create<Unit>() }
//        override val shuffleDeckClicks: Relay<Unit> by lazy { PublishRelay.create<Unit>() }
//
//        override val newDeckRequests: Relay<Unit> by lazy { PublishRelay.create<Unit>() }
//        override val cards: CardsRecycler by lazy {
//            val recycler = activity.findViewById(R.id.cards_recycler) as RecyclerView
//            return@lazy CardsRecycler(recycler, dealCardClicks)
//        }
//        override val presenter by lazy {
//            DealCardsPresenter(activity, activity, activity, dealer)
//        }
//        override val cardsLeftHint: TextView by lazy {
//            activity.findViewById(R.id.dealCardsUi_cardsRemaining_textView) as TextView
//        }
//        override val shuffleButton: View by lazy {
//            activity.findViewById(R.id.button_shuffle)
//        }
//        override val newDeckButton: View by lazy {
//            activity.findViewById(R.id.button_new_deck)
//        }
//
//        override val progressBar: ProgressBar by lazy {
//            activity.findViewById(R.id.dealCardsUi_progressBar_loading) as ProgressBar
//        }
//
//        override val dealCardsUi: ViewGroup by lazy {
//            activity.findViewById(R.id.dealCardsUi) as ViewGroup
//        }
//
//        override val error: TextView by lazy {
//            activity.findViewById(R.id.dealCardsUi_error) as TextView
//        }
//
//        override val main: Scheduler by lazy { AndroidSchedulers.mainThread() }
//        override val disk: Scheduler by lazy { Schedulers.io() }
//        override val comp: Scheduler by lazy {
//            //            Schedulers.computation()
//            main
//        }
//
//        override val renderer by lazy { DealCardsUi.Renderer(activity, main = main, comp = comp) }
//
//    }