package com.gameout.network;

import com.gameout.model.GameState;
import com.gameout.model.Player;

import java.net.*;

/**
 * Created by erwan on 21/11/2015.
 */
public class MessageServer extends AbstractServer implements Runnable {

    public MessageServer(int port) {
        super(port);
    }

    public void run() {
        DatagramSocket udpSocket;
        byte[] udpData = new byte[24];
        byte[] sendData;
        DatagramPacket receivePacket = new DatagramPacket(udpData, udpData.length);

        log(this.getClass().getSimpleName() + " now listening on port " + port + "...");

        try {
            udpSocket = new DatagramSocket(port);
        } catch(SocketException e) {
            log(e);
            System.exit(0);
            return;
        }

        while(true) {
            /* Process UDP Packets */

            try {
                log("Waiting for packet...");
                udpSocket.receive(receivePacket);

                Player player = GameoutServerHelper.processMessageFromPlayer(
                        receivePacket.getData(),
                        receivePacket.getAddress()
                );

                log(player.toString());

                sendData = GameoutServerHelper.getGameStateMessage(player.parentTeam.parentGameState);

                DatagramPacket sendPacket = new DatagramPacket(
                        sendData,
                        sendData.length,
                        receivePacket.getAddress(),
                        receivePacket.getPort()
                );

                log(player.parentTeam.parentGameState.toString());

                udpSocket.send(sendPacket);
            } catch(Exception e){
                log(e);
            }
        }
    }

}
