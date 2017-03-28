package com.taccardi.zak.library;

import org.junit.Test;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.filters.MediumTest;
import android.support.test.filters.SmallTest;

import static org.junit.Assert.assertEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleInstrumentedTest {

    @Test
    @MediumTest
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.taccardi.zak.library.test", appContext.getPackageName());
    }

    @Test
    @SmallTest
    public void smallTestStub() throws Exception {
        //prevents no test found error when filtering
    }

    @Test
    @MediumTest
    public void mediumTestStub() throws Exception {
        //prevents no test found error when filtering
    }

    @Test
    @LargeTest
    public void largeTestStub() throws Exception {
        //prevents no test found error when filtering
    }
}
