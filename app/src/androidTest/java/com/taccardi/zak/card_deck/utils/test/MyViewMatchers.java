package com.taccardi.zak.card_deck.utils.test;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.core.StringContains;

import android.support.annotation.NonNull;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.view.View;
import android.widget.TextView;

import static android.support.test.internal.util.Checks.checkNotNull;

/**
 * Created by zak.taccardi on 3/21/17.
 */

public class MyViewMatchers {


    public static Matcher<View> withPartialText(@NonNull final String string) {
        checkNotNull(string);

        final Matcher<String> stringMatcher = new StringContains(string);
        return new BoundedMatcher<View, TextView>(TextView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("with text: ");
                stringMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(TextView textView) {
                return stringMatcher.matches(textView.getText().toString());
            }
        };
    }

    public static Matcher<View> withPartialHtml(@NonNull final String string) {
        checkNotNull(string);

        final Matcher<String> stringMatcher = new StringContains(string);
        return new BoundedMatcher<View, TextView>(TextView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("with text: ");
                stringMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(TextView textView) {
                return stringMatcher.matches(textView.getText().toString());
            }
        };
    }
}
