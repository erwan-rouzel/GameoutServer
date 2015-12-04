package com.gameout.network;

import com.gameout.model.GameState;
import com.gameout.model.Player;
import com.gameout.model.Team;

import java.net.*;
import java.text.SimpleDateFormat;
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

                Player player = processMessageFromPlayer(
                        receivePacket.getData(),
                        receivePacket.getAddress()
                );

                Date now = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
                sendData = GameoutUtils.stringToBytes("UDP message back - " + sdf.format(now).trim());

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
        8 octets : timestamp en microsecondes
        1 octet : ID team
        1 octet : ID Joueur dans team
        1 octet : action
        1 octet : incrément
        2 octets : coordonnée X
        2 octets : coordonnée Y
        2 octets : coordonnée VX
        2 octets : coordonnée VY
        */

        if(m.length != 24) {
            throw new MalformedPlayerMessageException(m);
        }

        int idGame =        GameoutUtils.bytesToInt(m[0], m[1], m[2], m[3]);
        long timestamp =     GameoutUtils.bytesToLong(m[4], m[5], m[6], m[7], m[8], m[9], m[10], m[11]);
        byte idTeam =       m[12];
        byte idPlayer =     m[13];
        byte action =       m[14];
        byte increment =    m[15];
        short x =           GameoutUtils.bytesToShort(m[16], m[17]);
        short y =           GameoutUtils.bytesToShort(m[18], m[19]);
        short vx =          GameoutUtils.bytesToShort(m[20], m[21]);
        short vy =          GameoutUtils.bytesToShort(m[22], m[23]);

        // We retrive the corresponding player and update his datas
        GameState state = GameoutServer.gameStateList.get(idGame);
        System.out.println("idGame=" + idGame);
        System.out.println("idTeam=" + idTeam);
        System.out.println("idPlayer=" + idPlayer);
        Team team = state.teams[idTeam];
        Player player = team.players[idPlayer];

        player.ip = ip;
        player.x = x;
        player.y = y;
        player.vx = vx;
        player.vy = vy;

        return player;
    }
}
