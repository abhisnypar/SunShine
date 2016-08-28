package com.example.abhim.sunshine.Utils;

import junit.framework.Assert;

import java.util.concurrent.Callable;

/**
 * Created by abhim on 8/23/2016.
 *
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Note: This file copied from the Android CTS Tests
 */

public abstract class PollingCheck {
    private static final long TIME_SLICE = 50;
    private long mTimeOut = 3000;

    public PollingCheck(){
    }

    public PollingCheck(long timeOut){
        mTimeOut = timeOut;
    }

    protected abstract boolean check();

    public void run() {
        if (check()) {
            return;
        }


        long timeOut = mTimeOut;

        while (timeOut > 0) {
            try {
                Thread.sleep(TIME_SLICE);
            } catch (InterruptedException e) {
                Assert.fail("UnExpected Interrupted Exception");
            }

            if (check()) {
                return;
            }
            timeOut -= TIME_SLICE;
        }
        Assert.fail("unexpected timeout");
    }

    public void check(CharSequence message, long timeOut, Callable<Boolean> condition) throws Exception{

        while (timeOut>0){
            if (condition.call()){
                return;
            }

            Thread.sleep(TIME_SLICE);
            timeOut -=TIME_SLICE;
        }
        Assert.fail(message.toString());
    }
}
