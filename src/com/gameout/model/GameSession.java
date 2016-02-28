package com.gameout.model;

public class GameSession extends GameObject {
    public int id;
    public String roomId;
    public int timestamp;
    public byte gameType;
    public String serverHostName;
    public int numberOfPlayersInTeam1;
    public int numberOfPlayersInTeam2;
    public int numberOfPlayersInTeam3;
}