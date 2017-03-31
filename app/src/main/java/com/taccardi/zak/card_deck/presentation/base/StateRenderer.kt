package com.taccardi.zak.card_deck.presentation.base

/**
 * Responsible for rendering a view's state.
 */
interface StateRenderer<in VS> {

    /**
     * Accepts a pojo representing the current state of the view in order to render it on to the screen of the user.
     * @param state state to render
     */
    fun render(state: VS)
}