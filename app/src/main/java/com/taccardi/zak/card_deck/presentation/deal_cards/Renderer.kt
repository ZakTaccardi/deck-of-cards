package com.taccardi.zak.card_deck.presentation.deal_cards

import android.support.v7.util.DiffUtil
import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import com.taccardi.zak.card_deck.presentation.base.StateRenderer
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable

class DealCardsUiRenderer(
        val uiActions: DealCardsUi.Actions,
        val main: Scheduler,
        val comp: Scheduler
) : StateRenderer<DealCardsUi.State> {

    val disposables = CompositeDisposable()

    val state: Relay<DealCardsUi.State> = PublishRelay.create<DealCardsUi.State>().toSerialized()

    init {
        start()
    }

    fun start() {
        disposables += state
                .map { it.remaining }
                .distinctUntilChanged()
                .subscribeOn(comp)
                .observeOn(main)
                .subscribe { uiActions.showRemainingCards(it) }

        disposables += state
                .map { it.dealt }
                .distinctUntilChanged()
                .map { it.map { CardsRecycler.Item.UiCard(it) } }
                .map { cards ->
                    val list = ArrayList<CardsRecycler.Item>(cards.size + 1)
                    list.add(CardsRecycler.Item.UiDeck)
                    list.addAll(cards)
                    @Suppress("USELESS_CAST")
                    list as List<CardsRecycler.Item>
                }
                .scanMap(
                        emptyList<CardsRecycler.Item>(),
                        { old: List<CardsRecycler.Item>, new: List<CardsRecycler.Item> -> calculateDiff(old, new) }
                )
                .subscribeOn(comp)
                .observeOn(main)
                .subscribe { diff ->
                    uiActions.showDeck(diff)
                }

        disposables += state
                .map { it.isLoading }
                .distinctUntilChanged()
                .observeOn(comp)
                .observeOn(main)
                .subscribe { isLoading ->
                    uiActions.showLoading(isLoading)
                    uiActions.disableButtons(disable = isLoading)
                }

        disposables += state
                .mapNullable { it.error }
                .distinctUntilChanged()
                .subscribeOn(comp)
                .observeOn(main)
                .subscribe {
                    val error = it.value
                    if (error == null) {
                        uiActions.hideError()
                    } else {
                        uiActions.showError(error)
                    }
                }

    }

    override fun render(state: DealCardsUi.State) {
        this.state.accept(state)
    }

    fun stop() {
        disposables.clear()
    }


    companion object {
        fun calculateDiff(old: List<CardsRecycler.Item>, new: List<CardsRecycler.Item>): RecyclerViewBinding<CardsRecycler.Item> {
            val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    val oldItem = old[oldItemPosition]
                    val newItem = new[newItemPosition]
                    return oldItem.isItemSame(newItem)
                }

                override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    val oldItem = old[oldItemPosition]
                    val newItem = new[newItemPosition]
                    return oldItem.isContentSame(newItem)
                }

                override fun getOldListSize(): Int = old.size

                override fun getNewListSize(): Int = new.size

            })

            return RecyclerViewBinding(new = new, diff = diff)
        }
    }
}