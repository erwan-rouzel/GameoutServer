package com.gameout.model;

/**
 * Created by erwan on 14/11/2015.
 */
public class Team {
    public GameState parentGameState;
    public Player[] players;
    public byte score;

    public Team(GameState parentGameState, int numberOfPlayers) {
        this.parentGameState = parentGameState;
        this.players = new Player[numberOfPlayers];
    }
}
