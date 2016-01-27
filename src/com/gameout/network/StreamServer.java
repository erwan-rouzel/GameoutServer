package com.gameout.network;

import com.gameout.model.*;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

/**
 * Created by erwan on 21/11/2015.
 */
public class StreamServer extends AbstractServer implements Runnable {
    private long timePrevious;
    public static int counter = 0;
    public StreamServer(int port) {
        super(port);
    }

    public void run() {
        DatagramPacket sendPacket;
        DatagramSocket udpSocket;
        byte[] udpData = new byte[128];
        byte[] gameStateMessage;

        try {
            udpSocket = new DatagramSocket(port);
        } catch(SocketException e) {
            log(e);
            System.exit(0);
            return;
        }

        log(this.getClass().getSimpleName() + " now listening on port " + port + "...");

        while(true) {
            /* Process UDP Packets */
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                log(e);
            }

            try {
                for(Map.Entry<Integer, GameState> entry : GameoutServer.gameStateList.entrySet()) {
                    Integer key = entry.getKey();
                    GameState gameState = entry.getValue();

                    updateGameState(gameState);

                    /*
                    gameStateMessage = GameoutServerHelper.getGameStateMessage(gameState);

                    for(Team teams: gameState.teams) {
                        for(Player player: teams.players) {

                            if(player.ip != null) {
                                sendPacket = new DatagramPacket(
                                        gameStateMessage,
                                        gameStateMessage.length,
                                        player.ip,
                                        this.port
                                );

                                log("Sending game state to " + player.ip.toString() + " - ball=(" + gameState.ball.x + ", " + gameState.ball.y + ")");

                                udpSocket.send(sendPacket);
                            }
                        }
                    }
                    */
                }

            } catch (Exception e) {
                log(e);
            }
        }
    }

    public static void updateGameState(GameState gameState) {
        Ball ball = gameState.ball;
        Player pBottom = gameState.teams[0].players[0];
        //Player pTop = gameState.teams[0].players[0];

        ball.x += ball.vx;
        ball.y += ball.vy;

        //TODO : ball.x < ball.radX
        //et utiliser valeur absolue au lieu du *-1
        if(     (ball.x > HVPoint.WIDTH_REF) ||
                (ball.x < 0) ) {
            ball.vx *= -1;
        }

        if(     (ball.y < 0) ) {
            ball.vy *= -1;
        }

        /*
        if(ball.y > pBottom.y) {
            if(     (ball.x >= pBottom.x - pBottom.WIDTH/2) &&
                    (ball.x <= pBottom.x + pBottom.WIDTH/2) ) {
                ball.vy *= -1;
            }
        }
        */

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
            // incrementons le score, la balle a été envoyée
            //CurPfp.pfp.incScoreBidon();
            // agmentons la vitesse de la balle lorsque le score augmente
            //CurPfp.pfp.balleRegleVitesse();
        }

        if(ball.y > HVPoint.HEIGHT_REF) {
            // Player bottom lose

            ball.x = HVPoint.WIDTH_REF / 2;
            ball.y = HVPoint.HEIGHT_REF / 2;
        }

        //state.ball.y = (short) (Math.round(Math.sin(counter/10.0)*3000.0) + 5000);

        counter++;
    }
}
