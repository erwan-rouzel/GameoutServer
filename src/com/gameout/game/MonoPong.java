package com.gameout.game;

import com.gameout.model.*;

/**
 * Created by erwanrouzel on 31/01/2016.
 */
public class MonoPong implements IGameLogic {
    public static final int gameType = GameType.PONG_MONO;

    @Override
    public void update(GameState gameState) {
        Ball ball = gameState.ball;
        Player pBottom = gameState.teams[0].players[0];
        //Player pTop = gameState.teams[0].players[0];

        ball.x += ball.vx;
        ball.y += ball.vy;

        if(ball.x + ball.rx > HVPoint.WIDTH_REF) {
            ball.vx = (short) -Math.abs(ball.vx);
        } else if (ball.x - ball.rx < 0) {
            ball.vx = (short) Math.abs(ball.vx);
        }

        if(ball.y - ball.ry < 0) {
            ball.vy = (short) Math.abs(ball.vy);
        }

        // Rebond sur la raquette
        if ( ! (
                ( ball.vy < 0 )
                        || ((ball.y + ball.ry) < (pBottom.y - pBottom.ry))
                        || ((ball.y - ball.ry) > (pBottom.y + pBottom.ry))
                        || ((ball.x + ball.rx) < (pBottom.x - pBottom.rx))
                        || ((ball.x - ball.rx) > (pBottom.y + pBottom.ry))
        )   )
        {
            ball.vy = (short) -Math.abs(ball.vy);
            gameState.teams[0].incrementScore();
            ball.updateSpeed(pBottom.vx, pBottom.vy);
        }

        if(ball.y > HVPoint.HEIGHT_REF) {
            // Player bottom lose
            gameState.teams[0].initScore();
            ball.initSpeed();
            ball.initPosition();

            gameState.status = GameStatus.FINISHED;
        }
    }
}
