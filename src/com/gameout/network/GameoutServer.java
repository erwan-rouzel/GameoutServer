package com.gameout.network;

import com.gameout.model.GameSession;
import com.gameout.model.GameState;
import com.gameout.model.Player;
import com.gameout.model.Team;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

class GameoutServer {
    private static HashMap<Integer, GameState> gameStateList = new HashMap<Integer, GameState>();

    /*
    Début de partie (webservice REST JSon, TCP) :
    4 octets : ID Partie
    4 octets : timestamp complet (heure en secondes)
    1 octet : nombre de personnes équipe 1
    1 octet : nombre de personnes équipe 2
    1 octet : nombre de personnes équipe 3

    Message fin de partie pour le Host (TCP) :
    4 octets : ID Partie
    4 octets : timestamp
    3 octets : scores (entre 0 et 9)

    Message fin de partie pour les invités (TCP) :
    4 octets : ID Partie
    4 octets : timestamp
    3 octets : scores (entre 0 et 9)

    Message des joueurs vers le serveur (en binaire, UDP) :
    4 octets : ID Partie
    4 octets : timestamp en microsecondes
    1 octer : ID team
    1 octet : ID Joueur dans team
    1 octet : action
    1 octet : incrément
    2 octets : coordonnée X
    2 octets : coordonnée Y
    2 octets : coordonnée VX
    2 octets : coordonnée VY

    Message du serveur vers chaque joueur :
    4 octets : timestamp
    1 octet : incrément
    3 octets : scores (entre 0 et 9)
    2 octets : X balle
    2 octets : Y balle
    2 octets : VX balle
    2 octets : VY balle
    1 octet : nombre d'équipes
    1 octet : nombre de personnes équipe 1
    1 octet : nombre de personnes équipe 2
    1 octet : nombre de personnes équipe 3

    2 octets : X (E1, J1)
    2 octets : Y (E1, J1)
    2 octets : VX (E1, J1)
    2 octets : VY (E1, J1)
    1 octet : état J1
    ...
    2 octets : X (E1, Jn)
    2 octets : Y (E1, Jn)
    2 octets : VX (E1, Jn)
    2 octets : VY (E1, Jn)
    1 octet : état Jn

    2 octets : X (E2, J1)
    2 octets : Y (E2, J1)
    2 octets : VX (E2, J1)
    2 octets : VY (E2, J1)
    1 octet : état J1
    ...
    2 octets : X (E2, Jm)
    2 octets : Y (E2, Jm)
    2 octets : VX (E2, Jm)
    2 octets : VY (E2, Jm)
    1 octet : état Jm

    2 octets : X (E3, J1)
    2 octets : Y (E3, J1)
    2 octets : VX (E3, J1)
    2 octets : VY (E3, J1)
    1 octet : état J1
    ...
    2 octets : X (E3, Jp)
    2 octets : Y (E3, Jp)`
    2 octets : VX (E3, Jp)
    2 octets : VY (E3, Jp)
    1 octet : état Jp
    */
    public static void main(String args[]) throws Exception
    {
        ServerSocket tcpSocket = new ServerSocket(9875);
        DatagramSocket udpSocket = new DatagramSocket(9876);
        byte[] tcpData = new byte[1024];
        byte[] udpData = new byte[1024];
        byte[] sendData;
        Socket client;

        while(true)
        {
            /* Process TCP Packets */
            // Accept new TCP client
            client       = tcpSocket.accept();
            // Open output stream

            InputStream input = client.getInputStream();
            input.read(tcpData);

            DataOutputStream output = new DataOutputStream(client.getOutputStream());
            output.writeBytes("OK\n");

            String dataRead = GameoutUtils.bytesToString(tcpData);

            System.out.println("Data read : " + dataRead);

            Gson gson = new Gson();
            JsonReader reader = new JsonReader(new StringReader(dataRead));
            reader.setLenient(true);
            GameSession session = gson.fromJson(reader, GameSession.class);

            System.out.println("ID=" + session.id);
            System.out.println("timestamp=" + session.timestamp);
            System.out.println("nb_players_team1=" + session.numberOfPlayersInTeam1);
            System.out.println("nb_players_team2=" + session.numberOfPlayersInTeam2);

            if(! gameStateList.containsKey(session.id)) {
                System.out.println("Session does not exist. Starting new game !");
                startGame(session);
            } else {
                System.out.println("Session already exist. Doing nothing.");
            }

            /* Process UDP Packets */
            DatagramPacket receivePacket = new DatagramPacket(udpData, udpData.length);
            udpSocket.receive(receivePacket);


            Player player = processMessageFromPlayer(
                    receivePacket.getData(),
                    receivePacket.getAddress()
            );

            sendData = getPlayerUpdateMessage(player);

            DatagramPacket sendPacket = new DatagramPacket(
                    sendData,
                    sendData.length,
                    receivePacket.getAddress(),
                    receivePacket.getPort()
            );

            udpSocket.send(sendPacket);

            /* Update games */
            for(Map.Entry<Integer, GameState> entry : gameStateList.entrySet()) {
                Integer key = entry.getKey();
                GameState value = entry.getValue();

                updateGameState(value);
            }
        }
    }

