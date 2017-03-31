package com.taccardi.zak.library;

import org.junit.Test;

import android.support.test.filters.LargeTest;
import android.support.test.filters.MediumTest;
import android.support.test.filters.SmallTest;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleInstrumentedTest {

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
