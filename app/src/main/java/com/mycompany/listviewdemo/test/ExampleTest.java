package com.mycompany.listviewdemo.test;

import android.test.InstrumentationTestCase;

/**
 * Created by VIanoshchuk on 16.03.2015.
 */
public class ExampleTest extends InstrumentationTestCase {
    public void test() throws Exception {
        final int expected = 5;
        final int reality = 5;
        assertEquals(expected, reality);
    }
}