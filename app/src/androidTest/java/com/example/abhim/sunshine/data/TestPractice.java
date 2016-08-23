package com.example.abhim.sunshine.data;

import android.test.AndroidTestCase;

/**
 * Created by abhim on 7/31/2016.
 */
public class TestPractice extends AndroidTestCase {

    /*
           This gets run before the test
     */

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testThatDemonstrateAssertions() throws Throwable{
        int a = 5;
        int b = 3;
        int c = 5;
        int d = 10;

        assertEquals("X should be equal", a, c);
        assertEquals("Y should be true",d > a);
        assertEquals("Z should be false", a ==b );

        if (b>d){
            fail("Xy should never happen");
        }
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
