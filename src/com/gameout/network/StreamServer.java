package com.gameout.network;

import com.gameout.model.GameState;
import com.gameout.model.Player;
import com.gameout.model.Team;

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
                Thread.sleep(100);
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

    public static void updateGameState(GameState state) {
        state.ball.x = (short) (Math.round(Math.cos(counter/10.0)*3000.0) + 5000);
        state.ball.y = (short) (Math.round(Math.sin(counter/10.0)*3000.0) + 5000);

        counter++;
    }
}
