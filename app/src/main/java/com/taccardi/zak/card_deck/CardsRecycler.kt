package com.taccardi.zak.card_deck

import android.support.annotation.LayoutRes
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.taccardi.zak.library.pojo.Card
import com.taccardi.zak.library.pojo.Suit

/**
 * Delegate for the recyclerview in [DealCardsUi] that displays the cards that were dealt.
 */
class CardsRecycler(
        private val recyclerView: RecyclerView
) {

    private val adapter by lazy {
        val adapter = Adapter()
        recyclerView.adapter = adapter
        adapter
    }

    init {
        recyclerView.layoutManager = LinearLayoutManager(recyclerView.context, RecyclerView.HORIZONTAL, false)
    }

    fun showCardsDealt(cards: List<Item>) {
        adapter.showCardsDealt(cards)
    }


    private class Adapter : RecyclerView.Adapter<UiViewHolder<Item>>() {

        private var items: List<Item> = emptyList()


        override fun getItemCount(): Int {
            return items.size
        }

        override fun onBindViewHolder(holder: UiViewHolder<in Item>, position: Int) {
            holder.bind(items[position])
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UiViewHolder<Item> {
            val inflater = LayoutInflater.from(parent.context)
            val type = UiViewHolder.ViewType.of(viewType)
            val view = inflater.inflate(type.layoutId, parent, false)

            return UiViewHolder.create(view, UiViewHolder.ViewType.of(viewType))
        }


        override fun getItemViewType(position: Int): Int {
            return items[position].viewType
        }

        fun showCardsDealt(cards: List<Item>) {
            this.items = cards
            notifyDataSetChanged()
        }
    }


    private sealed class UiViewHolder<in I>(val itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(item: I)

        /**
         * View holder for cards
         */
        class CardHolder(itemView: View) : UiViewHolder<Item.UiCard>(itemView) {

            val suitRanks: Array<TextView> by lazy {
                arrayOf(
                        itemView.findViewById(R.id.dealCardsUi_bottomRight_suitRank) as TextView,
                        itemView.findViewById(R.id.dealCardsUi_topLeft_suitRank) as TextView
                )
            }

            val centerSuit: TextView by lazy { itemView.findViewById(R.id.dealCardsUi_center_suit) as TextView }

            override fun bind(item: Item.UiCard) {
                suitRanks.forEach { suitRank ->
                    suitRank.text = item.card.symbolHtml()
                }
                centerSuit.text = item.card.suit.symbolHtml()
            }

        }
        class DeckHolder(itemView: View) : UiViewHolder<Item.UiDeck>(itemView) {
            override fun bind(item: Item.UiDeck) {
                //no binding needed
            }
        }

        companion object {
            @Suppress("UNCHECKED_CAST")
            fun create(itemView: View, viewType: UiViewHolder.ViewType): UiViewHolder<Item> = when (viewType) {
                ViewType.CARD -> CardHolder(itemView) as UiViewHolder<Item>
                ViewType.DECK -> DeckHolder(itemView) as UiViewHolder<Item>
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

    sealed class Item {
        abstract val layoutId: Int
        val viewType get() = layoutId

        data class UiCard(val card: Card) : Item() {
            override val layoutId = R.layout.item_deal_cards_ui_card

        }

        object UiDeck : Item() {
            override val layoutId = R.layout.item_deal_cards_ui_deck
        }
    }
}

fun Card.symbolHtml(): Spanned {
    return Html.fromHtml("${rank.intDef}<br />${suit.symbol}")
}

fun Suit.symbolHtml(): Spanned {
    return Html.fromHtml(symbol)
}


