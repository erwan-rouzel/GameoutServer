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
import java.util.Collections;

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

                    GameState gameStateForRoom = GameoutServer.getByRoom(session.roomId);
                    if (gameStateForRoom == null) {
                        log("Session does not exist. Starting new game !");
                        gameInit = startGame(session);
                    } else {
                        log("Session already exist. Adding player to existing session.");
                        gameInit = updateGame(session, gameStateForRoom);
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

    public GameInit startGame(GameSession session) {

        // TODO : optimize this computation of new key. What is done currently might not scale up well!
        int gameSessionId;
        if(GameoutServer.gameStateList.isEmpty()) {
            gameSessionId = 0;
            log("No session started yet, sessionId set to 0" + gameSessionId);
        } else {
            gameSessionId = Collections.max(Collections.list(GameoutServer.gameStateList.keys())) + 1;
            log("Determine sessionId from max:" + gameSessionId);
        }

        session.id = gameSessionId;
        GameState state = new GameState(session);
        state.status = GameStatus.RUNNING;
        GameoutServer.gameStateList.put(gameSessionId, state);

        state.registerPlayer();

        return new GameInit(session.roomId, "195.154.123.213", gameSessionId, 0, 0);
    }

    public GameInit updateGame(GameSession session, GameState state) {
        log("Adding to session " + session.id + " with roomId=" + state.roomId);

        state.registerPlayer();

        if(state.allPlayersRegistered()) {
            state.status = GameStatus.RUNNING;
        }

        return new GameInit(session.roomId, "195.154.123.213", session.id, 1, 0);
    }

    public void endGame(long sessionId) {
        GameoutServer.gameStateList.remove(sessionId);
    }
}
