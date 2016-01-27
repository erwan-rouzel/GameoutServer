package com.gameout.model;

/**
 * Created by erwan on 14/11/2015.
 */
public class Ball extends GameObject {
    public short x;
    public short y;
    public short vx;
    public short vy;
    public short rx;
    public short ry;

    public Ball() {
        x = HVPoint.WIDTH_REF / 2;
        y = HVPoint.HEIGHT_REF / 2;
        vx = 20;
        vy = 30;
        rx = HVPoint.WIDTH_REF / 80;
        ry = HVPoint.WIDTH_REF / 80;
    }
}
