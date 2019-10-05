/*
 *
 *  //-----------------------------------------------------------------------------
 *  // Copyright 2019 by Resideo Inc.  All rights reserved.
 *  //-----------------------------------------------------------------------------
 *  *****************************************************************************
 *     Created By      : H144579
 *     Created On      : 27/5/19 6:54 PM
 *  ******************************************************************************
 * /
 *
 */

package com.hrh.customseekbarui;

import android.graphics.drawable.Drawable;

public class TickMark {

    private final Drawable mTickMarkEnabled;
    private final Drawable mTickMarkDisabled;
    int mAction;

    public TickMark(Drawable enabled, Drawable disabled, int action) {
        mTickMarkEnabled = enabled;
        mTickMarkDisabled = disabled;
        mAction = action;
    }

    public Drawable getmTickMarkEnabled() {
        return mTickMarkEnabled;
    }

    public Drawable getTickMarkDisabled() {
        return mTickMarkDisabled;
    }

    public int getAction() {
        return mAction;
    }

    @Override
    public String toString() {
        return "TickMark{" +
                "mTickMarkEnabled=" + mTickMarkEnabled +
                ", mTickMarkDisabled=" + mTickMarkDisabled +
                ", mAction=" + mAction +
                '}';
    }
}
