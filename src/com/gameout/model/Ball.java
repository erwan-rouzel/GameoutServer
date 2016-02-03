package com.gameout.model;

import com.gameout.utils.GameoutUtils;

/**
 * Created by erwan on 14/11/2015.
 */
public class Ball extends GameObject {
    private static short SPEED_COEFF = 10;
    public short x;
    public short y;
    public short vx;
    public short vy;
    public short rx;
    public short ry;

    public Ball() {
        initSpeed();
        initPosition();
        initRadius();
    }

    public void updateSpeed(int speedPlayerX, int speedPlayerY) {
        vx += vx * speedPlayerX * (SPEED_COEFF + GameoutUtils.randomWithRange(0, 20))/100;
        vy += vy * speedPlayerY * (SPEED_COEFF + GameoutUtils.randomWithRange(0, 20))/100;
    }

    public void initSpeed() {
        vx = (short) (10 + GameoutUtils.randomWithRange(0, 5));
        vy = (short) (15 + GameoutUtils.randomWithRange(0, 5));
    }

    public void initPosition() {
        x = HVPoint.WIDTH_REF / 2;
        y = HVPoint.HEIGHT_REF / 2;
    }

    public void initRadius() {
        rx = HVPoint.WIDTH_REF / 80;
        ry = HVPoint.WIDTH_REF / 80;
    }
}
