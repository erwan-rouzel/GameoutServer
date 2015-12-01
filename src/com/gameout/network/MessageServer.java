package com.gameout.network;

import com.gameout.model.GameState;
import com.gameout.model.Player;
import com.gameout.model.Team;

import java.net.*;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by erwan on 21/11/2015.
 */
public class MessageServer extends AbstractServer implements Runnable {

    public MessageServer(int port) {
        super(port);
    }

    public void run() {
        DatagramSocket udpSocket;
        byte[] udpData = new byte[1024];
        byte[] sendData;

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
                DatagramPacket receivePacket = new DatagramPacket(udpData, udpData.length);
                udpSocket.receive(receivePacket);

                Player player = processMessageFromPlayer(
                        receivePacket.getData(),
                        receivePacket.getAddress()
                );

                sendData = GameoutUtils.stringToBytes("UDP is here!");

                DatagramPacket sendPacket = new DatagramPacket(
                        sendData,
                        sendData.length,
                        receivePacket.getAddress(),
                        receivePacket.getPort()
                );

                udpSocket.send(sendPacket);

            } catch(Exception e){
                log(e);
            }
        }
    }

    public static Player processMessageFromPlayer(byte[] m, InetAddress ip) throws MalformedPlayerMessageException {
        /*
        4 octets : ID Partie
        4 octets : timestamp en microsecondes
        1 octet : ID team
        1 octet : ID Joueur dans team
        1 octet : action
        1 octet : incrément
        2 octets : coordonnée X
        2 octets : coordonnée Y
        2 octets : coordonnée VX
        2 octets : coordonnée VY
        */

        if(m.length != 20) {
            throw new MalformedPlayerMessageException();
        }

        int idGame =        GameoutUtils.bytesToInt(m[0], m[1], m[2], m[3]);
        int timestamp =     GameoutUtils.bytesToInt(m[4], m[5], m[6], m[7]);
        byte idTeam =       m[8];
        byte idPlayer =     m[9];
        byte action =       m[10];
        byte increment =    m[11];
        short x =           GameoutUtils.bytesToShort(m[12], m[13]);
        short y =           GameoutUtils.bytesToShort(m[14], m[15]);
        short vx =          GameoutUtils.bytesToShort(m[16], m[17]);
        short vy =          GameoutUtils.bytesToShort(m[18], m[19]);

        // We retrive the corresponding player and update his datas
        GameState state = GameoutServer.gameStateList.get(idGame);
        Player player = state.teams[idTeam].players[idPlayer];

        player.ip = ip;
        player.x = x;
        player.y = y;
        player.vx = vx;
        player.vy = vy;

        return player;
    }
}
