package com.taccardi.zak.card_deck

/**
 * Responsible for rendering a view's state.
 */
interface StateRenderer<VS> {

    /**
     * Accepts a pojo representing the current state of the view in order to render it on to the screen of the user.
     * @param viewState state to render
     */
    fun render(viewState: VS)
}