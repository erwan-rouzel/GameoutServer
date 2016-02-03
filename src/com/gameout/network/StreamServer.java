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
    public StreamServer(int port) {
        super(port);
    }

    public void run() {
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
                    //Integer key = entry.getKey();
                    GameState gameState = entry.getValue();

                    if(gameState.status == GameStatus.RUNNING) {
                        gameState.update();
                    }
                }

            } catch (Exception e) {
                log(e);
            }
        }
    }
}
