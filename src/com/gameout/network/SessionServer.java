package com.gameout.network;

import com.gameout.model.GameSession;
import com.gameout.model.GameState;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by erwan on 21/11/2015.
 */
public class SessionServer extends AbstractServer implements Runnable {

    public SessionServer(int port) {
        super(port);
    }

    public void run() {
        ServerSocket tcpSocket;
        byte[] tcpData = new byte[1024];
        Socket client;

        try {
            tcpSocket = new ServerSocket(port);
        } catch(Exception e) {
            log(e.toString());
            System.exit(0);
            return;
        }

        log(this.getClass().getSimpleName() + " now listening on port " + port + "...");

        try {
            while (true) {
                /* Process TCP Packets */
                // Open output stream
                // Accept new TCP client
                client = tcpSocket.accept();
                InputStream input = client.getInputStream();
                DataOutputStream output = new DataOutputStream(client.getOutputStream());

                Thread.sleep(1000);
                if (input.read(tcpData) > 0) {
                    log("Reading data received");
                    String dataRead = GameoutUtils.bytesToString(tcpData);
                    log("Data read : " + dataRead);

                    Gson gson = new Gson();
                    JsonReader reader = new JsonReader(new StringReader(dataRead));
                    reader.setLenient(true);
                    GameSession session = gson.fromJson(reader, GameSession.class);

                    log("ID=" + session.id);
                    log("timestamp=" + session.timestamp);
                    log("nb_players_team1=" + session.numberOfPlayersInTeam1);
                    log("nb_players_team2=" + session.numberOfPlayersInTeam2);

                    if (!GameoutServer.gameStateList.containsKey(session.id)) {
                        log("Session does not exist. Starting new game !");
                        output.writeBytes("OK\n");
                        startGame(session);
                        client.close();
                    } else {
                        output.writeBytes("KO\n");
                        log("Session already exist. Doing nothing.");
                        client.close();
                    }
                }
            }
        } catch(Exception e) {
            log(e.toString());
        }
    }

    public static void startGame(GameSession session) {
        GameState state = new GameState(session);
        GameoutServer.gameStateList.put(state.id, state);
    }

    public static void endGame(long sessionId) {
        GameoutServer.gameStateList.remove(sessionId);
    }
}
