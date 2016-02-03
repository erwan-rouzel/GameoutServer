package com.gameout.game;

import com.gameout.model.GameType;

/**
 * Created by erwanrouzel on 31/01/2016.
 */
public class GameLogicManager {

    public static IGameLogic getGameLogicByType(byte gameType) {
        switch (gameType) {
            case GameType.PONG_MONO:
                return new MonoPong();
            case GameType.PONG_MULTI:
                return new MultiPong();
        }

        return null;
    }
}
