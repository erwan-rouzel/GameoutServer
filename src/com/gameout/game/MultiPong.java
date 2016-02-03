package com.gameout.game;

import com.gameout.model.*;

/**
 * Created by erwanrouzel on 31/01/2016.
 */
public class MultiPong implements IGameLogic {
    public static final int gameType = GameType.PONG_MONO;
    public static final int SCORE_MAX = 5;

    @Override
    public void update(GameState gameState) {
        Ball ball = gameState.ball;
        Player pBottom = gameState.teams[0].players[0];
        Player pTop = gameState.teams[1].players[0];

        ball.x += ball.vx;
        ball.y += ball.vy;

        // Rebond sur les murs
        if(ball.x + ball.rx > HVPoint.WIDTH_REF) {
            ball.vx = (short) -Math.abs(ball.vx);
        } else if (ball.x - ball.rx < 0) {
            ball.vx = (short) Math.abs(ball.vx);
        }

        // Rebond sur la raquette du haut
        if ( ! (
                ( ball.vy > 0 )
                        || ((ball.y + ball.ry) < (pTop.y - pTop.ry))
                        || ((ball.y - ball.ry) > (pTop.y + pTop.ry))
                        || ((ball.x + ball.rx) < (pTop.x - pTop.rx))
                        || ((ball.x - ball.rx) > (pTop.y + pTop.ry))
        )   )
        {
            ball.vy = (short) Math.abs(ball.vy);
            ball.updateSpeed(pTop.vx, pTop.vy);
        }

        // Rebond sur la raquette du bas
        if ( ! (
                ( ball.vy < 0 )
                        || ((ball.y + ball.ry) < (pBottom.y - pBottom.ry))
                        || ((ball.y - ball.ry) > (pBottom.y + pBottom.ry))
                        || ((ball.x + ball.rx) < (pBottom.x - pBottom.rx))
                        || ((ball.x - ball.rx) > (pBottom.y + pBottom.ry))
        )   )
        {
            ball.vy = (short) -Math.abs(ball.vy);
            ball.updateSpeed(pBottom.vx, pBottom.vy);
        }

        // Player top lose
        if(ball.y - ball.ry < 0) {
            gameState.teams[0].incrementScore();
            ball.initSpeed();
            ball.initPosition();
        }

        // Player bottom lose
        if(ball.y > HVPoint.HEIGHT_REF) {
            gameState.teams[1].incrementScore();
            ball.initSpeed();
            ball.initPosition();
        }

        // L'une des deux équipes a dépassé le score max => on termine la partie
        if(gameState.teams[0].score == SCORE_MAX || gameState.teams[1].score == SCORE_MAX) {
            gameState.status = GameStatus.FINISHED;
        }
    }
}
