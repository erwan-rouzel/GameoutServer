package com.gameout.game;

import com.gameout.model.*;
import com.gameout.network.LogHelper;

/**
 * Created by erwanrouzel on 31/01/2016.
 */
public class MonoPong implements IGameLogic {
    public static final int gameType = GameType.PONG_MONO;
    private static final int MAX_NUM_BALLS = 3;
    private int numBalls;

    public MonoPong() {
        numBalls = 0;
    }


    @Override
    public void update(GameState gameState) {
        Ball ball = gameState.ball;

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

        // Rebonds sur les raquettes

        for(int i = 0; i < gameState.teams[0].players.length; i++) {
            Player p = gameState.teams[0].players[i];
            if (!(
                    (ball.vy < 0)
                            || ((ball.y + ball.ry) < (p.y - p.ry))
                            || ((ball.y - ball.ry) > (p.y + p.ry))
                            || ((ball.x + ball.rx) < (p.x - p.rx))
                            || ((ball.x - ball.rx) > (p.x + p.rx))
            )) {
                //LogHelper.log("REBOND PLAYER", "p=" + p.x + "," + p.y + "; r=" + p.rx + "," + p.ry);
                //LogHelper.log("REBOND BALL", "b=" + ball.x + "," + ball.y + "; r=" + ball.rx + "," + ball.ry);
                ball.vy = (short) -Math.abs(ball.vy);
                gameState.teams[0].incrementScore();
                ball.updateSpeed(p.vx, p.vy);
            }
        }

        if(ball.y > HVPoint.HEIGHT_REF) {
            // Player bottom lose
            // Avoir 3 balles, et à chaque fois mettre à jour le high score
            // (le high score est mis dans le score de l'équipe 2, le max dans l'équipe 1 et la somme dans l'équipe 3)
            gameState.teams[1].score = gameState.teams[0].score;
            gameState.teams[0].initScore();

            if(numBalls >= MAX_NUM_BALLS) {
                gameState.status = GameStatus.FINISHED;
            } else {
                numBalls++;
                ball.initSpeed();
                ball.initPosition();
            }
        }
    }
}
