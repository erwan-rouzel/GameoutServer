package com.gameout.model;

import com.gameout.game.GameLogicManager;
import com.gameout.game.IGameLogic;

/**
 * Created by erwan on 14/11/2015.
 */
public class GameState extends GameObject {
    public int id;
    public String roomId;
    public IGameLogic gameLogic;
    public Ball ball;
    public Team[] teams;
    public byte status;
    public int numberOfRegisteredPlayers;

    public GameState(GameSession session) {
        this.numberOfRegisteredPlayers = 0;
        this.id = session.id;
        this.roomId = session.roomId;
        this.ball = new Ball();
        this.gameLogic = GameLogicManager.getGameLogicByType(session.gameType);
        this.teams = new  Team[3];
        this.teams[0] = new Team((byte)0, this, session.numberOfPlayersInTeam1);
        this.teams[1] = new Team((byte)1, this, session.numberOfPlayersInTeam2);
        this.teams[2] = new Team((byte)2, this, session.numberOfPlayersInTeam3);

        if(session.gameType == GameType.PONG_MONO) {
            this.status = GameStatus.RUNNING;
        } else if(session.gameType == GameType.PONG_MULTI) {
            this.status = GameStatus.INITIALIZING;
        }
    }

    public void update() {
        this.gameLogic.update(this);
    }

    public void registerPlayer() {
        numberOfRegisteredPlayers++;
    }

    public boolean allPlayersRegistered() {
        return (numberOfRegisteredPlayers == getTotalNumberOfPlayers());
    }

    public int getTotalNumberOfPlayers() {
        return (teams[0].players.length + teams[1].players.length + teams[2].players.length);
    }
}