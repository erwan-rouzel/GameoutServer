package com.gameout.game;

import com.gameout.model.GameState;

/**
 * Created by erwanrouzel on 31/01/2016.
 */
public interface IGameLogic {
    public static final int gameType = -1;
    public void update(GameState gameState);
}
