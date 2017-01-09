/*
 * Copyright © 2011-2016 HERE Global B.V. and its affiliate(s).
 * All rights reserved.
 * The use of this software is conditional upon having a separate agreement
 * with a HERE company for the use or utilization of this software. In the
 * absence of such agreement, the use of the software is not allowed.
 */

package com.here.android.tutorial;

public class ARRadarDot {

    private boolean dimmed;
    private double translateX;
    private double translateY;

    public ARRadarDot(double translateX, double translateY,boolean dimmed) {
        super();
        this.dimmed = dimmed;
        this.translateX = translateX;
        this.translateY = translateY;
    }

    public boolean getDimmed() {
        return dimmed;
    }

    public void setDimmed(boolean dimmed) {
        this.dimmed = dimmed;
    }

    public double getTranslateX() {
        return translateX;
    }

    public void setTranslateX(double translateX) {
        this.translateX = translateX;
    }

    public double getTranslateY() {
        return translateY;
    }

    public void setTranslateY(double translateY) {
        this.translateY = translateY;
    }
}