    public static void startGame(GameSession session) {
        GameState state = new GameState(session);
        gameStateList.put(state.id, state);
    }

    public static void endGame(long sessionId) {
        gameStateList.remove(sessionId);
    }

    public static Player processMessageFromPlayer(byte[] m, InetAddress ip) {
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
        GameState state = gameStateList.get(idGame);
        Player player = state.teams[idTeam].players[idPlayer];

        player.ip = ip;
        player.x = x;
        player.y = y;
        player.vx = vx;
        player.vy = vy;

        return player;
    }

    public static byte[] getPlayerUpdateMessage(Player player) {
        /*
        8 octets : timestamp
        1 octet : incrément
        3 octets : scores (entre 0 et 9)
        2 octets : X balle
        2 octets : Y balle
        2 octets : VX balle
        2 octets : VY balle
        1 octet : nombre de personnes équipe 1
        1 octet : nombre de personnes équipe 2
        1 octet : nombre de personnes équipe 3

        2 octets : X (E1, J1)
        2 octets : Y (E1, J1)
        2 octets : VX (E1, J1)
        2 octets : VY (E1, J1)
        1 octet : état J1
        ...
        */

        ArrayList<Byte> message = new ArrayList<Byte>();
        GameState state = player.parentTeam.parentGameState;

        long timestamp = new Date().getTime();
        byte increment = 0;
        byte score1 = player.parentTeam.parentGameState.teams[0].score;
        byte score2 = player.parentTeam.parentGameState.teams[1].score;
        byte score3 = player.parentTeam.parentGameState.teams[2].score;
        short ballX = state.ball.x;
        short ballY = state.ball.y;
        short ballVX = state.ball.vx;
        short ballVY = state.ball.vy;
        byte numPlayersTeam1 = (byte) state.teams[0].players.length;
        byte numPlayersTeam2 = (byte) state.teams[1].players.length;
        byte numPlayersTeam3 = (byte) state.teams[2].players.length;

        byte[] buffer;

        buffer = GameoutUtils.longToBytes(timestamp);
        message.add(buffer[0]);
        message.add(buffer[1]);
        message.add(buffer[2]);
        message.add(buffer[3]);
        message.add(buffer[4]);
        message.add(buffer[5]);
        message.add(buffer[6]);
        message.add(buffer[7]);

        message.add(increment);
        message.add(score1);
        message.add(score2);
        message.add(score3);

        buffer = GameoutUtils.shortToBytes(ballX);
        message.add(buffer[0]);
        message.add(buffer[1]);

        buffer = GameoutUtils.shortToBytes(ballY);
        message.add(buffer[0]);
        message.add(buffer[1]);

        buffer = GameoutUtils.shortToBytes(ballVX);
        message.add(buffer[0]);
        message.add(buffer[1]);

        buffer = GameoutUtils.shortToBytes(ballVY);
        message.add(buffer[0]);
        message.add(buffer[1]);

        message.add(numPlayersTeam1);
        message.add(numPlayersTeam2);
        message.add(numPlayersTeam3);

        for(Team t: state.teams) {
            for(Player p: t.players) {
                buffer = GameoutUtils.shortToBytes(p.x);
                message.add(buffer[0]);
                message.add(buffer[1]);

                buffer = GameoutUtils.shortToBytes(p.y);
                message.add(buffer[0]);
                message.add(buffer[1]);

                buffer = GameoutUtils.shortToBytes(p.vx);
                message.add(buffer[0]);
                message.add(buffer[1]);

                buffer = GameoutUtils.shortToBytes(p.vy);
                message.add(buffer[0]);
                message.add(buffer[1]);

                message.add((byte) p.state.ordinal());
            }
        }

        byte[] result = new byte[message.size()];
        for(int i = 0; i < message.size(); i++) {
            result[i] = message.get(i);
        }
        return result;
    }

    public static void updateGameState(GameState state) {
        //TODO
    }
}
