package com.taccardi.zak.card_deck

import android.content.Context
import android.support.annotation.LayoutRes
import android.support.v7.util.DiffUtil
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.jakewharton.rxrelay2.Relay
import com.taccardi.zak.card_deck.CardsRecycler.Item.UiCard
import com.taccardi.zak.card_deck.CardsRecycler.Item.UiDeck
import com.taccardi.zak.library.pojo.Card
import com.taccardi.zak.library.pojo.Deck
import com.taccardi.zak.library.pojo.Suit

/**
 * Delegate for the recyclerview in [DealCardsUi] that displays the cards that were dealt.
 *
 * @property recyclerView the view itself
 * @property deckClicks relay that should emit when the user clicks on the deck (to deal a card)
 */
class CardsRecycler(
        val recyclerView: RecyclerView,
        private val deckClicks: Relay<Unit>
) {

    private val adapter = Adapter(deckClicks)


    init {
        recyclerView.layoutManager = NoScrollLinearLayoutManager(recyclerView.context, RecyclerView.HORIZONTAL, false)
                .also {

                }
        recyclerView.adapter = adapter

    }

    fun showCardsDealt(cards: List<Item>) {
        adapter.showCardsDealt(cards)
    }

    private class NoScrollLinearLayoutManager(context: Context, orientation: Int, reverseLayout: Boolean) : LinearLayoutManager(context, orientation, reverseLayout) {
        override fun canScrollHorizontally() = false
        override fun canScrollVertically() = false
    }


    private class Adapter(private val deckClicks: Relay<Unit>) : RecyclerView.Adapter<UiViewHolder<Item>>() {

        private var items: List<Item> = emptyList()


        override fun getItemCount(): Int {
            return items.size
        }

        override fun onBindViewHolder(holder: UiViewHolder<Item>, position: Int) {
            holder.bind(items[position])
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UiViewHolder<Item> {
            val inflater = LayoutInflater.from(parent.context)
            val type = UiViewHolder.ViewType.of(viewType)
            val view = inflater.inflate(type.layoutId, parent, false)

            return UiViewHolder.create(view, UiViewHolder.ViewType.of(viewType), deckClicks)
        }


        override fun getItemViewType(position: Int): Int {
            return items[position].viewType
        }

        fun showCardsDealt(cards: List<Item>) {
            this.items = cards
            notifyDataSetChanged()
        }

        fun showDeck(diff: RecyclerViewBinding<Item>) {
            this.items = diff.new
            diff.diff.dispatchUpdatesTo(this)
        }
    }

    private class CardItemAnimator : DefaultItemAnimator() {

        override fun canReuseUpdatedViewHolder(viewHolder: RecyclerView.ViewHolder, payloads: List<Any>): Boolean {
            when (viewHolder) {
                is UiViewHolder.DeckHolder -> {
                    return true
                }
            }

            return super.canReuseUpdatedViewHolder(viewHolder, payloads)
        }
    }


    private sealed class UiViewHolder<in I>(val itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(item: I)

        /**
         * View holder for cards
         */
        class CardHolder(itemView: View) : UiViewHolder<UiCard>(itemView) {

            val suitRanks: Array<TextView> by lazy {
                arrayOf(
                        itemView.findViewById(R.id.dealCardsUi_bottomRight_suitRank) as TextView,
                        itemView.findViewById(R.id.dealCardsUi_topLeft_suitRank) as TextView
                )
            }

            val centerSuit: TextView by lazy { itemView.findViewById(R.id.dealCardsUi_center_suit) as TextView }

            override fun bind(item: UiCard) {
                suitRanks.forEach { suitRank ->
                    suitRank.text = item.card.symbolHtml()
                }
                centerSuit.text = item.card.suit.symbolHtml()
            }

        }

        class DeckHolder(itemView: View, private val deckClicks: Relay<Unit>) : UiViewHolder<UiDeck>(itemView) {
            init {
                itemView.setOnClickListener { deckClicks.accept(Unit) }
            }

            override fun bind(item: UiDeck) {
                //no bind needed
            }
        }

        companion object {
            @Suppress("UNCHECKED_CAST")
            fun create(itemView: View, viewType: UiViewHolder.ViewType, deckClicks: Relay<Unit>): UiViewHolder<Item> = when (viewType) {
                ViewType.CARD -> CardHolder(itemView) as UiViewHolder<Item>
                ViewType.DECK -> DeckHolder(itemView, deckClicks) as UiViewHolder<Item>
            }
        }

        enum class ViewType(@LayoutRes val layoutId: Int) {
            CARD(R.layout.item_deal_cards_ui_card),
            DECK(R.layout.item_deal_cards_ui_deck);

            companion object {
                //don't want to allocate a defensive copy every time
                val VALUES by lazy { values() }

                fun of(layoutId: Int): ViewType {
                    val match = VALUES.firstOrNull { it.layoutId == layoutId }
                    match?.let { return match }

                    throw EnumConstantNotPresentException(ViewType::class.java, "could not find view type for $layoutId")
                }
            }
        }
    }

    /**
     * An item in the [CardsRecycler]View.
     */
    sealed class Item {
        abstract val layoutId: @param:LayoutRes Int
        val viewType get() = layoutId

        /** @see DiffUtil.Callback.areItemsTheSame */
        abstract fun isItemSame(new: Item): Boolean

        /** @see DiffUtil.Callback.areContentsTheSame */
        abstract fun isContentSame(new: Item): Boolean

        /**
         * The UI representation of a [Card]
         */
        data class UiCard(val card: Card) : Item() {

            override fun isContentSame(new: Item): Boolean = when (new) {
                is UiCard -> this == new
                UiDeck -> false
            }

            override fun isItemSame(new: Item): Boolean = when (new) {
                is UiCard -> this == new
                UiDeck -> false
            }

            override val layoutId = R.layout.item_deal_cards_ui_card

        }

        /**
         * The UI representation of a [Deck].
         */
        object UiDeck : Item() {
            override fun isItemSame(new: Item) = when (new) {
                is UiCard -> false
                UiDeck -> true //deck is always same
            }

            override fun isContentSame(new: Item): Boolean = when (new) {
                is UiCard -> false
                UiDeck -> true //deck is always same
            }

            override val layoutId = R.layout.item_deal_cards_ui_deck
        }

    }

    fun showDeck(diff: RecyclerViewBinding<Item>) {
        this.adapter.showDeck(diff)
    }

}

fun Card.symbolHtml(): Spanned {
    //TODO use non-deprecated version
    return Html.fromHtml("${rank.intDef}<br />${suit.symbol}")
}

fun Suit.symbolHtml(): Spanned {
    //TODO use non-deprecated version
    return Html.fromHtml(symbol)
}


