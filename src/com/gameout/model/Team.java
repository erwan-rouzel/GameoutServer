package com.gameout.model;

/**
 * Created by erwan on 14/11/2015.
 */
public class Team extends GameObject {
    public byte id;
    public GameState parentGameState;
    public Player[] players;
    public byte score;

    public Team(byte id, GameState parentGameState, int numberOfPlayers) {
        this.id = id;
        this.parentGameState = parentGameState;
        this.players = new Player[numberOfPlayers];

        for(byte i = 0; i < numberOfPlayers; i++) {
            this.players[i] = new Player(this, i);
        }
    }

    public void incrementScore() {
        score++;
    }

    public void initScore() {
        score = 0;
    }

    @Override
    public String toString() {
        return "{score=" + score + "}";
    }
}
