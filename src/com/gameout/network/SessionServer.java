package com.gameout.network;

import com.gameout.model.*;
import com.gameout.utils.GameoutUtils;
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
                GameInit gameInit;
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

                    log(session.toString());

                    if (!GameoutServer.gameStateList.containsKey(session.id)) {
                        log("Session does not exist. Starting new game !");
                        gameInit = startGame(session);
                    } else {
                        log("Session already exist. Adding player to existing session.");
                        gameInit = updateGame(session);
                        client.close();
                    }

                    output.writeBytes(new Gson().toJson(gameInit));
                    client.close();
                }
            }
        } catch(Exception e) {
            log(e.toString());
        }
    }

    public static GameInit startGame(GameSession session) {
        GameState state = new GameState(session);
        GameoutServer.gameStateList.put(state.id, state);
        return new GameInit(0, 0);
    }

    public static GameInit updateGame(GameSession session) {
        GameState state = GameoutServer.gameStateList.get(session.id);
        state.status = GameStatus.RUNNING;
        return new GameInit(1, 0);
    }

    public static void endGame(long sessionId) {
        GameoutServer.gameStateList.remove(sessionId);
    }
}
