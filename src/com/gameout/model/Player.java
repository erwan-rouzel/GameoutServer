package com.gameout.model;

import com.gameout.network.GameoutUtils;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.util.ArrayList;

/**
 * Created by erwan on 14/11/2015.
 */

public class Player extends GameObject {
    public static int WIDTH = 1000;
    public byte id;
    public Team parentTeam;
    public InetAddress ip;
    public PlayerType type;
    public PlayerState state;
    public short x;
    public short y;
    public short vx;
    public short vy;
    public short rx;
    public short ry;


    public Player(Team parentTeam, byte id) {
        this.id = id;
        this.parentTeam = parentTeam;
        ip = null;
        type = PlayerType.Guest;
        state = PlayerState.Active;
        x = 0;
        y = 0;
        vx = 1;
        vy = 1;
        rx = HVPoint.WIDTH_REF / 16;
        ry = HVPoint.HEIGHT_REF / 80;
    }
}